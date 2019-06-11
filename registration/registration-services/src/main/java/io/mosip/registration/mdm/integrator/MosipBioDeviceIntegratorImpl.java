package io.mosip.registration.mdm.integrator;

import static io.mosip.registration.constants.LoggerConstants.MOSIP_BIO_DEVICE_INTEGERATOR;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.mdm.dto.CaptureResponsBioDataDto;
import io.mosip.registration.mdm.dto.CaptureResponseBioDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;
import io.mosip.registration.mdm.util.MdmRequestResponseBuilder;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

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
	private AuditManagerService auditFactory;

	@Autowired
	protected ServiceDelegateUtil serviceDelegateUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#getDeviceInfo(
	 * java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Object getDeviceInfo(String url, Class<?> responseType) throws RegBaseCheckedException {
		LOGGER.info(MOSIP_BIO_DEVICE_INTEGERATOR, APPLICATION_NAME, APPLICATION_ID, "Getting the device info");

		return serviceDelegateUtil.invokeRestService(url, MosipBioDeviceConstants.DEVICE_INFO_SERVICENAME, null,
				responseType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#
	 * getDeviceDiscovery(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<DeviceDiscoveryResponsetDto> getDeviceDiscovery(String url, String deviceType, Class<?> responseType)
			throws RegBaseCheckedException {

		LOGGER.info(MOSIP_BIO_DEVICE_INTEGERATOR, APPLICATION_NAME, APPLICATION_ID, "Getting into device Discovery");
		return (List<DeviceDiscoveryResponsetDto>) serviceDelegateUtil.invokeRestService(url,
				MosipBioDeviceConstants.DEVICE_DISCOVERY_SERVICENAME,
				MdmRequestResponseBuilder.buildDeviceDiscoveryRequest(deviceType), Object[].class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#capture(java.
	 * lang.String, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, byte[]> capture(String url, Object request, Class<?> responseType)
			throws RegBaseCheckedException {
		LOGGER.info(MOSIP_BIO_DEVICE_INTEGERATOR, APPLICATION_NAME, APPLICATION_ID, "Getting into capture method");

		ObjectMapper mapper = new ObjectMapper();
		CaptureResponseDto mosipBioCaptureResponseDto = null;

		Map<String, Object> mosipBioCaptureResponseMap = (HashMap<String, Object>) serviceDelegateUtil
				.invokeRestService(url, MosipBioDeviceConstants.CAPTURE_SERVICENAME, request, responseType);

		try {
			mosipBioCaptureResponseDto = mapper.readValue(mapper.writeValueAsString(mosipBioCaptureResponseMap),
					CaptureResponseDto.class);

			/* Decode the bio response */
			decodeBiometrics(mapper, mosipBioCaptureResponseDto);

			auditFactory.audit(AuditEvent.MDM_CAPTURE_SUCCESS, Components.MDM_CAPTURE_SUCESS,
					RegistrationConstants.APPLICATION_NAME, AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

		} catch (IOException exception) {
			LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Error while reading the response from capture%s",
							exception.getMessage() + ExceptionUtils.getStackTrace(exception)));
			auditFactory.audit(AuditEvent.MDM_CAPTURE_FAILED, Components.MDM_CAPTURE_FAIELD,
					RegistrationConstants.APPLICATION_NAME, AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

		}

		return MdmRequestResponseBuilder.parseBioCaptureResponse(mosipBioCaptureResponseDto);

	}

	protected void decodeBiometrics(ObjectMapper mapper, CaptureResponseDto mosipBioCaptureResponseDto)
			throws IOException, JsonParseException, JsonMappingException {
		if (null != mosipBioCaptureResponseDto) {
			mosipBioCaptureResponseDto.setSlapImage(mosipBioCaptureResponseDto.getSlapImage() != null
					? Base64.getDecoder().decode(mosipBioCaptureResponseDto.getSlapImage())
					: null);
			if (mosipBioCaptureResponseDto.getMosipBioDeviceDataResponses() != null) {

				for (CaptureResponseBioDto captureResponseBioDto : mosipBioCaptureResponseDto
						.getMosipBioDeviceDataResponses()) {
					if (null != captureResponseBioDto) {
						String bioJson = new String(Base64.getDecoder().decode(captureResponseBioDto.getCaptureBioData()));
						if (null != bioJson) {
							CaptureResponsBioDataDto captureResponsBioDataDto = mapper.readValue(bioJson,
									CaptureResponsBioDataDto.class);
							if (null != captureResponsBioDataDto) {
								if (null != captureResponsBioDataDto.getBioExtract())
									captureResponsBioDataDto.setBioExtract(
											Base64.getDecoder().decode(captureResponsBioDataDto.getBioExtract()));
								if (null != captureResponsBioDataDto.getBioValue())
									captureResponsBioDataDto.setBioValue(
											Base64.getDecoder().decode(captureResponsBioDataDto.getBioValue()));
								captureResponseBioDto.setCaptureResponseData(captureResponsBioDataDto);
							}

						}
					}

				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#getFrame()
	 */
	@Override
	public CaptureResponseDto getFrame() {
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#forceCapture()
	 */
	@Override
	public CaptureResponseDto forceCapture() {
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#
	 * responseParsing()
	 */
	@Override
	public CaptureResponseDto responseParsing() {
		return null;

	}
}
