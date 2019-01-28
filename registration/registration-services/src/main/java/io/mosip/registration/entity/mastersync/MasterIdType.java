package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.registration.entity.mastersync.id.CodeAndLanguageCodeID;

/**
 * Entity class for IdType.
 * 
 * @author Sreekar chukka
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "id_type", schema = "reg")
@IdClass(CodeAndLanguageCodeID.class)
public class MasterIdType extends MasterSyncBaseEntity implements Serializable {

	/**
	 * Serializable version id.
	 */
	private static final long serialVersionUID = -97767928612692201L;

	@Id
	@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code")),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code")) })
	/**
	 * The idtype code.
	 */
	private String code;

	/**
	 * The idtype language code.
	 */
	private String langCode;

	/**
	 * The idtype name.
	 */
	@Column(name = "name")
	private String name;

	/**
	 * The idtype description.
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
