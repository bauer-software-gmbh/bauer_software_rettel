package de.bauersoft.data.entities.tour;

import de.bauersoft.data.entities.institution.Institution;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tour_institutions")
public class TourInstitution
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

    @Column(nullable = false)
    private LocalDate contractStart;

    @Column(nullable = true, columnDefinition = "DATE default 31.12.9999")
    private LocalDate contractEnd;

    @Column(nullable = false)
    private LocalTime expectedArrivalTime;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, length = 10485760) //10 MB
    private byte[] temperatureImage;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, length = 10485760) //10 MB
    private byte[] signature;

    @Column(nullable = false)
    private LocalDateTime validationTime;
}
