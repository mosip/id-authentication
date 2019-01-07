package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema = "master", name = "gender")
public class Gender extends MasterCommonFields {
	@EmbeddedId
	private GenericId genericId;
	@Column(name = "name")
	private String name;

	/**
	 * @return the genericId
	 */
	public GenericId getGenericId() {
		return genericId;
	}

	/**
	 * @param genericId
	 *            the genericId to set
	 */
	public void setGenericId(GenericId genericId) {
		this.genericId = genericId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((genericId == null) ? 0 : genericId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gender other = (Gender) obj;
		if (genericId == null) {
			if (other.genericId != null)
				return false;
		} else if (!genericId.equals(other.genericId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
