package de.bauersoft.data.entities.institution;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.field.Field;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "child_count", columnDefinition = "integer default 0")
    private int childCount;

    @OneToMany(mappedBy = "institutionField", fetch = FetchType.EAGER)
    private Set<InstitutionMultiplier> institutionMultipliers = new HashSet<>();

    @OneToMany(mappedBy = "institutionField", fetch = FetchType.EAGER)
    private Set<InstitutionAllergen> institutionAllergens = new HashSet<>();

    @OneToMany(mappedBy = "institutionField", fetch = FetchType.EAGER)
    private Set<InstitutionPattern> institutionPatterns = new HashSet<>();
}
