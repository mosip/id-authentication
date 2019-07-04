package io.mosip.registration.mdm.integrator;

import java.util.List;

import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;

/**
 * This class will work as a mediator for request and response between the
 * application and the MDM server
 * <p> This class will be used to get the Device info of any biometric device based on url</p>
 * <p>Upon findin the devices , these devices will be regiestered in the device registery and from there we can find 
 * any particular device</p>
 * <p>This class will be used to capture the biometric details</p>
 *  
 * @author Taleev Aalam
 *
 */
public interface IMosipBioDeviceIntegrator {

	/**
	 * Gets the Information of the biometric devices based
	 * on the url(where device is running) that we are passing as parameter
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
	 * Discovers the device for the given device type.
	 * <p>The device discovery would be used to identify the MOSIP compliant devices by the
	 *applications</p> 
	 * 
	 * @param url
	 *            - device info MDM service url
	 * @param deviceType
	 *            - type of bio device
	 * @param responseType
	 *            - response format
	 * 
	 * @return List - list of {@link DeviceDiscoveryResponsetDto}
	 * @throws RegBaseCheckedException
	 * 				- generalised exception with errorCode and errorMessage
	 */
	List<DeviceDiscoveryResponsetDto> getDeviceDiscovery(String url, String deviceType, Class<?> responseType)
			throws RegBaseCheckedException;

	/**
	 * Captures the biometric details from the Bio device through MDM service
	 * <p>This will accept the url where the biometric device  will be running and a request data object which will 
	 * specify exactly what biometric we are requesting for e.g finger, iris, face and will the return a response
	 * containing the bytes of the scanned data along with some other detail such as quality score of the scanned data
	 * </p>
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

	/**
	 * @return {@link CaptureResponseDto}
	 */
	CaptureResponseDto getFrame();

	/**
	 * @return {@link CaptureResponseDto}
	 */
	CaptureResponseDto forceCapture();

	/**
	 * @return {@link CaptureResponseDto}
	 */
	CaptureResponseDto responseParsing();

}