package io.mosip.kernel.idgenerator.tsp.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class for TSPID generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "tspid", schema = "ids")
@Data
public class Tsp {

	/**
	 * The TSPID generated.
	 */
	@Id
	@Column(name = "tsp_id", nullable = false)
	private long tspId;

	/**
	 * Created time for TSPID.
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

	/**
	 * Updated By
	 */
	@Column(name = "upd_by")
	private String updatedBy;

	/**
	 * Update date time
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

	/**
	 * Is Deleted Or Not
	 */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/**
	 * Deleted date time.
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;

	/**
	 * Created by.
	 */
	@Column(name = "cr_by", nullable = false, length = 24)
	private String createdBy;
}
