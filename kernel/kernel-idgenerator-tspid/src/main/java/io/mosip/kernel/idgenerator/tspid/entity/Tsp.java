
package io.mosip.kernel.idgenerator.tspid.entity;

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
@Table(name = "tspid_seq", schema = "master")
@Data
public class Tsp {

	/**
	 * The TSPID generated.
	 */
	@Id
	@Column(name = "curr_seq_no", nullable = false)
	private int tspId;

	/**
	 * The ID created by.
	 */
	@Column(name = "cr_by", nullable = false)
	private String createdBy;

	/**
	 * The ID created at.
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

	/**
	 * The ID updated by.
	 */
	@Column(name = "upd_by")
	private String updatedBy;

	/**
	 * The ID updated at.
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

}
