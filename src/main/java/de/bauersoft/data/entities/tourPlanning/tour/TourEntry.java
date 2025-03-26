package de.bauersoft.data.entities.tourPlanning.tour;

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
public class TourEntry {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "info_id")
    private TourInformation info;
}

