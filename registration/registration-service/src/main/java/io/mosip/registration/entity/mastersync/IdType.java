package io.mosip.registration.entity.mastersync;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;

/**
 * Entity class for IdType.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "id_type", schema = "reg")
public class IdType extends RegistrationCommonFields implements Serializable {

	private static final long serialVersionUID = -97767928612692201L;

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delDtimes;

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
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the crBy
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * @param crBy the crBy to set
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the delDtimes
	 */
	public LocalDateTime getDelDtimes() {
		return delDtimes;
	}

	/**
	 * @param delDtimes the delDtimes to set
	 */
	public void setDelDtimes(LocalDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}

}
