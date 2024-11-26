package de.bauersoft.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "institution_fields")
public class InstitutionFields {
	@EmbeddedId
	private InstitutionFieldsKey id;
	
	@ManyToOne
	@MapsId("fieldId")
	@JoinColumn(name = "field_id")
	private Field field;
	
	@Column(name = "child_count")
	private int childCount;

	public InstitutionFieldsKey getId() {
		return id;
	}

	public void setId(InstitutionFieldsKey id) {
		this.id = id;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}
}