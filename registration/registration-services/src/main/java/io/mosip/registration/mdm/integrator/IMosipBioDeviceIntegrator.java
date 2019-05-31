package io.mosip.registration.mdm.integrator;

import java.util.List;
import java.util.Map;

import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;

public interface IMosipBioDeviceIntegrator {

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
	Object getDeviceInfo(String url, Class<?> responseType) throws RegBaseCheckedException;

	/**
	 * discovers the device for the given device type
	 * 
	 * @param url
	 *            - device info MDM service url
	 * @param serviceName
	 *            - MDM service name
	 * @param deviceType
	 *            - type of bio device
	 * @param responseType
	 *            - response format
	 * 
	 * @return List - list of device details
	 * @throws RegBaseCheckedException
	 */
	List<DeviceDiscoveryResponsetDto> getDeviceDiscovery(String url, String deviceType, Class<?> responseType)
			throws RegBaseCheckedException;

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
	CaptureResponseDto capture(String url, Object request, Class<?> responseType) throws RegBaseCheckedException;

	CaptureResponseDto getFrame();

	CaptureResponseDto forceCapture();

	CaptureResponseDto responseParsing();

}