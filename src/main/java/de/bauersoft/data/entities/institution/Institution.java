package de.bauersoft.data.entities.institution;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "institution")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Institution extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(length = 256)
    private String customerId;

    @Column(nullable = false, columnDefinition = "TIME default '00:00:05'")
    private LocalTime orderStart = LocalTime.of(0, 5);

    @Column(nullable = false, columnDefinition = "TIME default '08:00:00'")
    private LocalTime orderEnd = LocalTime.of(8, 8);

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Address.class)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "institution_users",
            joinColumns = @JoinColumn(name = "institution_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "institution", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
    private Set<InstitutionField> institutionFields = new HashSet<>();

}
