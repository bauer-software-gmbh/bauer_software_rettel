package de.bauersoft.data.entities.user;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_locations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLocation extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Nutzer, zu dem die Standortdaten gehören

    @Column(nullable = false)
    private Double latitude; // Breitengrad (GPS-Koordinate)

    @Column(nullable = false)
    private Double longitude; // Längengrad (GPS-Koordinate)

    @Column(nullable = false)
    private LocalDateTime timestamp; // Zeitpunkt der Standortaktualisierung

    @Override
    public String toString() {
        return "UserLocation{" +
                "userId=" + user.getId() +
                ", name='" + user.getName() + '\'' +
                ", email='" + user.getEmail() + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                '}';
    }
}
