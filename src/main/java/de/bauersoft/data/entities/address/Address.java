package de.bauersoft.data.entities.address;

import de.bauersoft.data.entities.AbstractGroupByEntity;
import de.bauersoft.data.entities.institution.Institution;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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
    @Column(nullable = false, length = 64)
    private String street;

    @Column(nullable = false, length = 8)
    private String number;

    @Column(nullable = false, length = 5)
    private String postal;

    @Column(nullable = false, length = 64)
    private String city;

    @OneToMany(mappedBy = "address", fetch = FetchType.EAGER)
    private Set<Institution> institutions = new HashSet<>();

}
