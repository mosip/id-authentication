package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for KeyAlias
 * 
 * @author Nagarjuna
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "key_alias",schema = "ida")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyAlias{

	/**
	 * The field alias
	 */
	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String alias;

	/**
	 * The field applicationId
	 */
	@Column(name = "app_id", nullable = false, length = 36)
	private String applicationId;

	/**
	 * The field referenceId
	 */
	@Column(name = "ref_id", length = 36)
	private String referenceId;

	/**
	 * The field keyGenerationTime
	 */
	@Column(name = "key_gen_dtimes")
	private LocalDateTime keyGenerationTime;

	/**
	 * The field keyExpiryTime
	 */
	@Column(name = "key_expire_dtimes")
	private LocalDateTime keyExpiryTime;

	/**
	 * The field status
	 */
	@Column(name = "status_code", length = 36)
	private String status;
	
	/**
	 * The field createdBy
	 */
	@Column(name = "cr_by", length = 256)
	private String createdBy;

	/**
	 * The field createdtimes
	 */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdtimes;

	/**
	 * The field updatedBy
	 */
	@Column(name = "upd_by", length = 256)
	private String updatedBy;

	/**
	 * The field updatedtimes
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtimes;

	/**
	 * The field isDeleted
	 */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/**
	 * The field deletedtimes
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedtimes;
}
