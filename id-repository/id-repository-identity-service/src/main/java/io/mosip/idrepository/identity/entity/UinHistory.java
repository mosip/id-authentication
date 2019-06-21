package io.mosip.idrepository.identity.entity;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class UinHistory - Entity class for uin_h table.
 *
 * @author Manoj SP
 */
@Data
@Entity
@Table(name = "uin_h", schema = "idrepo")
@IdClass(HistoryPK.class)
public class UinHistory {
	
	/**
	 * Instantiates a new uin history.
	 */
	public UinHistory() {
		
	}

	/**
	 * Instantiates a new uin history.
	 *
	 * @param uinRefId the uin ref id
	 * @param effectiveDateTime the effective date time
	 * @param uin the uin
	 * @param uinHash the uin hash
	 * @param uinData the uin data
	 * @param uinDataHash the uin data hash
	 * @param regId the reg id
	 * @param bioRefId the bio ref id
	 * @param statusCode the status code
	 * @param langCode the lang code
	 * @param createdBy the created by
	 * @param createdDateTime the created date time
	 * @param updatedBy the updated by
	 * @param updatedDateTime the updated date time
	 * @param isDeleted the is deleted
	 * @param deletedDateTime the deleted date time
	 */
	public UinHistory(String uinRefId, LocalDateTime effectiveDateTime, String uin, String uinHash, byte[] uinData,
			String uinDataHash, String regId, String bioRefId, String statusCode, String langCode, String createdBy,
			LocalDateTime createdDateTime, String updatedBy, LocalDateTime updatedDateTime, Boolean isDeleted,
			LocalDateTime deletedDateTime) {
		super();
		this.uinRefId = uinRefId;
		this.effectiveDateTime = effectiveDateTime;
		this.uin = uin;
		this.uinHash = uinHash;
		this.uinData = uinData.clone();
		this.uinDataHash = uinDataHash;
		this.regId = regId;
		this.bioRefId = bioRefId;
		this.statusCode = statusCode;
		this.langCode = langCode;
		this.createdBy = createdBy;
		this.createdDateTime = createdDateTime;
		this.updatedBy = updatedBy;
		this.updatedDateTime = updatedDateTime;
		this.isDeleted = isDeleted;
		this.deletedDateTime = deletedDateTime;
	}

	/** The uin ref id. */
	@Id
	private String uinRefId;

	/** The effective date time. */
	@Id
	@Column(name = "eff_dtimes")
	private LocalDateTime effectiveDateTime;

	/** The uin. */
	private String uin;
	
	/** The uin hash. */
	private String uinHash;

	/** The uin data. */
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Basic(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private byte[] uinData;

	/** The uin data hash. */
	private String uinDataHash;

	/** The reg id. */
	private String regId;
	
	/** The bio ref id. */
	private String bioRefId;

	/** The status code. */
	private String statusCode;

	/** The lang code. */
	private String langCode;

	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;

	/** The created date time. */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdDateTime;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The updated date time. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

	/** The is deleted. */
	private Boolean isDeleted;

	/** The deleted date time. */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;

	/**
	 * Gets the uin data.
	 *
	 * @return the uin data
	 */
	public byte[] getUinData() {
		return uinData.clone();
	}

	/**
	 * Sets the uin data.
	 *
	 * @param uinData the new uin data
	 */
	public void setUinData(byte[] uinData) {
		this.uinData = uinData.clone();
	}

}
