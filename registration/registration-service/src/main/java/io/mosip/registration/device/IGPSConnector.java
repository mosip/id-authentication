package io.mosip.registration.device;

import io.mosip.registration.device.GPSUtill.GPSPosition;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface class for connceting gps and for getting latitude , longitude and
 * distance from gps device.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 * 
 */
public interface IGPSConnector {

	/**
	 * This method connect to the GPS device in the mentioned port and wait for
	 * certain time to receive the data from the device.
	 * 
	 * 
	 * @param comPortNo
	 * @param portReadWaitTime
	 * @return
	 * @throws RegBaseCheckedException
	 */
	String getComPortGPSData(String comPortNo, int portReadWaitTime) throws RegBaseCheckedException;

	/**
	 * This method parse GPS data and get latitude and logitude from gps information
	 * 
	 * @param comPortNo
	 * @param portReadWaitTime
	 * @return
	 */
	boolean parse(String[] tokens, GPSPosition position);

}
