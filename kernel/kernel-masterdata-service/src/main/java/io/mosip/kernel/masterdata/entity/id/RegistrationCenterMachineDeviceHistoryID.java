package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RegistrationCenterMachineDeviceHistoryID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8092065929310769990L;

	@Column(name = "regcntr_id", unique = true, nullable = false, length = 10)
	private String regCenterId;

	@Column(name = "device_id", unique = true, nullable = false, length = 36)
	private String deviceId;

	@Column(name = "machine_id", unique = true, nullable = false, length = 10)
	private String machineId;
	
	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectivetimes;
}
