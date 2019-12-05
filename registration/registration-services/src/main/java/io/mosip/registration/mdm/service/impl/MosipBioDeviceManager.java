
package io.mosip.registration.mdm.service.impl;

import static io.mosip.registration.constants.LoggerConstants.MOSIP_BIO_DEVICE_MANAGER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.BidiOrder;

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
import io.mosip.registration.dao.impl.RegisteredDeviceDAO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.mdm.dto.BioDevice;
import io.mosip.registration.mdm.dto.CaptureResponseBioDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;
import io.mosip.registration.mdm.dto.DeviceInfo;
import io.mosip.registration.mdm.dto.DeviceInfoResponseData;
import io.mosip.registration.mdm.dto.RequestDetail;
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
	 * This method will prepare the device registry, device registry contains all
	 * the running biometric devices
	 * <p>
	 * In order to prepare device registry it will loop through the specified ports
	 * and identify on which port any particular biometric device is running
	 * </p>
	 * 
	 * Looks for all the configured ports available and initializes all the
	 * Biometric devices and saves it for future access
	 * 
	 * @throws RegBaseCheckedException
	 *             - generalised exception with errorCode and errorMessage
	 */
	@PostConstruct
	@SuppressWarnings("unchecked")
	public void init() throws RegBaseCheckedException {

		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Entering init method for preparing device registry");

		for (int port = portFrom; port <= portTo; port++) {

			initByPort(port);

		}
		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Exit init method for preparing device registry");
	}

	private void initByPort(int port) throws RegBaseCheckedException {
		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Initializing on port : "+port);

	}

	private void initByDeviceType(String constructedDeviceType) throws RegBaseCheckedException {
		
		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Initializing device : "+constructedDeviceType);

		initByPortAndDeviceType(null, constructedDeviceType);

	}

	private void initByPortAndDeviceType(Integer availablePort, String deviceType) throws RegBaseCheckedException {

		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Initializing device : "+deviceType+" ,on Port : "+availablePort);

		if (availablePort != null) {

			String url;
			ObjectMapper mapper = new ObjectMapper();
			DeviceInfoResponseData deviceInfoResponse = null;

			url = buildUrl(availablePort, MosipBioDeviceConstants.DEVICE_INFO_ENDPOINT);
			/* check if the service is available for the current port */
			if (RegistrationAppHealthCheckUtil.checkServiceAvailability(url)) {
				List<LinkedHashMap<String, String>> deviceInfoResponseDtos = null;
				String response = (String) mosipBioDeviceIntegrator.getDeviceInfo(url, Object[].class);
				try {
					deviceInfoResponseDtos = mapper.readValue(response, List.class);
				} catch (IOException exception) {
					throw new RegBaseCheckedException("202", "Device not found");
				}

				if (MosioBioDeviceHelperUtil.isListNotEmpty(deviceInfoResponseDtos)) {

					deviceInfoResponse = getDeviceInfoResponse(mapper, availablePort, deviceInfoResponseDtos, deviceType);

				}
			} else {
				LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
						"No device is running at port number " + availablePort);
			}
		} else {
			for (int port = portFrom; port <= portTo; port++) {

				initByPortAndDeviceType(port,deviceType);

			}
		}
	}

	/**
	 * Gets the device info response.
	 * 
	 * @param mapper
	 * @param deviceInfoResponse
	 *            {@link DeviceInfoResponseData} -Contains the details of a specific
	 *            bio device
	 * @param port
	 *            - The port in which the bio device is active
	 * @param deviceInfoResponseDtos
	 *            - This list will contain the response that we receive after
	 *            finding the device
	 * @return {@link DeviceInfoResponseData}
	 */
	private DeviceInfoResponseData getDeviceInfoResponse(ObjectMapper mapper, int port,
			List<LinkedHashMap<String, String>> deviceInfoResponseDtos, String deviceType) {

		DeviceInfoResponseData deviceInfoResponse = null;
		for (LinkedHashMap<String, String> deviceInfoResponseHash : deviceInfoResponseDtos) {

			try {
				deviceInfoResponse = mapper.readValue(mapper.writeValueAsString(deviceInfoResponseHash),
						DeviceInfoResponseData.class);
				auditFactory.audit(AuditEvent.MDM_DEVICE_FOUND, Components.MDM_DEVICE_FOUND,
						RegistrationConstants.APPLICATION_NAME,
						AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			} catch (IOException exception) {
				LOGGER.error(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
						String.format("%s -> Exception while mapping the response  %s",
								exception.getMessage() + ExceptionUtils.getStackTrace(exception)));
				auditFactory.audit(AuditEvent.MDM_NO_DEVICE_AVAILABLE, Components.MDM_NO_DEVICE_AVAILABLE,
						RegistrationConstants.APPLICATION_NAME,
						AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			}

			creationOfBioDeviceObject(deviceInfoResponse, port, deviceType);
		}
		return deviceInfoResponse;
	}

	/**
	 * This method will save the device details into the device registry
	 *
	 * @param deviceInfoResponse
	 *            the device info response
	 * @param port
	 *            the port number
	 */
	private void creationOfBioDeviceObject(DeviceInfoResponseData deviceInfoResponse, int port, String deviceType) {

		if ((deviceType == null || deviceType
				.equals((deviceInfoResponse.getType().toUpperCase() + RegistrationConstants.UNDER_SCORE
						+ deviceInfoResponse.getSubType().toUpperCase()))
				&& (null != deviceInfoResponse && StringUtils.isNotEmpty(deviceInfoResponse.getType())))) {

			/*
			 * Creating new bio device object for each device from service
			 */
			BioDevice bioDevice = new BioDevice();
			bioDevice.setDeviceSubType(deviceInfoResponse.getSubType());
			bioDevice.setDeviceType(deviceInfoResponse.getType());
			bioDevice.setRunningPort(port);
			bioDevice.setRunningUrl(getRunningurl());
			bioDevice.setMosipBioDeviceIntegrator(mosipBioDeviceIntegrator);
			DeviceInfo deviceInfo = deviceInfoResponse.getDeviceInfo();
			bioDevice.setDeviceId(deviceInfo.getDeviceId());
			bioDevice.setFirmWare(deviceInfo.getFirmware());
			bioDevice.setDeviceProviderName(deviceInfo.getDeviceProviderName());
			bioDevice.setDeviceProviderId(deviceInfo.getDeviceProviderId());
			bioDevice.setDeviceModel(deviceInfo.getDeviceModel());
			bioDevice.setDeviceMake(deviceInfo.getDeviceMake());
			bioDevice.setDeviceExpiry(deviceInfo.getDeviceExpiry());
			bioDevice.setCertification(deviceInfo.getCertification());
			bioDevice.setTimestamp(deviceInfo.getTimestamp());
			bioDevice.setSerialVersion(deviceInfoResponse.getServiceVersion());
			deviceRegistry.put(bioDevice.getDeviceType().toUpperCase()+RegistrationConstants.UNDER_SCORE+bioDevice.getDeviceSubType().toUpperCase(), bioDevice);
		}	

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

	@Autowired
	RegisteredDeviceDAO registeredDeviceDAO;
	
	/**
	 * Triggers the biometric capture based on the device type and returns the
	 * biometric value from MDM
	 * 
	 * @param deviceType
	 *            - The type of the device
	 * @return CaptureResponseDto - captured biometric values from the device
	 * @throws RegBaseCheckedException
	 *             - generalised exception with errorCode and errorMessage
	 * @throws IOException
	 */
	public CaptureResponseDto scan(RequestDetail requestDetail) throws RegBaseCheckedException, IOException {

		BioDevice bioDevice = findDeviceToScan(requestDetail.getType());
		
		if (bioDevice != null) {
			
//			if(registeredDeviceDAO.getRegisteredDevices(bioDevice.getDeviceId()).size()>0) {
			
				LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
					"Device found in the device registery");
				return bioDevice.capture(requestDetail);
//			}
//			throw new RegBaseCheckedException("101", "");
		} else {
			LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
					"Device not found in the device registery");
			return null;
		}

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
	 * @throws IOException
	 */
	public CaptureResponseDto authScan(RequestDetail requestDetail) throws RegBaseCheckedException, IOException {

		BioDevice bioDevice = findDeviceToScan(requestDetail.getType());
		InputStream streaming =  stream(requestDetail);
		if (bioDevice != null) {
			LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
					"Device found in the device registery");
			CaptureResponseDto captureResponse =  bioDevice.capture(requestDetail);
			if(captureResponse.getError().getErrorCode().matches("202|403|404")) {
				streaming.close();	

			}
			return captureResponse;

		} else {
			LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
					"Device not found in the device registery");
			return null;
		}

	}

	private BioDevice findDeviceToScan(String deviceType) throws RegBaseCheckedException {
		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID, "Enter scan method");

		/*
		 * fetch and store the bio device list from MDM if the device registry does not
		 * contain the requested devices
		 */

		String deviceId = "";

		String constructedDeviceType = constructDeviceType(deviceType);

		if (deviceRegistry.isEmpty() || deviceRegistry.get(constructedDeviceType) == null) {
			initByDeviceType(constructedDeviceType);
		}
		BioDevice bioDevice = deviceRegistry.get(constructedDeviceType);
		if (bioDevice == null)
			return null;

		deviceId = constructedDeviceType.equals("FINGERPRINT_SLAB")
				? deviceType.substring("FINGERPRINT_SLAB".length() + 1, deviceType.length())
				: constructedDeviceType.equals("FINGERPRINT_SINGLE") ? "SINGLE" 
          : constructedDeviceType.equals("IRIS_DOUBLE") ? "DOUBLE"
						: constructedDeviceType.equals("FACE_FULL FACE")
								? deviceType.substring("FACE_FULL".length() + 1, deviceType.length())
								: deviceId;

		bioDevice.buildDeviceSubId(deviceId);
		return bioDevice;
	}

	private String constructDeviceType(String deviceType) {

		return deviceType.contains("FINGERPRINT_SLAB") ? "FINGERPRINT_SLAB"
				: deviceType.contains("IRIS_DOUBLE") ? "IRIS_DOUBLE"
						: deviceType.contains("FACE_FULL") ? "FACE_FULL FACE" : deviceType;

	}

	
	public InputStream stream(RequestDetail requestDetail) throws RegBaseCheckedException, IOException {

  	LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Stream starting for : "+requestDetail.getType());
		BioDevice bioDevice = null;
		bioDevice = findDeviceToScan(requestDetail.getType());
		if (bioDevice != null)
						return bioDevice.stream(requestDetail);
		return null;
 
	}

	/**
	 * This method will return the scanned biometric data
	 * <p>
	 * When the biometric scan will happed the return will contain many detail such
	 * as device code, quality score this method will extract the scanned biometric
	 * from the captured response
	 * </p>
	 * 
	 * 
	 * @param captureResponseDto
	 *            - Response Data object {@link CaptureResponseDto} which contains
	 *            the captured biometrics from MDM
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
	 * This method will be used to get the scanned biometric value which will be
	 * returned from the bio service as response
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
				return Base64.getDecoder().decode(captureResponseBioDtos.getCaptureResponseData().getBioExtract());
			}
		}
		return capturedByte;
	}

	/**
	 * This method will loop through the specified port to find the active devices
	 * at any instant of time
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
	 * Used to remove any inactive devices from device registry
	 * 
	 * @param type
	 *            - device type
	 */
	public void deRegister(String type) {
		deviceRegistry.remove(type);
		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Removed" + type + " from Device registry");

	}

	/**
	 * Gets the bio device.
	 *
	 * @param type
	 *            - the type of device
	 * @param modality
	 *            - the modality
	 */
	public void getBioDevice(String type, String modality) {

	}

	public void refreshBioDeviceByDeviceType(String deviceType) throws RegBaseCheckedException {

		LOGGER.info(MOSIP_BIO_DEVICE_MANAGER, APPLICATION_NAME, APPLICATION_ID,
				"Refreshing device of : "+deviceType);

		BioDevice bioDevice = deviceRegistry.get(constructDeviceType(deviceType));

		if(bioDevice!=null) {
			initByPortAndDeviceType(bioDevice.getRunningPort(), deviceType);
		}
		

	}
}
