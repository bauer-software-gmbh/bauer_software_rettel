package de.bauersoft.data.entities.tour.tour;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tour_entry", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "tour_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TourEntry extends AbstractEntity
{
    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "tour_id", referencedColumnName = "id", nullable = false)
    private Tour tour;

    @Column(length = 256)
    private String timeWindow;

    @Column(length = 1024)
    private String note;

//    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "info_id")
//    private TourInformation info;
}

