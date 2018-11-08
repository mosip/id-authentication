package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the qcuser_registration_id database table.
 * 
 */
@Embeddable
public class QcuserRegistrationIdPKEntity implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="reg_id")
	private String regId;

	@Column(name="usr_id")
	private String usrId;

	public QcuserRegistrationIdPKEntity() {
		super();
	}
	public String getRegId() {
		return this.regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getUsrId() {
		return this.usrId;
	}
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.usrId.hashCode();
		
		return hash;
	}
}