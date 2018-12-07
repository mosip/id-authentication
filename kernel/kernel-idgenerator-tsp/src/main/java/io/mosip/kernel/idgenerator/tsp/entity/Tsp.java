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
@Table(name = "tsp_id", schema = "ids")
@Data
public class Tsp {

	/**
	 * Primary id for tsp_id table.
	 */
	@Id
	@Column(name = "id", nullable = false)
	private int id;

	/**
	 * The TSPID generated.
	 */
	@Column(name = "tsp_id", nullable = false)
	private long tspId;

	/**
	 * Created time for TSPID.
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

}
