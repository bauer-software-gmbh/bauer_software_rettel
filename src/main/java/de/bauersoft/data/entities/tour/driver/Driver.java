package de.bauersoft.data.entities.tour.driver;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    public boolean canDriveTour(Tour tour)
    {
        return driveableTours.contains(tour);
    }

    public boolean canDriveTour(Long tourId)
    {
        return driveableTours.stream().anyMatch(tour -> tour.getId().equals(tourId));
    }
}
