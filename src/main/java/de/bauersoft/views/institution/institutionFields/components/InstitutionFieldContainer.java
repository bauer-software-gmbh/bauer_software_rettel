package de.bauersoft.views.institution.institutionFields.components;

import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institution.*;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.views.institution.institutionFields.components.allergen.AllergenContainer;
import de.bauersoft.views.institution.institutionFields.components.multiplier.MultiplierContainer;
import de.bauersoft.views.institution.institutionFields.components.pattern.PatternContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class InstitutionFieldContainer
{
    private final InstitutionField institutionField;

    private final Map<Pattern, PatternContainer> patternContainers;
    private final Map<Course, MultiplierContainer> multiplierContainers;
    private final Map<Allergen, AllergenContainer> allergenContainers;

    public InstitutionFieldContainer(InstitutionField institutionField)
    {
        this.institutionField = institutionField;

        patternContainers = new HashMap<>();
        multiplierContainers = new HashMap<>();
        allergenContainers = new HashMap<>();
    }

    public Set<InstitutionPattern> getInstitutionPatterns()
    {
//        Objects.requireNonNull(institutionField.getId(),
//                "You must save the InstitutionField first before you can generate InstitutionPatterns for it.");
//
//        Set<InstitutionPattern> institutionPatterns = new HashSet<>();
//        for(PatternContainer patternContainer : patternContainers.values())
//        {
//            InstitutionPatternKey key = new InstitutionPatternKey();
//            key.setInstitutionFieldId(institutionField.getId());
//            key.setPatternId(patternContainer.getPattern().getId());
//
//            InstitutionPattern institutionPattern = new InstitutionPattern();
//            institutionPattern.setId(key);
//            institutionPattern.setInstitutionField(institutionField);
//            institutionPattern.setPattern(patternContainer.getPattern());
//
//            institutionPattern.setAmount(patternContainer.getAmount());
//
//            institutionPatterns.add(institutionPattern);
//        }

        return new HashSet<>();
    }

    public Set<InstitutionMultiplier> getInstitutionMultipliers()
    {
        Objects.requireNonNull(institutionField.getId(),
                "You must save the InstitutionField first before you can generate InstitutionMultipliers for it.");

//        Set<InstitutionMultiplier> institutionMultipliers = new HashSet<>();
//        for(MultiplierContainer multiplierContainer : multiplierContainers.values())
//        {
//            InstitutionMultiplierKey key = new InstitutionMultiplierKey();
//            key.setInstitutionFieldId(institutionField.getId());
//            key.setCourseId(multiplierContainer.getCourse().getId());
//
//            InstitutionMultiplier institutionMultiplier = new InstitutionMultiplier();
//            institutionMultiplier.setId(key);
//            institutionMultiplier.setInstitutionField(institutionField);
//            institutionMultiplier.setCourse(multiplierContainer.getCourse());
//
//            institutionMultiplier.setMultiplier(multiplierContainer.getMultiplier());
//
//            institutionMultipliers.add(institutionMultiplier);
//        }
//
//        return institutionMultipliers;
        return new HashSet<>();
    }

    public Set<InstitutionAllergen> getInstitutionAllergens()
    {
        Objects.requireNonNull(institutionField.getId(),
                "You must save the InstitutionField first before you can generate InstitutionAllergens for it.");

//        Set<InstitutionAllergen> institutionAllergens = new HashSet<>();
//        for(AllergenContainer allergenContainer : allergenContainers.values())
//        {
//            System.out.println(allergenContainer.getAllergen() + " - " + allergenContainer.getAmount() + " - " + allergenContainer.isDisable());
//            if(allergenContainer.isDisable()) continue;
//
//            InstitutionAllergenKey key = new InstitutionAllergenKey();
//            key.setInstitutionFieldId(institutionField.getId());
//            key.setAllergenId(allergenContainer.getAllergen().getId());
//
//            InstitutionAllergen institutionAllergen = new InstitutionAllergen();
//            institutionAllergen.setId(key);
//            institutionAllergen.setInstitutionField(institutionField);
//            institutionAllergen.setAllergen(allergenContainer.getAllergen());
//
//            institutionAllergen.setAmount(allergenContainer.getAmount());
//
//            institutionAllergens.add(institutionAllergen);
//        }

        return new HashSet<>();
    }
}
