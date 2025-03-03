package de.bauersoft.data.entities.fieldMultiplier;

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
public class FieldMultiplierKey implements Serializable
{
    @Column(nullable = false)
    private Long fieldId;

    @Column(nullable = false)
    private Long courseId;

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        FieldMultiplierKey that = (FieldMultiplierKey) o;
        return Objects.equals(fieldId, that.fieldId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(fieldId, courseId);
    }

    @Override
    public String toString()
    {
        return "FieldMultiplierKey{" +
                "fieldId=" + fieldId +
                ", courseId=" + courseId +
                '}';
    }
}
