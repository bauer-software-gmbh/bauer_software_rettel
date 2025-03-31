package de.bauersoft.data.entities.tour.driver;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "driver", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = {"user_id"})
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Driver extends AbstractEntity
{
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "driveableTours",
            joinColumns = @JoinColumn(name = "driver_id"),
            inverseJoinColumns = @JoinColumn(name = "tour_id")
    )
    private Set<Tour> driveableTours = new HashSet<>();
}
