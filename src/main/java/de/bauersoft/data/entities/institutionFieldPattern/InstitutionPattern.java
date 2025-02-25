package de.bauersoft.data.entities.institutionFieldPattern;

import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.components.container.ContainerID;
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
public class InstitutionPattern implements ContainerID<InstitutionPatternKey>
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

    @Override
    public String toString()
    {
        return "InstitutionPattern{" +
                "id=" + id +
                ", pattern=" + pattern +
                ", amount=" + amount +
                '}';
    }
}
