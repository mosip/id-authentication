package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import io.mosip.kernel.core.exception.ServiceError;
import lombok.Data;

@Data
public class DeviceRegResponseDto {
	private String status;
	private ServiceError error;
	private String deviceCode;
	private LocalDateTime timestamp;
	private String env;
}
