package de.bauersoft.data.entities.tourPlanning.vehicle;

import de.bauersoft.components.container.ContainerID;
import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_downtime")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleDowntime extends AbstractEntity implements ContainerID<Long>
{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private Vehicle vehicle;

    @Column(length = 64)
    private String header;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;
}
