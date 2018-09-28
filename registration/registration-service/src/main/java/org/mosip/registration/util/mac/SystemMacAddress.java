package org.mosip.registration.util.mac;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.mosip.registration.constants.RegProcessorExceptionEnum;
import org.mosip.registration.exception.RegBaseCheckedException;

/**
 * Utility Class to get the MAC Address of the System
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class SystemMacAddress {

	/**
	 * Returns the MAC Address of the System as String
	 * 
	 * @return <b>String</b> the System's MAC Addresss
	 * @throws RegBaseCheckedException
	 */
	public static String getSystemMacAddress() throws RegBaseCheckedException {
		try {
			StringBuilder machineId = new StringBuilder();
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface networkInterface : interfaces) {
				byte[] macAddress = networkInterface.getHardwareAddress();
				if (null != macAddress && networkInterface.getDisplayName().contains("Ethernet")) {

					for (int i = 0; i < macAddress.length; i++) {
						machineId
								.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));
					}
					break;
				}
			}
			return machineId.toString();
		} catch (SocketException exception) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_SOCKET_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_SOCKET_ERROR_CODE.getErrorMessage());
		}
	}
}