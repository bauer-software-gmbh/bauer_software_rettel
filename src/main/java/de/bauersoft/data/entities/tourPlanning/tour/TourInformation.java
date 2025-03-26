package de.bauersoft.data.entities.tourPlanning.tour;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tour_information")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TourInformation {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 255)
    private String timeWindow;

    @Column(length = 1000)
    private String notes;
}


