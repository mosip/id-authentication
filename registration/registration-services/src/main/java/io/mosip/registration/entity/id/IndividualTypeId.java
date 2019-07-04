package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.IndividualType;
import lombok.Data;

/**
 * Composite key for {@link IndividualType}
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Embeddable
@Data
public class IndividualTypeId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7001663925687776491L;

	@Column(name = "code")
	private String code;

	@Column(name = "lang_code")
	private String langCode;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((langCode == null) ? 0 : langCode.hashCode());
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
		CodeAndLanguageCodeID other = (CodeAndLanguageCodeID) obj;
		if (code == null) {
			if (other.getCode() != null)
				return false;
		} else if (!code.equals(other.getCode()))
			return false;
		if (langCode == null) {
			if (other.getLangCode() != null)
				return false;
		} else if (!langCode.equals(other.getLangCode()))
			return false;
		return true;
	}
}
