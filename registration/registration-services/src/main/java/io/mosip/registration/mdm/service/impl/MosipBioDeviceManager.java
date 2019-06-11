
package io.mosip.registration.mdm.service.impl;

import static io.mosip.registration.constants.LoggerConstants.MOSIP_BIO_DEVICE_MANAGER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.mdm.dto.BioDevice;
import io.mosip.registration.mdm.dto.CaptureResponseBioDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;
import io.mosip.registration.mdm.dto.DeviceInfoResponseData;
import io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator;
import io.mosip.registration.mdm.util.MosioBioDeviceHelperUtil;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * 
 * Handles all the Biometric Devices controls
 * 
 * @author balamurugan.ramamoorthy
 * 
 */
@Component
public class MosipBioDeviceManager {

	@Autowired
	private AuditManagerService auditFactory;

	@Value("${mdm.host}")
	private String host;

	@Value("${mdm.hostProtocol}")
	private String hostProtocol;

	@Value("${mdm.portRangeFrom}")
	private int portFrom;

	@Value("${mdm.portRangeTo}")
	private int portTo;

	@Autowired
	private IMosipBioDeviceIntegrator mosipBioDeviceIntegrator;

	private static Map<String, BioDevice> deviceRegistry = new HashMap<>();

	private static final Logger LOGGER = AppConfig.getLogger(MosipBioDeviceManager.class);

	/**
	 * Looks for all the configured ports available and initializes all the
	 * Biometric devices and saves it for future access
	 * 
	 * @throws RegBaseCheckedException
	 *             - generalised exception with errorCode and errorMessage
	 */
	@SuppressWarnings("unchecked")
	public void init() throws RegBaseCheckedException {

		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Entering init method for preparing device registery");

		String url;
		ObjectMapper mapper = new ObjectMapper();
		DeviceInfoResponseData deviceInfoResponse = null;

		for (int port = portFrom; port <= portTo; port++) {

			url = buildUrl(port, MosipBioDeviceConstants.DEVICE_INFO_ENDPOINT);
			/* check if the service is available for the current port */
			if (RegistrationAppHealthCheckUtil.checkServiceAvailability(url)) {

				List<LinkedHashMap<String, String>> deviceInfoResponseDtos = (List<LinkedHashMap<String, String>>) mosipBioDeviceIntegrator
						.getDeviceInfo(url, Object[].class);

				if (MosioBioDeviceHelperUtil.isListNotEmpty(deviceInfoResponseDtos)) {

					for (LinkedHashMap<String, String> deviceInfoResponseHash : deviceInfoResponseDtos) {

						try {
							deviceInfoResponse = mapper.readValue(mapper.writeValueAsString(deviceInfoResponseHash),
									DeviceInfoResponseData.class);
							auditFactory.audit(AuditEvent.MDM_DEVICE_FOUND, Components.MDM_DEVICE_FOUND,
									RegistrationConstants.APPLICATION_NAME,
									AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

						} catch (IOException exception) {
							LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME,
									APPLICATION_ID, String.format("%s -> Exception while mapping the response  %s",
											exception.getMessage() + ExceptionUtils.getStackTrace(exception)));
							auditFactory.audit(AuditEvent.MDM_NO_DEVICE_AVAILABLE, Components.MDM_NO_DEVICE_AVAILABLE,
									RegistrationConstants.APPLICATION_NAME,
									AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

						}

						if (null != deviceInfoResponse && StringUtils.isNotEmpty(deviceInfoResponse.getType())) {

							/*
							 * Creating new bio device object for each device from service
							 */
							BioDevice bioDevice = new BioDevice();
							bioDevice.setDeviceSubType(deviceInfoResponse.getSubType());
							bioDevice.setDeviceType(deviceInfoResponse.getType());
							bioDevice.setRunningPort(port);
							bioDevice.setRunningUrl(getRunningurl());
							bioDevice.setMosipBioDeviceIntegrator(mosipBioDeviceIntegrator);

							String deviceSubType = deviceInfoResponse.getSubType();
							switch (deviceInfoResponse.getType().toUpperCase()) {

							case MosipBioDeviceConstants.VALUE_FINGERPRINT:
								if (StringUtils.isNotEmpty(deviceInfoResponse.getSubType())) {
									deviceRegistry.put(MosipBioDeviceConstants.VALUE_FINGERPRINT + "_" + deviceSubType,
											bioDevice);
								}

								break;
							case MosipBioDeviceConstants.VALUE_FACE:

								deviceRegistry.put(MosipBioDeviceConstants.VALUE_FACE, bioDevice);
								LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
										bioDevice + " device type is being added in the device registery");
								break;
							case MosipBioDeviceConstants.VALUE_IRIS:

								if ((MosipBioDeviceConstants.VALUE_SINGLE).equalsIgnoreCase(deviceSubType)) {
									deviceRegistry.put(MosipBioDeviceConstants.VALUE_IRIS + "_"
											+ MosipBioDeviceConstants.VALUE_SINGLE, bioDevice);
								} else if (MosipBioDeviceConstants.VALUE_DOUBLE.equalsIgnoreCase(deviceSubType)) {
									deviceRegistry.put(MosipBioDeviceConstants.VALUE_IRIS + "_"
											+ MosipBioDeviceConstants.VALUE_DOUBLE, bioDevice);
								}
								break;
							case MosipBioDeviceConstants.VALUE_VEIN:
								deviceRegistry.put(MosipBioDeviceConstants.VALUE_VEIN, bioDevice);
								break;

							default:
								break;
							}
						} else {
							LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
									"Device response is invalid for device running at port number" + port);
						}
					}

				}
			} else {
				LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
						"No device is running at port number " + port);
			}
		}
		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Exit init method for preparing device registery");
	}

	/**
	 * @return the deviceRegistry
	 */
	public static Map<String, BioDevice> getDeviceRegistry() {
		return deviceRegistry;
	}

	private String buildUrl(int port, String endPoint) {
		return getRunningurl() + ":" + port + "/" + endPoint;
	}

	private String getRunningurl() {
		return hostProtocol + "://" + host;
	}

	/**
	 * Triggers the biometric capture based on the device type and returns the
	 * biometric value from MDM
	 * 
	 * @param deviceType
	 *            - The type of the device
	 * @return CaptureResponseDto - captured biometric values from the device
	 * @throws RegBaseCheckedException
	 *             - generalised exception with errorCode and errorMessage
	 */
	public CaptureResponseDto scan(String deviceType) throws RegBaseCheckedException {

		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID, "Enter scan method");

		/*
		 * fetch and store the bio device list from MDM if the device registry does not
		 * contain the requested devices
		 */
		if (deviceRegistry.isEmpty() || deviceRegistry.get(deviceType) == null) {
			init();
		}
		BioDevice bioDevice = deviceRegistry.get(deviceType);
		if (bioDevice != null) {
			LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
					"Device found in the device registery");
			return bioDevice.capture();
		} else {
			LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
					"Device not found in the device registery");
			throw new RegBaseCheckedException();
		}

	}

	/**
	 * Used to get the bio raw image value from the response
	 * 
	 * @param captureResponseDto
	 *            - Response object which contains the capture biometrics from MDM
	 * @return byte[] - captured bio image
	 */
	public byte[] getSingleBioValue(CaptureResponseDto captureResponseDto) {
		byte[] capturedByte = null;
		if (null != captureResponseDto && captureResponseDto.getMosipBioDeviceDataResponses() != null
				&& !captureResponseDto.getMosipBioDeviceDataResponses().isEmpty()) {

			CaptureResponseBioDto captureResponseBioDtos = captureResponseDto.getMosipBioDeviceDataResponses().get(0);
			if (null != captureResponseBioDtos && null != captureResponseBioDtos.getCaptureResponseData()) {
				return captureResponseBioDtos.getCaptureResponseData().getBioValue();
			}
		}
		return capturedByte;
	}

	/**
	 * Used to get the bio extract value from the response
	 * 
	 * @param captureResponseDto
	 *            - Response object which contains the capture biometrics from MDM
	 * @return byte[] - captured bio extract
	 */
	public byte[] getSingleBiometricIsoTemplate(CaptureResponseDto captureResponseDto) {
		byte[] capturedByte = null;
		if (null != captureResponseDto && captureResponseDto.getMosipBioDeviceDataResponses() != null
				&& !captureResponseDto.getMosipBioDeviceDataResponses().isEmpty()) {

			CaptureResponseBioDto captureResponseBioDtos = captureResponseDto.getMosipBioDeviceDataResponses().get(0);
			if (null != captureResponseBioDtos && null != captureResponseBioDtos.getCaptureResponseData()) {
				return captureResponseBioDtos.getCaptureResponseData().getBioExtract();
			}
		}
		return capturedByte;
	}

	/**
	 * discovers the device for the given device type
	 * 
	 * @param deviceType
	 *            - type of bio device
	 * @return List - list of device details
	 * @throws RegBaseCheckedException
	 *             - generalized exception with errorCode and errorMessage
	 */
	public List<DeviceDiscoveryResponsetDto> getDeviceDiscovery(String deviceType) throws RegBaseCheckedException {

		List<DeviceDiscoveryResponsetDto> deviceDiscoveryResponsetDtos = null;
		String url;
		for (int port = portFrom; port <= portTo; port++) {

			url = buildUrl(port, MosipBioDeviceConstants.DEVICE_DISCOVERY_ENDPOINT);

			if (RegistrationAppHealthCheckUtil.checkServiceAvailability(url)) {
				deviceDiscoveryResponsetDtos = mosipBioDeviceIntegrator.getDeviceDiscovery(url, deviceType, null);

				auditFactory.audit(AuditEvent.MDM_DEVICE_FOUND, Components.MDM_DEVICE_FOUND,
						RegistrationConstants.APPLICATION_NAME,
						AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());
				break;
			} else {
				LOGGER.debug(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
						"this" + url + " is unavailable");
				auditFactory.audit(AuditEvent.MDM_NO_DEVICE_AVAILABLE, Components.MDM_NO_DEVICE_AVAILABLE,
						RegistrationConstants.APPLICATION_NAME,
						AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			}

		}
		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID, "Device discovery completed");

		return deviceDiscoveryResponsetDtos;

	}

	public void register() {

	}

	/**
	 * Used to remove a specific device from device registry
	 * 
	 * @param type
	 *            - device type
	 */
	public void deRegister(String type) {
		deviceRegistry.remove(type);
		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Removed" + type + " from Device registery");

	}

	public void getBioDevice(String type, String modality) {

	}
}
