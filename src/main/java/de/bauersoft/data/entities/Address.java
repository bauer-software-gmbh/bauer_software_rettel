package de.bauersoft.data.entities;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Table;

@Entity
@Table(name = "address")
public class Address extends AbstractGroupByEntity<Address>{
	
	private String street;
	
	@Column(name = "house_number")
	private String houseNumber;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	private String city;
	
	@OneToMany(mappedBy = "address",fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
	private Set<Institution> institutions;
	
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Set<Institution> getInstitutions() {
		return institutions;
	}

	public void setInstitutions(Set<Institution> institutions) {
		this.institutions = institutions;
	}
}
