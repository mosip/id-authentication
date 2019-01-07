package io.mosip.kernel.idrepo.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
@Table(schema = "idrepo")
public class Uin {

	public Uin(String uinRefId, String uin, byte[] uinData, String uinDataHash, String regId, String statusCode,
			String langCode, String createdBy, LocalDateTime createdDateTime, String updatedBy,
			LocalDateTime updatedDateTime, Boolean isDeleted, LocalDateTime deletedDateTime) {
		this.uinRefId = uinRefId;
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
	@Column(insertable = false, updatable = false, nullable = false)
	private String uinRefId;

	/** The uin. */
	private String uin;

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

	@OneToMany(mappedBy = "uin", cascade = CascadeType.ALL)
	@NotFound(action=NotFoundAction.IGNORE)
	private List<UinBiometric> biometrics;

	@OneToMany(mappedBy = "uin", cascade = CascadeType.ALL)
	@NotFound(action=NotFoundAction.IGNORE)
	private List<UinDocument> documents;

	/**
	 * Gets the uin data.
	 *
	 * @return the uin data
	 */
	public byte[] getUinData() {
		return uinData;
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
