package de.bauersoft.services.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.tour.LatLngPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import software.xdev.vaadin.maps.leaflet.basictypes.LLatLng;
import software.xdev.vaadin.maps.leaflet.registry.LComponentManagementRegistry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteService
{

    private static final String API_KEY = "5b3ce3597851110001cf624892fdb51433e340c183fdb69668f3722f"; // ⛔ Ersetzen!
    private static final String ROUTE_URL = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

    public static List<LLatLng> fetchRoute(List<LatLngPoint> points, LComponentManagementRegistry registry) throws Exception
    {
        JSONArray coordinates = new JSONArray();

        for(LatLngPoint point : points)
        {
            JSONArray coord = new JSONArray();
            coord.put(point.lng); // [lng, lat] → GeoJSON-Konvention
            coord.put(point.lat);
            coordinates.put(coord);
        }

        JSONObject body = new JSONObject();
        body.put("coordinates", coordinates);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openrouteservice.org/v2/directions/driving-car/geojson"))
                .header("Authorization", API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject geojson = new JSONObject(response.body());

        if (!geojson.has("features")) {
            throw new RuntimeException("Routing API returned no features: " + response.body());
        }

        JSONArray coords = geojson
                .getJSONArray("features")
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONArray("coordinates");


        List<LLatLng> routePoints = new ArrayList<>();
        for(int i = 0; i < coords.length(); i++)
        {
            JSONArray coord = coords.getJSONArray(i);
            double lng = coord.getDouble(0);
            double lat = coord.getDouble(1);
            routePoints.add(new LLatLng(registry, lat, lng));
        }

        return routePoints;
    }
}

