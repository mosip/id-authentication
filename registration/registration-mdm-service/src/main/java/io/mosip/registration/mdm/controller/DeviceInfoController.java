package io.mosip.registration.mdm.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.mdm.dto.DeviceDiscoveryRequestDto;
import io.mosip.registration.mdm.dto.DeviceInfoResponseData;

@RestController
public class DeviceInfoController {

	@GetMapping("/deviceInfo")
	public List<DeviceInfoResponseData> getDevicesInfo() {
		return Arrays.asList(
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SLAP),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SINGLE),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_TOUCHLESS),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_IRIS, MosipBioDeviceConstants.VALUE_SINGLE), 
				getDeviceInfo(MosipBioDeviceConstants.VALUE_IRIS, MosipBioDeviceConstants.VALUE_DOUBLE),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FACE),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_VEIN)
		);

	}

	@PostMapping("/deviceInfo")
	private DeviceInfoResponseData getDeviceDiscoveryInfo(@RequestBody DeviceDiscoveryRequestDto deviceDiscoveryRequestDto) {
		String type = deviceDiscoveryRequestDto.getType();
		String subType ;
		if(deviceDiscoveryRequestDto.getSubType()!=null)
			subType = deviceDiscoveryRequestDto.getSubType();
		else 
			subType = "";
		return getDeviceInfo(type, subType);
		
	}
	
	private DeviceInfoResponseData getDeviceInfo(String type, String subType) {
		DeviceInfoResponseData deviceInfo = new DeviceInfoResponseData();
		deviceInfo.setType(type);
		deviceInfo.setSubType(subType);
		deviceInfo.setStatus("RUNNING");

		return deviceInfo;
	}
	
	private DeviceInfoResponseData getDeviceInfo(String type) {
		return getDeviceInfo(type, "");
	}

}
