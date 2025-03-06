package de.bauersoft.data.entities.tour;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.driver.Driver;
import de.bauersoft.data.entities.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false, columnDefinition = "INTEGER default 1")
    private int requiredDrivers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", referencedColumnName = "id", nullable = false)
    private Driver driver;

    @Column(nullable = false)
    private LocalDate drivesUntil;

    @ManyToOne(fetch = FetchType.EAGER)
    private Driver coDriver;

    @Column(nullable = false)
    private LocalDate coDrivesUntil;

    @OneToMany(mappedBy = "tour", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
    private Set<TourInstitution> institutions;

    @ManyToMany(mappedBy = "driveableTours", fetch = FetchType.EAGER)
    private Set<Driver> possibleDrivers;
}
