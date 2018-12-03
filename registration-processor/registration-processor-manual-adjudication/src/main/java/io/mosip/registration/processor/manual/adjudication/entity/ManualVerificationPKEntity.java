package io.mosip.registration.processor.manual.adjudication.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
public class ManualVerificationPKEntity implements Serializable {
	private static final long serialVersionUID = 8916394292563999805L;

	@Column(name = "reg_id", nullable = false)
	private String regId;

	
	
	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regId == null) ? 0 : regId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManualVerificationPKEntity other = (ManualVerificationPKEntity) obj;
		if (regId == null) {
			if (other.regId != null)
				return false;
		} else if (!regId.equals(other.regId))
			return false;
		return true;
	}



}
