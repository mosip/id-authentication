package io.mosip.registration.entity.mastersync;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;



@Embeddable
public class ReasonListId implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3035455749747854356L;



	@Column(name = "code")
	private String code;
	
	
	
	@Column(name = "lang_code")
	private String langCode;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "rsncat_code")
	private ReasonCategory reasonCategoryCode;

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

	/**
	 * @return the reasonCategoryCode
	 */
	public ReasonCategory getReasonCategoryCode() {
		return reasonCategoryCode;
	}

	/**
	 * @param reasonCategoryCode the reasonCategoryCode to set
	 */
	public void setReasonCategoryCode(ReasonCategory reasonCategoryCode) {
		this.reasonCategoryCode = reasonCategoryCode;
	}
	
	
}
