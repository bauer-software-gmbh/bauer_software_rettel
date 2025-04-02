package de.bauersoft.data.entities.tour.tour;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tour_locations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TourLocation extends AbstractEntity
{

    @ManyToOne
    @JoinColumn(name = "tour_id", referencedColumnName = "id")
    private Tour tour;

    @Column(nullable = false)
    private Double latitude; // Breitengrad (GPS-Koordinate)

    @Column(nullable = false)
    private Double longitude; // Längengrad (GPS-Koordinate)

    @Column(nullable = false)
    private LocalDateTime timestamp; // Zeitpunkt der Standortaktualisierung

    @Column(length = 2)
    private String markerIcon; // Icon für den Marker auf der Karte


    @Override
    public String toString()
    {
        return "TourLocation{" +
                "tour=" + tour.getId() +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                '}';
    }
}
