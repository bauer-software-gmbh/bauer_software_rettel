package de.bauersoft.data.entities.institution;

import de.bauersoft.data.entities.field.Field;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "institution_fields")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InstitutionField
{
    @EmbeddedId
    private InstitutionFieldKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("institutionId")
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("fieldId")
    @JoinColumn(name = "field_id")
    private Field field;

    @Column(name = "child_count", columnDefinition = "integer default 0")
    private int childCount;

    @Override
    public String toString()
    {
        return "InstitutionFields{" +
                "id=" + id +
                ", field=" + field +
                ", childCount=" + childCount +
                '}';
    }
}
