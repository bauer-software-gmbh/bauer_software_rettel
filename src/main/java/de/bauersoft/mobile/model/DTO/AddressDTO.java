package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.address.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private String street;
    private String number;
    private String city;
    private String postal;

    public AddressDTO(Address address) {
        this.street = address.getStreet();
        this.number = address.getNumber();
        this.city = address.getCity();
        this.postal = address.getPostal();
    }
}
