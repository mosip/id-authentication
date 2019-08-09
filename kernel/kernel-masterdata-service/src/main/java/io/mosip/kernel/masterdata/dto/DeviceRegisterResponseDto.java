package io.mosip.kernel.masterdata.dto;

import lombok.Data;

@Data
public class DeviceRegisterResponseDto {
	private DeviceRegResponseDto response;
	private String signature;
}
