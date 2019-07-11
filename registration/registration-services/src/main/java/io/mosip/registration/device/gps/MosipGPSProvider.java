package io.mosip.registration.device.gps;

import io.mosip.registration.device.gps.impl.GPSBU343Connector.GPSPosition;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface class for connceting gps and for getting latitude , longitude and
 * distance from gps device.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 * 
 */
public interface MosipGPSProvider {

	/**
	 * This method connect to the GPS device in the mentioned port and wait for
	 * certain time to receive the data from the device.
	 *
	 * @param comPortNo
	 *            the com port no
	 * @param portReadWaitTime
	 *            the port read wait time
	 * @return the com port GPS data
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	String getComPortGPSData(String comPortNo, int portReadWaitTime) throws RegBaseCheckedException;

	/**
	 * This method parse GPS data and get latitude and logitude from gps
	 * information.
	 *
	 * @param tokens
	 *            the tokens
	 * @param position
	 *            the position
	 * @return true, if successful
	 */
	boolean parse(String[] tokens, GPSPosition position);

	/**
	 * This method parses the GPS Signal.
	 *
	 * @param line
	 *            the line used to parse the GPS signal
	 * @return the GPS position {@link GPSPosition}
	 * @throws RegBaseCheckedException
	 *             the exception class to handle all the checked exceptions
	 */
	GPSPosition signlaParser(String line) throws RegBaseCheckedException;

}
