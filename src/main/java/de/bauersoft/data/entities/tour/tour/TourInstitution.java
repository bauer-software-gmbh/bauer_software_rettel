package de.bauersoft.data.entities.tour.tour;

import de.bauersoft.components.container.ContainerID;
import de.bauersoft.data.entities.institution.Institution;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tour_institutions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TourInstitution implements ContainerID<TourInstitutionKey>
{
    @EmbeddedId
    private TourInstitutionKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("tourId")
    @JoinColumn(name = "tour_id", referencedColumnName = "id")
    private Tour tour;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("institutionId")
    @JoinColumn(name = "institution_id", referencedColumnName = "id")
    private Institution institution;

//    @Column(nullable = false)
//    private LocalDate contractStart;
//
//    @Column(nullable = true, columnDefinition = "DATE default '9999-12-31'")
//    private LocalDate contractEnd;

    @Column(nullable = false)
    private LocalTime expectedArrivalTime;

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
//    @Column(length = 10485760) //10 MB
//    private byte[] temperatureImage;

    private String temperature;

    private LocalDateTime validationDateTime;

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
//    @Column(length = 10485760) //10 MB
//    private byte[] signature;
}
