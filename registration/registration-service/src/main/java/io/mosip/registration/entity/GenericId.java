package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

@Embeddable
public class GenericId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2757022596319558123L;
	@Column(name = "code", length = 32, nullable = false)
	private String code;
	@Column(name = "is_active", nullable = false)
	@Type(type = "true_false")
	private Boolean isActive;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the isActive
	 */
	public Boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericId other = (GenericId) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (isActive != other.isActive)
			return false;
		return true;
	}

}
