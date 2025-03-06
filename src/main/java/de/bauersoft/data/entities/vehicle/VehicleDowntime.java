package de.bauersoft.data.entities.vehicle;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_downtime")
public class VehicleDowntime extends AbstractEntity
{
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private Vehicle vehicle;
}
