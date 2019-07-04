package io.mosip.registration.mdm.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceInfoResponseDto {

	private List<DeviceInfoResponseData> deviceInfoResponses;

}
