package io.mosip.kernel.keymanagerservice.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Superclass for entities
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {

	/**
	 * The field createdBy
	 */
	@Column(name = "cr_by")
	private String createdBy;

	/**
	 * The field createdtimes
	 */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdtimes;

	/**
	 * The field updatedBy
	 */
	@Column(name = "upd_by")
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