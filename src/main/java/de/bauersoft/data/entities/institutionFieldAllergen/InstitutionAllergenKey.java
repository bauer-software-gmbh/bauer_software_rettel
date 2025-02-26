package de.bauersoft.data.entities.institutionFieldAllergen;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionAllergenKey
{
    @Column(nullable = false)
    private Long institutionFieldId;

    @Column(nullable = false)
    private Long allergenId;

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        InstitutionAllergenKey that = (InstitutionAllergenKey) o;
        return Objects.equals(institutionFieldId, that.institutionFieldId) && Objects.equals(allergenId, that.allergenId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(institutionFieldId, allergenId);
    }

    @Override
    public String toString()
    {
        return "InstitutionAllergenKey{" +
                "institutionFieldId=" + institutionFieldId +
                ", allergenId=" + allergenId +
                '}';
    }
}
