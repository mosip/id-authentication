package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.registration.entity.mastersync.id.CodeAndLanguageCodeID;

/**
 * Gender entity mapped according to DB
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "gender", schema = "reg")
@IdClass(CodeAndLanguageCodeID.class)
public class MasterGender extends MasterSyncBaseEntity implements Serializable {
	private static final long serialVersionUID = 1323022736883315822L;

	
	
	@Id
	@Column(name = "code")
	private String code;
    
	@Column(name = "name")
	private String genderName;

	@Id
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
	 * @return the genderName
	 */
	public String getGenderName() {
		return genderName;
	}

	/**
	 * @param genderName the genderName to set
	 */
	public void setGenderName(String genderName) {
		this.genderName = genderName;
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
