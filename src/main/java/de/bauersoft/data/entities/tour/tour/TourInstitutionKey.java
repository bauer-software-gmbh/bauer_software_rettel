package de.bauersoft.data.entities.tour.tour;

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
public class TourInstitutionKey implements Serializable
{
    @Column(nullable = false)
    private Long tourId;

    @Column(nullable = false)
    private Long institutionId;

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        TourInstitutionKey that = (TourInstitutionKey) o;
        return Objects.equals(tourId, that.tourId) && Objects.equals(institutionId, that.institutionId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(tourId, institutionId);
    }
}
