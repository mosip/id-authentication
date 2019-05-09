package io.mosip.registration.mdm.service.impl;

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

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.mdm.dto.BioDevice;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;
import io.mosip.registration.mdm.dto.DeviceInfoResponseData;
import io.mosip.registration.mdm.integrator.MosipBioDeviceIntegrator;
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

	@Value("${mdm.host}")
	private String host;

	@Value("${mdm.hostProtocol}")
	private String hostProtocol;

	@Value("${mdm.portRangeFrom}")
	private int portFrom;

	@Value("${mdm.portRangeTo}")
	private int portTo;

	@Autowired
	private MosipBioDeviceIntegrator mosipBioDeviceIntegrator;

	private static Map<String, BioDevice> deviceRegistry = new HashMap<>();
	
	private static final Logger LOGGER = AppConfig.getLogger(MosipBioDeviceManager.class);


	/**
	 * Looks for all the configured ports available and initializes all the
	 * Biometric devices and saves it for future access
	 * 
	 * @throws RegBaseCheckedException
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void init() throws RegBaseCheckedException {

		String url;
		ObjectMapper mapper = new ObjectMapper();
		String value = null;
		DeviceInfoResponseData deviceInfoResponse = null;

		for (int port = portFrom; port <= portTo; port++) {

			url = buildUrl(port, MosipBioDeviceConstants.DEVICE_INFO_ENDPOINT);
			/* check if the service is available for the current port */
			if (RegistrationAppHealthCheckUtil.checkServiceAvailability(url)) {

				List<LinkedHashMap<String, String>> deviceInfoResponseDtos = (List<LinkedHashMap<String, String>>) mosipBioDeviceIntegrator
						.getDeviceInfo(url, MosipBioDeviceConstants.DEVICE_INFO_SERVICENAME, Object[].class);

				if (MosioBioDeviceHelperUtil.isListNotEmpty(deviceInfoResponseDtos)) {

					for (LinkedHashMap<String, String> deviceInfoResponseHash : deviceInfoResponseDtos) {

						try {
							value = mapper.writeValueAsString(deviceInfoResponseHash);
							deviceInfoResponse = mapper.readValue(value, DeviceInfoResponseData.class);
						} catch (IOException e) {
							LOGGER.debug(LoggerConstants.LOG_SERVICE_DELEGATE_UTIL_GET, APPLICATION_NAME, APPLICATION_ID,
									"Exception while mapping the response");
						}

						if (StringUtils.isNotEmpty(deviceInfoResponse.getType())) {

							/*
							 * Creating new bio device object for each device
							 * from service
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

									if ((MosipBioDeviceConstants.VALUE_SINGLE).equalsIgnoreCase(deviceSubType)) {
										deviceRegistry.put(MosipBioDeviceConstants.VALUE_FINGERPRINT + "_"
												+ MosipBioDeviceConstants.VALUE_SINGLE, bioDevice);
									} else if (MosipBioDeviceConstants.VALUE_SLAP.equalsIgnoreCase(deviceSubType)) {
										deviceRegistry.put(MosipBioDeviceConstants.VALUE_FINGERPRINT + "_"
												+ MosipBioDeviceConstants.VALUE_SLAP, bioDevice);
									} else if (MosipBioDeviceConstants.VALUE_TOUCHLESS
											.equalsIgnoreCase(deviceSubType)) {
										deviceRegistry.put(MosipBioDeviceConstants.VALUE_FINGERPRINT + "_"
												+ MosipBioDeviceConstants.VALUE_TOUCHLESS, bioDevice);
									}

								}

								break;
							case MosipBioDeviceConstants.VALUE_FACE:

								deviceRegistry.put(MosipBioDeviceConstants.VALUE_FACE, bioDevice);
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
						}
					}

				}
			}
		}
	}

	/**
	 * @return the deviceRegistry
	 */
	public static Map<String, BioDevice> getDeviceRegistry() {
		return deviceRegistry;
	}

	protected String buildUrl(int port, String endPoint) {
		return getRunningurl() + ":" + port + "/" + endPoint;
	}

	protected String getRunningurl() {
		return hostProtocol + "://" + host;
	}

	/**
	 * Triggers the capture based on the device type and returns the biometric
	 * value
	 * 
	 * @param deviceType
	 *            - The type of the device
	 * @return Map<String, byte[]> - captured biometric values from the device
	 * @throws RegBaseCheckedException
	 */
	public Map<String, byte[]> scan(String deviceType) throws RegBaseCheckedException {

		/*
		 * fetch and store the bio device list from MDM if the device registry
		 * does not contain the requested devices
		 */
		if (deviceRegistry.isEmpty() || deviceRegistry.get(deviceType) == null) {
			init();
		}

		BioDevice bioDevice = deviceRegistry.get(deviceType);
		if (bioDevice != null) {
			return bioDevice.capture();
		}

		return null;

	}

	/**
	 * discovers the device for the given device type
	 * 
	 * @param deviceType
	 *            - type of bio device
	 * @return List - list of device details
	 * @throws RegBaseCheckedException
	 */
	public List<DeviceDiscoveryResponsetDto> getDeviceDiscovery(String deviceType) throws RegBaseCheckedException {

		List<DeviceDiscoveryResponsetDto> deviceDiscoveryResponsetDtos = null;
		String url;
		for (int port = portFrom; port <= portTo; port++) {

			url = buildUrl(port, MosipBioDeviceConstants.DEVICE_DISCOVERY_ENDPOINT);

			if (RegistrationAppHealthCheckUtil.checkServiceAvailability(url)) {
				deviceDiscoveryResponsetDtos = mosipBioDeviceIntegrator.getDeviceDiscovery(url,
						MosipBioDeviceConstants.DEVICE_DISCOVERY_SERVICENAME, deviceType, null);
				break;
			}

		}
		return deviceDiscoveryResponsetDtos;

	}

	public void register() {

	}

	public void deRegister() {

	}

	public void getBioDevice(String type, String modality) {

	}
}
