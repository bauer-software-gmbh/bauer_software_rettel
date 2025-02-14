package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.data.entities.allergen.Allergen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllergenContainer
{
    private Allergen allergen;
    private Integer amount;

    private boolean disable;
}
