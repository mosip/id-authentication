package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the qcuser_registration_id database table.
 * 
 */
@Embeddable
public class QcuserRegistrationIdPKEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	/** The reg id. */
	@Column(name="reg_id")
	private String regId;

	/** The usr id. */
	@Column(name="usr_id")
	private String usrId;

	/**
	 * Instantiates a new qcuser registration id PK entity.
	 */
	public QcuserRegistrationIdPKEntity() {
		super();
	}
	
	/**
	 * Gets the reg id.
	 *
	 * @return the reg id
	 */
	public String getRegId() {
		return this.regId;
	}
	
	/**
	 * Sets the reg id.
	 *
	 * @param regId the new reg id
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}
	
	/**
	 * Gets the usr id.
	 *
	 * @return the usr id
	 */
	public String getUsrId() {
		return this.usrId;
	}
	
	/**
	 * Sets the usr id.
	 *
	 * @param usrId the new usr id
	 */
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QcuserRegistrationIdPKEntity)) {
			return false;
		}
		QcuserRegistrationIdPKEntity castOther = (QcuserRegistrationIdPKEntity)other;
		return 
			this.regId.equals(castOther.regId)
			&& this.usrId.equals(castOther.usrId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.usrId.hashCode();
		
		return hash;
	}
}