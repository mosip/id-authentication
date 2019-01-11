package io.mosip.kernel.idgenerator.machineid.entity;

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
@Table(name = "mid", schema = "ids")
public class MachineId {
	/**
	 * The Machine ID.
	 */
	@Id
	@Column(name = "machine_id")
	private int mId;
}
