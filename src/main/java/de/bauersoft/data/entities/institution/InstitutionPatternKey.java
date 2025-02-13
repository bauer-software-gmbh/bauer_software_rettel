package de.bauersoft.data.entities.institution;

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
public class InstitutionPatternKey
{
    @Column(nullable = false)
    private Long institutionFieldId;

    @Column(nullable = false)
    private Long patternId;

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        InstitutionPatternKey that = (InstitutionPatternKey) o;
        return Objects.equals(institutionFieldId, that.institutionFieldId) && Objects.equals(patternId, that.patternId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(institutionFieldId, patternId);
    }
}
