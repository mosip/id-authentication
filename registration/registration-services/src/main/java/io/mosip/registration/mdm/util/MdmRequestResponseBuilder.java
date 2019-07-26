package io.mosip.registration.mdm.util;

import static io.mosip.registration.constants.LoggerConstants.MDM_REQUEST_RESPONSE_BUILDER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.mdm.dto.BioDevice;
import io.mosip.registration.mdm.dto.CaptureRequestDeviceDetailDto;
import io.mosip.registration.mdm.dto.CaptureRequestDto;
import io.mosip.registration.mdm.dto.CaptureResponseBioDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.DeviceDiscoveryRequestDto;

/**
 * Handles all the request response parsing of biometric data
 * 
 * @author balamurugan.ramamoorthy
 *
 */
public class MdmRequestResponseBuilder {
	
	private static final Logger LOGGER = AppConfig.getLogger(MdmRequestResponseBuilder.class);

	private static Random rnd = new Random();

	private MdmRequestResponseBuilder() {
		
	}

	/**
	 * Builds the capture request dto with all the required values needed for MDM
	 * capture service
	 * 
	 * @param bioDevice
	 *            - type of device
	 * @return captureRequestDto
	 */
	public static CaptureRequestDto buildMosipBioCaptureRequestDto(BioDevice bioDevice) {
		
		LOGGER.info(MDM_REQUEST_RESPONSE_BUILDER, APPLICATION_NAME, APPLICATION_ID,
				"Building the request dto");


		CaptureRequestDto bioCaptureRequestDto = new CaptureRequestDto();

		bioCaptureRequestDto.setEnv(RegistrationConstants.MDM_ENVIRONMENT);
		bioCaptureRequestDto.setMosipProcess("Registration");
		bioCaptureRequestDto.setTimeout(RegistrationConstants.MDM_TIMEOUT);
		bioCaptureRequestDto.setVersion(RegistrationConstants.MDM_VERSION);
		bioCaptureRequestDto.setRegistrationID(String.valueOf(generateID()));

		CaptureRequestDeviceDetailDto mosipBioRequest = new CaptureRequestDeviceDetailDto();
		mosipBioRequest.setType(getDevicCode(bioDevice.getDeviceType()));
		mosipBioRequest.setCount(1);

		String[] excptions= {};
		List<String> ls = (ArrayList<String>)SessionContext.map().get("CAPTURE_EXCEPTION");
		if(ls!=null) {
			excptions = (ls).toArray(excptions);
		}
		mosipBioRequest.setException(excptions);
		if (mosipBioRequest.getType().equalsIgnoreCase("FACE")) {
			mosipBioRequest.setType("FACE");
			mosipBioRequest.setRequestedScore(60);
		} else {
			mosipBioRequest.setRequestedScore(40);
		}
		mosipBioRequest.setDeviceId(bioDevice.getDeviceId());
		mosipBioRequest.setDeviceSubId(bioDevice.getDeviceSubId());
		mosipBioRequest.setPreviousHash("");
		bioCaptureRequestDto.setCaptureTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString());

		List<CaptureRequestDeviceDetailDto> bioRequests = new ArrayList<>();
		bioRequests.add(mosipBioRequest);

		bioCaptureRequestDto.setMosipBioRequest(bioRequests);
		
		Map<String, String> customOpts = new HashMap<String,String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
			put("Name", "name1");
			put("Value", "value1");
			}
		};	
		
		bioCaptureRequestDto.setCustomOpts(Arrays.asList(customOpts));

		return bioCaptureRequestDto;

	}
	
	private static String getDevicCode(String deviceType) {
		switch (deviceType.toUpperCase()) {
		case RegistrationConstants.FINGERPRINT_UPPERCASE:
			deviceType= "FIR";
			break;
		}
		
		return deviceType;
		
	}

	private static long generateID() { 
	    char [] digits = new char[10];
	    digits[0] = (char) (rnd.nextInt(9) + '1');
	    for(int i=1; i<digits.length; i++) {
	        digits[i] = (char) (rnd.nextInt(10) + '0');
	    }
	    return Long.parseLong(new String(digits));
	}
	
	/**
	 * Returns the map for captured byte
	 * 
	 * @param mosipBioCaptureResponseDto
	 *            - CaptureResponseDto
	 * @return {@link Map}
	 */
	public static Map<String, byte[]> parseBioCaptureResponse(CaptureResponseDto mosipBioCaptureResponseDto) {

		LOGGER.info(MDM_REQUEST_RESPONSE_BUILDER, APPLICATION_NAME, APPLICATION_ID,
				"Parsing the resonse dto");

		Map<String, byte[]> responseBioData = new HashMap<>();

		if (null != mosipBioCaptureResponseDto && MosioBioDeviceHelperUtil
				.isListNotEmpty(mosipBioCaptureResponseDto.getMosipBioDeviceDataResponses())) {

			for (CaptureResponseBioDto mosipBioCaptureResponse : mosipBioCaptureResponseDto
					.getMosipBioDeviceDataResponses()) {
				// TODO - have to clarify how the array of bio data response handled
				// TODO- clarify how the segmented values handled
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

	/**
	 * Builds the Request dto for Device Discovery which has to be passed for device
	 * discovery MDM service 
	 * 
	 * @param deviceType
	 *            - type of the device
	 * @return DeviceDiscoveryRequestDto
	 */
	public static DeviceDiscoveryRequestDto buildDeviceDiscoveryRequest(String deviceType) {
		LOGGER.info(MDM_REQUEST_RESPONSE_BUILDER, APPLICATION_NAME, APPLICATION_ID,
				"Building the discovery request");

		DeviceDiscoveryRequestDto deviceDiscoveryRequestDto = new DeviceDiscoveryRequestDto();
		deviceDiscoveryRequestDto.setType(deviceType);

		return deviceDiscoveryRequestDto;
	}

}
