package de.bauersoft.data.entities.institutionClosingTime;

import de.bauersoft.components.container.ContainerID;
import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "institution_closing_times")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionClosingTime extends AbstractEntity implements ContainerID<Long>
{
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 64)
    private String header;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_field_id", nullable = false)
    private InstitutionField institutionField;

    @Override
    public String toString()
    {
        return "InstitutionClosingTime{" +
                "id=" + getId() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", institutionField=" + institutionField.getId() +
                '}';
    }
}
