package io.mosip.registration.mdm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.mdm.dto.BioDevice;
import io.mosip.registration.mdm.dto.DeviceDiscoveryRequestDto;
import io.mosip.registration.mdm.dto.CaptureRequestDto;
import io.mosip.registration.mdm.dto.CaptureResponseBioDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.CaptureRequestDeviceDetailDto;

/**
 * Handles all the request response parsing of biometric data
 * 
 * @author balamurugan.ramamoorthy
 *
 */
public class MdmRequestResponseBuilder {

	public static CaptureRequestDto buildMosipBioCaptureRequestDto(BioDevice bioDevice) {

		CaptureRequestDto bioCaptureRequestDto = new CaptureRequestDto();

		bioCaptureRequestDto.setEnv("");
		bioCaptureRequestDto.setTimeout(1000);
		bioCaptureRequestDto.setVersion("");
		bioCaptureRequestDto.setTransactionId("");

		CaptureRequestDeviceDetailDto mosipBioRequest = new CaptureRequestDeviceDetailDto();
		mosipBioRequest.setCount(1);
		mosipBioRequest.setDeviceId(bioDevice.getDeviceType());
		mosipBioRequest.setDeviceSubId(bioDevice.getDeviceSubType());
		mosipBioRequest.setFormat("");
		mosipBioRequest.setPreviousHash("");
		mosipBioRequest.setType("");

		List<CaptureRequestDeviceDetailDto> bioRequests = new ArrayList<>();
		bioRequests.add(mosipBioRequest);

		bioCaptureRequestDto.setMosipBioRequest(bioRequests);

		return bioCaptureRequestDto;

	}

	public static Map<String, byte[]> parseBioCaptureResponse(CaptureResponseDto mosipBioCaptureResponseDto) {

		Map<String, byte[]> responseBioData = new HashMap<>();

		if (null != mosipBioCaptureResponseDto && MosioBioDeviceHelperUtil
				.isListNotEmpty(mosipBioCaptureResponseDto.getMosipBioDeviceDataResponses())) {

			for (CaptureResponseBioDto mosipBioCaptureResponse : mosipBioCaptureResponseDto
					.getMosipBioDeviceDataResponses()) {
				// TODO - have to clarify how the array of bio data response handled
				// TODO- clarify how the sengmented values handled
				if (mosipBioCaptureResponse.getCaptureResponseData() != null) {
					String capturedType= mosipBioCaptureResponse.getCaptureResponseData().getBioType();
					if(mosipBioCaptureResponse.getCaptureResponseData().getBioSubType()!=null || !mosipBioCaptureResponse.getCaptureResponseData().getBioSubType().isEmpty()) {
						capturedType=capturedType+"_"+mosipBioCaptureResponse.getCaptureResponseData().getBioSubType();	
					}
					responseBioData.put(capturedType,
							mosipBioCaptureResponse.getCaptureResponseData().getBioValue());
				}

			}
		}
		return responseBioData;
	}

	public static DeviceDiscoveryRequestDto buildDeviceDiscoveryRequest(String deviceType) {
		DeviceDiscoveryRequestDto deviceDiscoveryRequestDto = new DeviceDiscoveryRequestDto();
		deviceDiscoveryRequestDto.setType(deviceType);

		return deviceDiscoveryRequestDto;
	}

}
