package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the biometric_exceptions database table.
 * 
 * @author Horteppa M1048399
 */
@Embeddable
public class BiometricExceptionPKEntity implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="reg_id")
	private String regId;

	@Column(name="missing_bio")
	private String missingBio;

	@Column(name="lang_code")
	private String langCode;

	public BiometricExceptionPKEntity() {
	}
	public String getRegId() {
		return this.regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getMissingBio() {
		return this.missingBio;
	}
	public void setMissingBio(String missingBio) {
		this.missingBio = missingBio;
	}
	public String getLangCode() {
		return this.langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BiometricExceptionPKEntity)) {
			return false;
		}
		BiometricExceptionPKEntity castOther = (BiometricExceptionPKEntity)other;
		return 
			this.regId.equals(castOther.regId)
			&& this.missingBio.equals(castOther.missingBio)
			&& this.langCode.equals(castOther.langCode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.missingBio.hashCode();
		hash = hash * prime + this.langCode.hashCode();
		
		return hash;
	}
}