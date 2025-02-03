package de.bauersoft.data.entities.address;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.AbstractGroupByEntity;
import de.bauersoft.data.entities.institution.Institution;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "address", indexes =
        {
                @Index(name = "idx_unique_street_number_postal_city",
                        columnList = "street, number, postal, city",
                        unique = true),
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Address extends AbstractGroupByEntity<Address>
{
    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String postal;

    @Column(nullable = false)
    private String city;

    @OneToMany(mappedBy = "address", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<Institution> institutions;

}
