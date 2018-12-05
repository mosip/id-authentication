package io.mosip.kernel.synchandler.entity.id;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RegistrationCenterMachineID implements Serializable {

	private static final long serialVersionUID = -8541947587557590379L;

	@Column(name = "regcntr_id", unique = true, nullable = false, length = 36)
	private String regCenterId;

	@Column(name = "machine_id", unique = true, nullable = false, length = 36)
	private String machineId;
}