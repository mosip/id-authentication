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
 * Entity class for KeyPolicy
 * 
 * @author Nagarjuna
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "key_policy_def",schema = "ida")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyPolicy {

	/**
	 * The field applicationId
	 */
	@Id
	@Column(name = "app_id", nullable = false, length = 36)
	private String applicationId;

	/**
	 * The field validityInDays
	 */
	@Column(name = "key_validity_duration")
	private int validityInDays;

	/**
	 * The field isActive
	 */
	@Column(name = "is_active")
	private boolean isActive;
	
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
