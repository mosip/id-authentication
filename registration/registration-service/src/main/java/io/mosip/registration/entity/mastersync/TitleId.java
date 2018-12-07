package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Entity class to define composite primary key of table
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Embeddable
public class TitleId implements Serializable {
	private static final long serialVersionUID = -1169678222222376557L;

	@Column(name = "code")
	private String titleCode;

	@Column(name = "lang_code")
	private String langCode;

	/**
	 * @return the titleCode
	 */
	public String getTitleCode() {
		return titleCode;
	}

	/**
	 * @param titleCode the titleCode to set
	 */
	public void setTitleCode(String titleCode) {
		this.titleCode = titleCode;
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
