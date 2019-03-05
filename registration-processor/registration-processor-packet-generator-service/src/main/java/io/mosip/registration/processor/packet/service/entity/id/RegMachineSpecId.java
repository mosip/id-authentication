package io.mosip.registration.processor.packet.service.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The Class RegMachineSpecId.
 */
@Embeddable
public class RegMachineSpecId implements Serializable {
	private static final long serialVersionUID = -7306845601917592413L;

	@Column(name = "id")
	private String id;

	@Column(name = "lang_code")
	private String langCode;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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

}
