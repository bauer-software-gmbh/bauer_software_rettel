package de.bauersoft.data.entities.institutionField;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionFieldKey implements Serializable
{
    @Column(nullable = false)
    private Long institutionId;

    @Column(nullable = false)
    private Long fieldId;

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
    public int hashCode()
    {
        return Objects.hash(institutionId, fieldId);
    }

    @Override
    public String toString()
    {
        return "InstitutionFieldKey{" +
                "institutionId=" + institutionId +
                ", fieldId=" + fieldId +
                '}';
    }
}
