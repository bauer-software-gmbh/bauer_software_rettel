package de.bauersoft.data.entities.institution;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InstitutionFieldKey implements Serializable
{
    @Column(nullable = false)
    private Long institutionId;

    @Column(nullable = false)
    private Long fieldId;

    @Override
    public int hashCode()
    {
        return Objects.hash(institutionId, fieldId);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        InstitutionFieldKey that = (InstitutionFieldKey) o;
        return Objects.equals(institutionId, that.institutionId) && Objects.equals(fieldId, that.fieldId);
    }

    @Override
    public String toString()
    {
        return "InstitutionFieldsKey{" +
                "institutionId=" + institutionId +
                ", fieldId=" + fieldId +
                '}';
    }
}
