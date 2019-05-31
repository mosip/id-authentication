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

	/**
	 * Returns the Device info data
	 * @return DeviceInfoResponseData
	 */
	@GetMapping("/deviceInfo")
	public List<DeviceInfoResponseData> getDevicesInfo() {
		return Arrays.asList(
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SLAP_LEFT),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SLAP_RIGHT),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SLAP_THUMB),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SLAP_LEFT_ONBOARD),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SLAP_RIGHT_ONBOARD),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SLAP_THUMB_ONBOARD),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_SINGLE),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FINGERPRINT, MosipBioDeviceConstants.VALUE_TOUCHLESS),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_IRIS, MosipBioDeviceConstants.VALUE_SINGLE), 
				getDeviceInfo(MosipBioDeviceConstants.VALUE_IRIS, MosipBioDeviceConstants.VALUE_DOUBLE),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_FACE),
				getDeviceInfo(MosipBioDeviceConstants.VALUE_VEIN)
		);

	}

	/**
	 * Returns the Device info data
	 * @param DeviceDiscoveryRequestDto
	 * @return DeviceInfoResponseData
	 */
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
	
	/**
	 * Returns the Device info data
	 * @param String
	 * 		  -deviceType
	 * @param String
	 *        -deviceSubType
	 * @return DeviceInfoResponseData
	 */
	private DeviceInfoResponseData getDeviceInfo(String type, String subType) {
		DeviceInfoResponseData deviceInfo = new DeviceInfoResponseData();
		deviceInfo.setType(type);
		deviceInfo.setSubType(subType);
		deviceInfo.setStatus("RUNNING");

		return deviceInfo;
	}


	/**
	 * Returns the Device info data
	 * @param String
	 * 		  -deviceType
	 * @return DeviceInfoResponseData
	 */
	private DeviceInfoResponseData getDeviceInfo(String type) {
		return getDeviceInfo(type, "");
	}

}
