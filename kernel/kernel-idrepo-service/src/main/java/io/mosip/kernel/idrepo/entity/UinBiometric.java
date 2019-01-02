package io.mosip.kernel.idrepo.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Table(schema = "idrepo")
public class UinBiometric {
	
	/** The uin ref id. */
	@Id
	private String uinRefId;
	
	/** The bio file id. */
	private String bioFileId;
	
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
}
