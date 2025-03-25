package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DriverDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate drivesUntil; // Neu: Das "drivesUntil" aus der Tour

    public DriverDTO(Driver driver, LocalDate drivesUntil) {
        this.id = driver.getId();
        this.name = (driver.getUser() != null) ? driver.getUser().getName() : "Unbekannter Fahrer";
        this.email = (driver.getUser() != null) ? driver.getUser().getEmail() : "Keine E-Mail";
        this.drivesUntil = drivesUntil;
    }
}

