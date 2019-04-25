package io.mosip.registration.mdm.integrator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosio.registration.mdm.restclient.MosipBioDeviceServiceDelagate;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.MosipBioCaptureResponseDto;
import io.mosip.registration.service.mdm.util.MdmRequestResponseBuilder;

/**
 * Handles the request and response of bio devices
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Service
public class MosipBioDeviceIntegrator {

	@Autowired
	protected MosipBioDeviceServiceDelagate mosipBioDeviceServiceDelagate;

	/**
	 * Gets the device info details from the MDM service
	 * 
	 * @param url
	 *            - device info MDM service url
	 * @param serviceName
	 *            - MDM service name
	 * @param responseType
	 *            - response format
	 * @return Object - Device info
	 * @throws RegBaseCheckedException
	 */
	public Object getDeviceInfo(String url, String serviceName, Class<?> responseType) throws RegBaseCheckedException {
		return mosipBioDeviceServiceDelagate.invokeRestService(url, serviceName, null, responseType);

	}

	public List<MosipBioCaptureResponseDto> getDeviceDiscovery() {
		return null;

	}

	/**
	 * Captures the biometric details from the Bio device through MDM service
	 * 
	 * @param url
	 *            - device info MDM service url
	 * @param serviceName
	 *            - MDM service name
	 * @param request
	 *            - request for capture biometric
	 * @param responseType
	 *            - response format
	 * @return Map<String, byte[]> - the captured biometric details
	 * @throws RegBaseCheckedException
	 */
	public Map<String, byte[]> capture(String url, String serviceName, Object request, Class<?> responseType)
			throws RegBaseCheckedException {

		MosipBioCaptureResponseDto mosipBioCaptureResponseDto = (MosipBioCaptureResponseDto) mosipBioDeviceServiceDelagate
				.invokeRestService(url, serviceName, request, responseType);

		return MdmRequestResponseBuilder.parseBioCaptureResponse(mosipBioCaptureResponseDto);

	}

	public MosipBioCaptureResponseDto getFrame() {
		return null;

	}

	public MosipBioCaptureResponseDto forceCapture() {
		return null;

	}

	public MosipBioCaptureResponseDto responseParsing() {
		return null;

	}
}
