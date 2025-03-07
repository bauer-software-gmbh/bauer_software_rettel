package de.bauersoft.data.entities.institutionField;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplier;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPattern;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "institution_fields", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = {"institution_id", "field_id"})
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionField extends AbstractEntity
{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "field_id")
    private Field field;

    @OneToMany(mappedBy = "institutionField", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<InstitutionMultiplier> institutionMultipliers = new HashSet<>();

    @OneToMany(mappedBy = "institutionField", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<InstitutionAllergen> institutionAllergens = new HashSet<>();

    @OneToMany(mappedBy = "institutionField", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<InstitutionPattern> institutionPatterns = new HashSet<>();

//    @OneToMany(mappedBy = "institutionField", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
//    private Set<InstitutionClosingTime> institutionClosingTimes = new HashSet<>();

//    public boolean isClosed()
//    {
//        return institutionClosingTimes.stream().anyMatch(closing ->
//        {
//            return LocalDate.now().isAfter(closing.getStartDate()) && LocalDate.now().isBefore(closing.getEndDate());
//        });
//    }

    @Override
    public String toString()
    {
        return "InstitutionField{" +
                "field=" + field +
                ", institutionMultipliers=" + institutionMultipliers +
                ", institutionAllergens=" + institutionAllergens +
                ", institutionPatterns=" + institutionPatterns +
                '}';
    }
}
