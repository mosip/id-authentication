package io.mosip.registration.mdm.integrator;

import static io.mosip.registration.constants.LoggerConstants.MOSIP_BIO_DEVICE_INTEGERATOR;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;
import io.mosip.registration.mdm.restclient.MosipBioDeviceServiceDelagate;
import io.mosip.registration.mdm.util.MdmRequestResponseBuilder;

/**
 * Handles the request and response of bio devices
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Service
public class MosipBioDeviceIntegratorImpl implements IMosipBioDeviceIntegrator {

	private static final Logger LOGGER = AppConfig.getLogger(MosipBioDeviceIntegratorImpl.class);
	
	@Autowired
	protected MosipBioDeviceServiceDelagate mosipBioDeviceServiceDelagate;

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#getDeviceInfo(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Object getDeviceInfo(String url, String serviceName, Class<?> responseType) throws RegBaseCheckedException {
		LOGGER.info(MOSIP_BIO_DEVICE_INTEGERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting the device info");

		return mosipBioDeviceServiceDelagate.invokeRestService(url, serviceName, null, responseType);

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#getDeviceDiscovery(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<DeviceDiscoveryResponsetDto> getDeviceDiscovery(String url, String serviceName, String deviceType,
			Class<?> responseType) throws RegBaseCheckedException {

		LOGGER.info(MOSIP_BIO_DEVICE_INTEGERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting into device Discovery");
		return (List<DeviceDiscoveryResponsetDto>) mosipBioDeviceServiceDelagate.invokeRestService(url, serviceName,
				MdmRequestResponseBuilder.buildDeviceDiscoveryRequest(deviceType), Object[].class);

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#capture(java.lang.String, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, byte[]> capture(String url, String serviceName, Object request, Class<?> responseType)
			throws RegBaseCheckedException {
		LOGGER.info(MOSIP_BIO_DEVICE_INTEGERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting into capture method");
		
		ObjectMapper mapper = new ObjectMapper();
		CaptureResponseDto mosipBioCaptureResponseDto=null;

		Map<String, Object> mosipBioCaptureResponseMap = (HashMap<String, Object>) mosipBioDeviceServiceDelagate.invokeRestService(url, serviceName, request, responseType);

		try {
			mosipBioCaptureResponseDto = mapper.readValue(mapper.writeValueAsString(mosipBioCaptureResponseMap), CaptureResponseDto.class);
		} catch (IOException exception) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Error while reading the response from capture%s",
							exception.getMessage() + ExceptionUtils.getStackTrace(exception)));
		}

		return MdmRequestResponseBuilder.parseBioCaptureResponse(mosipBioCaptureResponseDto);

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#getFrame()
	 */
	@Override
	public CaptureResponseDto getFrame() {
		return null;

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#forceCapture()
	 */
	@Override
	public CaptureResponseDto forceCapture() {
		return null;

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#responseParsing()
	 */
	@Override
	public CaptureResponseDto responseParsing() {
		return null;

	}
}
