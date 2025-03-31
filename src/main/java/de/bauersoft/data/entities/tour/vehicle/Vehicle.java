package de.bauersoft.data.entities.tour.vehicle;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "vehicle", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = {"license_plate"})
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Vehicle extends AbstractEntity
{
    @Column(nullable = false, length = 16)
    private String licensePlate;

    @Column(length = 1024)
    private String typeDescription;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.EAGER)
    private Set<VehicleDowntime> downtimes;

}
