package de.bauersoft.data.entities.institution;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "institution")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Institution extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String customerId;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean localMultiplier;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Address.class)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "institution_users",
            joinColumns = @JoinColumn(name = "institution_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "institution", fetch = FetchType.EAGER)
    private Set<InstitutionField> institutionFields = new HashSet<>();

    @OneToMany(mappedBy = "institution", fetch = FetchType.EAGER)
    private Set<InstitutionMultiplier> institutionMultipliers = new HashSet<>();

    @Override
    public String toString()
    {
        return "Institution{" +
                "users=" + users +
                ", address=" + address +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean useLocalMultiplier()
    {
        return localMultiplier;
    }

    public void setUseLocalMultiplier(boolean localMultiplier)
    {
        this.localMultiplier = localMultiplier;
    }
}
