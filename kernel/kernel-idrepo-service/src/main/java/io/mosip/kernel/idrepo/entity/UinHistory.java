package io.mosip.kernel.idrepo.entity;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Uin.
 *
 * @author Manoj SP
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "uin_h", schema = "idrepo")
@IdClass(HistoryPK.class)
public class UinHistory {
	
	public UinHistory(String uinRefId, LocalDateTime effectiveDateTime, String uin, byte[] uinData, String uinDataHash,
			String regId, String statusCode, String langCode, String createdBy, LocalDateTime createdDateTime,
			String updatedBy, LocalDateTime updatedDateTime, Boolean isDeleted, LocalDateTime deletedDateTime) {
		super();
		this.uinRefId = uinRefId;
		this.effectiveDateTime = effectiveDateTime;
		this.uin = uin;
		this.uinData = uinData.clone();
		this.uinDataHash = uinDataHash;
		this.regId = regId;
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
	
	/** The uin data. */
	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	@Basic(fetch=FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private byte[] uinData;
	
	/** The uin data hash. */
	private String uinDataHash;
	
	/** The reg id. */
	private String regId;
	
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
	 * @param uinData
	 *            the new uin data
	 */
	public void setUinData(byte[] uinData) {
		this.uinData = uinData.clone();
	}
	
}
