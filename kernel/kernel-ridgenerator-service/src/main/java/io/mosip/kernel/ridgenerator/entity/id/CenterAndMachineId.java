package io.mosip.kernel.ridgenerator.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite id class for RID entity.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CenterAndMachineId implements Serializable {

	/**
	 * Generated serial number.
	 */
	private static final long serialVersionUID = -7092273068186176758L;

	/**
	 * The center id.
	 */
	@Column(name = "regcntr_id")
	private String centerId;

	/**
	 * The machine id.
	 */
	@Column(name = "machine_id")
	private String machineId;

}
