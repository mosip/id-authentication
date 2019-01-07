package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
/**
 * The Class RegOsiPkEntity.
 *
 * @author Girish Yarru
 */
@Embeddable
public class RegOsiPkEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The reg id. */
	@Column(name = "reg_id", nullable = false)
	private String regId;

	/**
	 * Gets the reg id.
	 *
	 * @return the reg id
	 */
	public String getRegId() {
		return regId;
	}

	/**
	 * Sets the reg id.
	 *
	 * @param regId the new reg id
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regId == null) ? 0 : regId.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		RegOsiPkEntity other = (RegOsiPkEntity) obj;
		if (regId == null) {
			if (other.regId != null)
				return false;
		} else if (!regId.equals(other.regId))
			return false;
		return true;
	}



}
