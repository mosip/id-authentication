
package io.mosip.kernel.idgenerator.partnerid.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class for PartnerId generator.
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
// TODO: chages is there according to the table and column name once schema
// ready Now used tspid as sample entity and tspid_seq sample table.
@Entity
@Table(name = "tspid_seq", schema = "master")
@Data
public class Partner {

	/**
	 * The TSPID generated.
	 */
	@Id
	@Column(name = "curr_seq_no", nullable = false)
	private int tspId;

	/**
	 * The ID created by.
	 */
	@Column(name = "cr_by", nullable = false, length = 256)
	private String createdBy;

	/**
	 * The ID created at.
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

	/**
	 * The ID updated by.
	 */
	@Column(name = "upd_by", length = 256)
	private String updatedBy;

	/**
	 * The ID updated at.
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

}
