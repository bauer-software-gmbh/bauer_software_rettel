package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.institution.Institution;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitutionDTO {
    private Long id;
    private String name;

    public InstitutionDTO(Institution institution) {
        if (institution == null) {
            System.out.println("🚨 Institution ist NULL!");
            this.id = -1L;  // Dummy-ID setzen
            this.name = "Unbekannte Institution";
        } else {
            this.id = (institution.getId() != null) ? institution.getId() : -1L;
            this.name = (institution.getName() != null) ? institution.getName() : "Unbenannt";
        }
    }
}

