package de.bauersoft.data.entities.tour.tour;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.tour.driver.Driver;
import de.bauersoft.data.entities.tour.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tour", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = {"name"})
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tour extends AbstractEntity
{
    @Column(nullable = false, length = 64)
    private String name;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false, columnDefinition = "INTEGER default 1")
    private int requiredDrivers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;

    private LocalDate drivesUntil;

    @ManyToOne(fetch = FetchType.EAGER)
    private Driver coDriver;

    private LocalDate coDrivesUntil;

    @OneToMany(mappedBy = "tour", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<TourInstitution> institutions = new HashSet<>();

    @ManyToMany(mappedBy = "driveableTours", fetch = FetchType.EAGER)
    private Set<Driver> possibleDrivers = new HashSet<>();

    @Column(nullable = false, columnDefinition = "TINYINT default 0")
    private boolean holidayMode = false;
}
