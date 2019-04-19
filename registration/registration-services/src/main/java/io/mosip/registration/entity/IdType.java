package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.id.CodeAndLanguageCodeID;

/**
 * The Entity class for IdType.
 * 
 * @author Sreekar chukka
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "id_type", schema = "reg")
public class IdType extends RegistrationCommonFields implements Serializable {

	/**
	 * Serializable version id.
	 */
	private static final long serialVersionUID = -97767928612692201L;

	@EmbeddedId
	private CodeAndLanguageCodeID codeAndLanguageCodeID;

	/**
	 * @return the codeAndLanguageCodeID
	 */
	public CodeAndLanguageCodeID getCodeAndLanguageCodeID() {
		return codeAndLanguageCodeID;
	}

	/**
	 * @param codeAndLanguageCodeID the codeAndLanguageCodeID to set
	 */
	public void setCodeAndLanguageCodeID(CodeAndLanguageCodeID codeAndLanguageCodeID) {
		this.codeAndLanguageCodeID = codeAndLanguageCodeID;
	}

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
