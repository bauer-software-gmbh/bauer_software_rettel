package de.bauersoft.data.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class InstitutionFieldsKey {
	@Column(name = "institution_id")
    Long institutionId;

    @Column(name = "field_id")
    Long fieldId;

	public Long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(Long institutionId) {
		this.institutionId = institutionId;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldId, institutionId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstitutionFieldsKey other = (InstitutionFieldsKey) obj;
		return Objects.equals(fieldId, other.fieldId) && Objects.equals(institutionId, other.institutionId);
	}
}
