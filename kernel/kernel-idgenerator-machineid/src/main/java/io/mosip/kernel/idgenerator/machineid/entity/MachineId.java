package io.mosip.kernel.idgenerator.machineid.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class for Machine ID.
 * 
 * @author M1044542
 * @since 1.0.0
 *
 */
@Entity
@Data
@Table(name = "mid_seq", schema = "master")
public class MachineId {
	/**
	 * The Machine ID.
	 */
	@Id
	@Column(name = "curr_seq_no")
	private int mId;

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
