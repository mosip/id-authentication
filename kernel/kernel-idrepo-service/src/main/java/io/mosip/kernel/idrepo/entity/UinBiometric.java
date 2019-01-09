package io.mosip.kernel.idrepo.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class UinBiometric.
 *
 * @author Manoj SP
 */
@Data
@Entity
@NoArgsConstructor
@Table(schema = "idrepo")
public class UinBiometric {

	public UinBiometric(String uinRefId, String bioFileId, String biometricFileType, String biometricFileName,
			String biometricFileHash, String langCode, String createdBy, LocalDateTime createdDateTime,
			String updatedBy, LocalDateTime updatedDateTime, Boolean isDeleted, LocalDateTime deletedDateTime) {
		super();
		this.uinRefId = uinRefId;
		this.bioFileId = bioFileId;
		this.biometricFileType = biometricFileType;
		this.biometricFileName = biometricFileName;
		this.biometricFileHash = biometricFileHash;
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

	/** The bio file id. */
	private String bioFileId;

	/** The bio file id. */
	private String biometricFileType;

	/** The biometric file name. */
	private String biometricFileName;

	/** The biometric file hash. */
	private String biometricFileHash;

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

	@ManyToOne
	@JoinColumn(name = "uinRefId", insertable = false, updatable = false)
	@JsonBackReference
	private Uin uin;
}
