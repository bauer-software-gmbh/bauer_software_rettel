package de.bauersoft.data.entities.institutionFieldAllergen;

import de.bauersoft.components.container.ContainerID;
import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "institution_allergen_group")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionAllergen extends AbstractEntity implements ContainerID<Long>
{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_field_id", referencedColumnName = "id")
    private InstitutionField institutionField;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "institution_allergen",
            joinColumns = @JoinColumn(name = "institution_allergen_group_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id"))
    private Set<Allergen> allergens = new HashSet<>();
}
