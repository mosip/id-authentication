package io.mosip.registration.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.registration.entity.id.CodeAndLanguageCodeID;

/**
 * This Entity class contains list of center types with respect to language code.
 * The data for this table will come through sync from server master table.
 * 
 * @author Sreekar chukka
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "reg_center_type", schema = "reg")
@IdClass(CodeAndLanguageCodeID.class)
public class RegistrationCenterType extends RegistrationCommonFields {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 7869240207930949234L;

	@Id
	@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code")),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code")) })
	/**
	 * The code of the registration center type.
	 */
	private String code;

	/**
	 * The language code of the registration center type.
	 */
	private String langCode;

	/**
	 * The name of the registration center type.
	 */
	@Column(name = "name")
	private String name;

	/**
	 * The description of the registration center type.
	 */
	@Column(name = "descr")
	private String descr;

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
	 * @return the descr
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * @param descr the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}

}
