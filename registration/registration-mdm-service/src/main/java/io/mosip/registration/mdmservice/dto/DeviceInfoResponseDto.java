package io.mosip.registration.mdmservice.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceInfoResponseDto {

	private List<DeviceInfoResponseData> deviceInfoResponses;

}
