package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.mastersync.id.CodeAndLanguageCodeID;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "biometric_attribute", schema = "reg")
@IdClass(CodeAndLanguageCodeID.class)
public class MasterBiometricAttribute extends MasterSyncBaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1302520630931393544L;
	
	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "code")),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code")) })
	
	private String code;
	private String langCode;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	@Column(name = "bmtyp_code")
	private String biometricTypeCode;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "bmtyp_code", referencedColumnName = "code" ,insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code" ,insertable = false, updatable = false) })
	private MasterBiometricType biometricType;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the biometricTypeCode
	 */
	public String getBiometricTypeCode() {
		return biometricTypeCode;
	}

	/**
	 * @param biometricTypeCode the biometricTypeCode to set
	 */
	public void setBiometricTypeCode(String biometricTypeCode) {
		this.biometricTypeCode = biometricTypeCode;
	}

	/**
	 * @return the biometricType
	 */
	public MasterBiometricType getBiometricType() {
		return biometricType;
	}

	/**
	 * @param biometricType the biometricType to set
	 */
	public void setBiometricType(MasterBiometricType biometricType) {
		this.biometricType = biometricType;
	}
	
	

}
