package de.bauersoft.data.entities;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "field")
public class Field extends AbstractEntity {

	private String name;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "field",fetch = FetchType.EAGER)
	private Set<InstitutionFields> institution;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<InstitutionFields> getInstitution() {
		return institution;
	}

	public void setInstitution(Set<InstitutionFields> institution) {
		this.institution = institution;
	}

}
