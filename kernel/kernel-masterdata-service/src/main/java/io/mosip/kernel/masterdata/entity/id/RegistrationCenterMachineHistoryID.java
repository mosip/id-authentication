package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class RegistrationCenterMachineHistoryID implements Serializable {

	private static final long serialVersionUID = -8541947587557590379L;

	@Column(name = "regcntr_id", unique = true, nullable = false, length = 10)
	private String regCenterId;

	@Column(name = "machine_id", unique = true, nullable = false, length = 10)
	private String machineId;

	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectivetimes;
}