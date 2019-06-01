package io.mosip.registration.mdm.integrator;

import java.util.List;

import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;

public interface IMosipBioDeviceIntegrator {

	/**
	 * Gets the device info details from the MDM service
	 * 
	 * @param url
	 *            - device info MDM service url
	 * @param responseType
	 *            - response format
	 * @return Object - Device info
	 * @throws RegBaseCheckedException
	 *             - generalised exception with errorCode and errorMessage
	 */
	Object getDeviceInfo(String url, Class<?> responseType) throws RegBaseCheckedException;

	/**
	 * discovers the device for the given device type
	 * 
	 * @param url
	 *            - device info MDM service url
	 * @param deviceType
	 *            - type of bio device
	 * @param responseType
	 *            - response format
	 * 
	 * @return List - list of device details
	 * @throws RegBaseCheckedException
	 * 				- generalised exception with errorCode and errorMessage
	 */
	List<DeviceDiscoveryResponsetDto> getDeviceDiscovery(String url, String deviceType, Class<?> responseType)
			throws RegBaseCheckedException;

	/**
	 * Captures the biometric details from the Bio device through MDM service
	 * 
	 * @param url
	 *            - device info MDM service url
	 * @param request
	 *            - request for capture biometric
	 * @param responseType
	 *            - response format
	 * @return CaptureResponseDto - the captured biometric details
	 * @throws RegBaseCheckedException
	 * 			- generalized exception with errorCode and errorMessage
	 */
	CaptureResponseDto capture(String url, Object request, Class<?> responseType) throws RegBaseCheckedException;

	CaptureResponseDto getFrame();

	CaptureResponseDto forceCapture();

	CaptureResponseDto responseParsing();

}