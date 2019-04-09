package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.id.CenterMachineId;

/**
 * The Entity Class for Center Machine details
 * 
 * @author Yaswanth S
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "reg_center_machine")
public class CenterMachine extends RegistrationCommonFields {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4546131262621540483L;

	/** The center machine id. */
	@EmbeddedId
	private CenterMachineId centerMachineId;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The deleted date time. */
	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;

	/** The deleted date time. */
	@Column(name = "lang_code")
	private String langCode;

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
	 * @return the centerMachineId
	 */
	public CenterMachineId getCenterMachineId() {
		return centerMachineId;
	}

	/**
	 * @param centerMachineId the centerMachineId to set
	 */
	public void setCenterMachineId(CenterMachineId centerMachineId) {
		this.centerMachineId = centerMachineId;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the deletedDateTime
	 */
	public Timestamp getDeletedDateTime() {
		return deletedDateTime;
	}

	/**
	 * @param deletedDateTime the deletedDateTime to set
	 */
	public void setDeletedDateTime(Timestamp deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
