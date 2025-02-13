package de.bauersoft.views.institution.institutionFields;

import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.variant.Variant;
import lombok.Getter;

import java.util.Map;

@Getter
public class InstitutionFieldDialogDataContainer
{
    private final Map<Variant, Integer> variantAmountMap;
    private final Map<Allergen, Integer> allergenAmountMap;
    private final Map<Course, Double> multiplierMap;

    public InstitutionFieldDialogDataContainer(Map<Variant, Integer> variantAmountMap, Map<Allergen, Integer> allergenAmountMap, Map<Course, Double> multiplierMap)
    {
        this.variantAmountMap = variantAmountMap;
        this.allergenAmountMap = allergenAmountMap;
        this.multiplierMap = multiplierMap;
    }
}
