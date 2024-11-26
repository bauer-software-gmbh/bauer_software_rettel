package de.bauersoft.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "unit")
public class Unit extends AbstractEntity {

	private String name;
	private String shorthand;
	private float parent_factor;
	
	@OneToOne(optional = true,targetEntity = Unit.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_id",table = "unit", referencedColumnName = "id")
	private Unit parent;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShorthand() {
		return shorthand;
	}
	public void setShorthand(String shorthand) {
		this.shorthand = shorthand;
	}
	
	public Unit getParent() {
		return parent;
	}
	public void setParent(Unit parent) {
		this.parent = parent;
	}
	public float getParent_factor() {
		return parent_factor;
	}
	public void setParent_factor(float parent_factor) {
		this.parent_factor = parent_factor;
	}
	
}
