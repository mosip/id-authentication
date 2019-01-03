package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RegAbisRefPkEntity implements Serializable {

	/** The Constant serialVersionUID. */
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	/** The reg id. */
	@Column(name = "reg_id")
	private String regId;

	/** The usr id. */
	@Column(name = "abis_ref_id")
	private String abisRefId;

	public RegAbisRefPkEntity() {
		super();

	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getAbisRefId() {
		return abisRefId;
	}

	public void setAbisRefId(String abisRefId) {
		this.abisRefId = abisRefId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abisRefId == null) ? 0 : abisRefId.hashCode());
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
		RegAbisRefPkEntity other = (RegAbisRefPkEntity) obj;
		if (abisRefId == null) {
			if (other.abisRefId != null)
				return false;
		} else if (!abisRefId.equals(other.abisRefId))
			return false;
		if (regId == null) {
			if (other.regId != null)
				return false;
		} else if (!regId.equals(other.regId))
			return false;
		return true;
	}

}
