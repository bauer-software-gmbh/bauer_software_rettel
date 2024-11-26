package de.bauersoft.data.entities;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "institution")
public class Institution extends AbstractEntity {
	
	private String name;
	private String description ="";
	
	@ManyToOne(optional = true,targetEntity = Address.class,fetch = FetchType.EAGER)
	private Address address;
	
	@Transient
	private Set<InstitutionFields> fields;

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
	@JoinTable(name ="institutution_users", 
			joinColumns =  @JoinColumn(name="institution_id"), 
			inverseJoinColumns = @JoinColumn(name ="user_id"))
	private Set<User> users;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}


	public Set<InstitutionFields> getFields() {
		return fields;
	}

	public void setFields(Set<InstitutionFields> fields) {
		this.fields = fields;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
}
