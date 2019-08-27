package io.mosip.kernel.masterdata.dto;

import lombok.Data;

@Data
public class DeviceRegisterDto {
	private DeviceDataDto deviceData;
	private String dpSignature;
}
