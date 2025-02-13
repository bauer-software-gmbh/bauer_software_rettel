package de.bauersoft.data.entities.institution;

import de.bauersoft.data.entities.pattern.Pattern;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "institution_patterns")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionPattern
{
    @EmbeddedId
    private InstitutionPatternKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("institutionFieldId")
    @JoinColumn(name = "institution_field_id", referencedColumnName = "id")
    private InstitutionField institutionField;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("patternId")
    @JoinColumn(name = "pattern_id", referencedColumnName = "id")
    private Pattern pattern;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int amount;
}
