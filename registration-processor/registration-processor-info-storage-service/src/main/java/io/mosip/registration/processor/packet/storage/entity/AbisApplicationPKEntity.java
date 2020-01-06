package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the abis_application database table.
 * 
 */
@Embeddable
public class AbisApplicationPKEntity implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String code;

	@Column(name="lang_code")
	private String langCode;

	public AbisApplicationPKEntity() {
	}
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLangCode() {
		return this.langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbisApplicationPKEntity)) {
			return false;
		}
		AbisApplicationPKEntity castOther = (AbisApplicationPKEntity)other;
		return 
			this.code.equals(castOther.code)
			&& this.langCode.equals(castOther.langCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.code.hashCode();
		hash = hash * prime + this.langCode.hashCode();
		
		return hash;
	}
}