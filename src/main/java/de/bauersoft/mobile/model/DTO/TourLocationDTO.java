package de.bauersoft.mobile.model.DTO;

import java.time.LocalDateTime;

public class TourLocationDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private Long tourId;
    private String markerIcon;

    public TourLocationDTO() {}

    public TourLocationDTO(Long id, Double latitude, Double longitude, LocalDateTime timestamp, Long tourId, String markerIcon) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.tourId = tourId;
        this.markerIcon = markerIcon;
    }

    // Getter & Setter
    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarkerIcon() { return markerIcon; }
    public void setMarkerIcon(String markerIcon) { this.markerIcon = markerIcon; }
}
