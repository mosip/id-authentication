package io.mosip.kernel.syncdata.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationCenterMachineHistoryDto extends BaseDto {

	private String regCenterId;

	private String machineId;

	private LocalDateTime effectivetimes;

}
