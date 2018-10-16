package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the applicant_demographic database table.
 * 
 */
@Embeddable
public class ApplicantDemographicPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="reg_id")
	private String regId;

	@Column(name="lang_code")
	private String langCode;

	public ApplicantDemographicPK() {
		super();
	}
	public String getRegId() {
		return this.regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getLangCode() {
		return this.langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
}