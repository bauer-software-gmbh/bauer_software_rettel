package de.bauersoft.data.entities.institution;

import de.bauersoft.data.entities.allergen.Allergen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "institution_allergens")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionAllergen
{
    @EmbeddedId
    private InstitutionAllergenKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("institutionFieldId")
    @JoinColumn(name = "institution_field_id", referencedColumnName = "id")
    private InstitutionField institutionField;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("allergenId")
    @JoinColumn(name = "allergen_id", referencedColumnName = "id")
    private Allergen allergen;

    @Column(columnDefinition = "integer default 0")
    private int amount;
}
