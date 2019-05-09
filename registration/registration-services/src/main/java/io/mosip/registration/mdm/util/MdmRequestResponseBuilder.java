package io.mosip.registration.mdm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.mdm.dto.BioDevice;
import io.mosip.registration.mdm.dto.DeviceDiscoveryRequestDto;
import io.mosip.registration.mdm.dto.MosipBioCaptureRequestDto;
import io.mosip.registration.mdm.dto.MosipBioCaptureResponse;
import io.mosip.registration.mdm.dto.MosipBioCaptureResponseDto;
import io.mosip.registration.mdm.dto.MosipBioRequest;

/**
 * Handles all the request response parsing of biometric data
 * 
 * @author balamurugan.ramamoorthy
 *
 */
public class MdmRequestResponseBuilder {

	public static MosipBioCaptureRequestDto buildMosipBioCaptureRequestDto(BioDevice bioDevice) {

		MosipBioCaptureRequestDto bioCaptureRequestDto = new MosipBioCaptureRequestDto();

		bioCaptureRequestDto.setEnv("");
		bioCaptureRequestDto.setTimeout(1000);
		bioCaptureRequestDto.setVersion("");
		bioCaptureRequestDto.setTransactionId("");

		MosipBioRequest mosipBioRequest = new MosipBioRequest();
		mosipBioRequest.setCount(1);
		mosipBioRequest.setDeviceId(bioDevice.getDeviceType());
		mosipBioRequest.setDeviceSubId(bioDevice.getDeviceSubType());
		mosipBioRequest.setFormat("");
		mosipBioRequest.setPreviousHash("");
		mosipBioRequest.setType("");

		List<MosipBioRequest> bioRequests = new ArrayList<>();
		bioRequests.add(mosipBioRequest);

		bioCaptureRequestDto.setMosipBioRequest(bioRequests);

		return bioCaptureRequestDto;

	}

	public static Map<String, byte[]> parseBioCaptureResponse(MosipBioCaptureResponseDto mosipBioCaptureResponseDto) {

		Map<String, byte[]> responseBioData = new HashMap<>();

		if (null != mosipBioCaptureResponseDto && MosioBioDeviceHelperUtil
				.isListNotEmpty(mosipBioCaptureResponseDto.getMosipBioDeviceDataResponses())) {

			for (MosipBioCaptureResponse mosipBioCaptureResponse : mosipBioCaptureResponseDto
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
