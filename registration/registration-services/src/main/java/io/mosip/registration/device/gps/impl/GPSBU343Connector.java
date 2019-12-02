package io.mosip.registration.device.gps.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.device.gps.MosipGPSProvider;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;

/**
 * Class for implementing GPS Connection and Latitude and distance.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Component
public class GPSBU343Connector implements MosipGPSProvider, SerialPortEventListener {

	/** Object for serialPort. */
	private SerialPort serialPortId;

	/** Object for portEnumList. */
	private CommPortIdentifier portEnumList;

	/** Object for inputStream. */
	private InputStream inputStream;

	/** Object for deviceData. */
	private StringBuilder deviceData = new StringBuilder();

	/** Object for Logger. */

	private static final Logger LOGGER = AppConfig.getLogger(GPSBU343Connector.class);

	/**
	 * {@code getGPSData} is to get GPS data to get latitude and longitude .
	 *
	 * @param portNo           the port no
	 * @param portReadWaitTime the port read wait time
	 * @return String GPRS details
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	@Override
	public String getComPortGPSData(String portNo, int portReadWaitTime) throws RegBaseCheckedException {

		String gpsResponse = null;

		LOGGER.info(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Entering to fetch GPS inforamtion" + "Port Name" + portNo + "wait time" + portReadWaitTime);
		try {

			@SuppressWarnings("unchecked")

			Enumeration<CommPortIdentifier> portListEnumeration = CommPortIdentifier.getPortIdentifiers();

			while (portListEnumeration.hasMoreElements()) {

				portEnumList = portListEnumeration.nextElement();

				if (portEnumList.getPortType() == CommPortIdentifier.PORT_SERIAL) {

					if (StringUtils.isNotEmpty(portNo)) {

						if (portEnumList.getName().equals(portNo)) {

							readDataFromComPort();
							Thread.sleep(portReadWaitTime);

						} else {
							deviceData.append(RegistrationConstants.GPS_CAPTURE_PORT_FAILURE_MSG);
						}

					} else {

						readDataFromComPort();
						Thread.sleep(portReadWaitTime);

					}

					if (deviceData.toString().contains(RegistrationConstants.GPS_SIGNAL)) {
						break;

					}

				}

			}

			if (portEnumList == null) {
				deviceData.append(RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE);
			}

			gpsResponse = deviceData.toString();

			if (StringUtils.isEmpty(gpsResponse)) {

				gpsResponse = RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE;
			}

			if (!gpsResponse.equals(RegistrationConstants.GPS_CAPTURE_FAILURE)
					&& !gpsResponse.equals(RegistrationConstants.GPS_CAPTURE_FAILURE_MSG)
					&& !gpsResponse.contains(RegistrationConstants.GPS_CAPTURE_PORT_FAILURE_MSG)
					&& !gpsResponse.equals(RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE)) {

				inputStream.close();
				serialPortId.removeEventListener();
				serialPortId.close();
			}

			deviceData = new StringBuilder();

		} catch (IOException | PortInUseException | TooManyListenersException | UnsupportedCommOperationException
				| InterruptedException regBaseCheckedException) {
			Thread.currentThread().interrupt();
			LOGGER.error(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ExceptionUtils.getStackTrace(regBaseCheckedException));
			throw new RegBaseCheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION,
					regBaseCheckedException.getMessage(), regBaseCheckedException);

		} catch (RuntimeException regBaseUnCheckedException) {
			Thread.currentThread().interrupt();
			LOGGER.error(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ExceptionUtils.getStackTrace(regBaseUnCheckedException));
			throw new RegBaseUncheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION,
					regBaseUnCheckedException.getMessage());

		}

		return gpsResponse;
	}

	/**
	 * This method is used to read data from com port and process the data.
	 *
	 * @throws IOException                       Signals that an I/O exception has
	 *                                           occurred.
	 * @throws PortInUseException                the port in use exception
	 * @throws TooManyListenersException         the too many listeners exception
	 * @throws UnsupportedCommOperationException the unsupported comm operation
	 *                                           exception
	 */
	private void readDataFromComPort()
			throws IOException, PortInUseException, TooManyListenersException, UnsupportedCommOperationException {

		LOGGER.info(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Reading data from GPS devie");

		if (serialPortId != null) {
			serialPortId.removeEventListener();
			serialPortId.close();
		}

		serialPortId = (SerialPort) portEnumList.open("", 0);

		serialPortId.addEventListener(this);
		serialPortId.notifyOnDataAvailable(true);
		serialPortId.setSerialPortParams(4800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		inputStream = serialPortId.getInputStream();

		LOGGER.info(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Ends Reading data from GPS devie");
	}

	/**
	 * This method is serial event to read byte stream for GPS.
	 *
	 * @param event - the event
	 */
	public void serialEvent(SerialPortEvent event) {

		LOGGER.info(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Reading byte stream from GPS");

		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:

			break;

		case SerialPortEvent.DATA_AVAILABLE:

			String line = "";

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));) {

				// read data
				while (inputStream.available() > 0) {

					while (reader.ready()) {

						line = reader.readLine();
						deviceData.append(line);
					}

				}
			} catch (IOException exception) {

				throw new RegBaseUncheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION,
						exception.toString(), exception);
			}
			break;

		default:
			break;
		}

		LOGGER.info(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Reading byte stream from GPS ends");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.device.IGPSConnector#parse(java.lang.String[],
	 * io.mosip.registration.device.GPSUtill.GPSPosition)
	 */
	public boolean parse(String[] tokens, GPSPosition position) {
		return false;
	}

	public GPSPosition signlaParser(String line) throws RegBaseCheckedException {

		/** The position. */
		GPSPosition position = new GPSPosition();

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
				"parsing GPS singal is started");

		if (line.startsWith("$")) {

			String gpsData = line.substring(1);
			String[] tokens = gpsData.split(",");
			String type = tokens[0];

			try {

				if ("GPRMC".equals(type)) {
					parseGPRMC(tokens, position);
				} else {
					position.setResponse("failure");
				}

			} catch (Exception exception) {
				throw new RegBaseCheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION, exception.toString(),
						exception);
			}
		}

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "parsing GPS singal is Ends");

		return position;
	}

	/**
	 * Parses the GPRMC.
	 *
	 * @param tokens   the tokens
	 * @param position the position
	 */
	private static void parseGPRMC(String[] tokens, GPSPosition position) {

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "parsing GPRMC Singal");

		if (tokens[2].equals("A")) {

			position.setLat(latitude2Decimal(tokens[3], tokens[4]));
			position.setLon(longitude2Decimal(tokens[5], tokens[6]));
			position.setResponse("success");

		} else {
			position.setResponse("failure");
		}

	}

	/**
	 * Decimal to longitude.
	 *
	 * @param lon the lon
	 * @param direction        the direction
	 * @return the float
	 */
	private static double longitude2Decimal(String lon, String direction) {

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "Longitude conversion begins");

		double longitudeDegrees = 0.0;

		if (lon.indexOf('.') != -1) {

			int minutesPosition = lon.indexOf('.') - 2;
			double minutes = Double.parseDouble(lon.substring(minutesPosition));
			double decimalDegrees = Double.parseDouble(lon.substring(minutesPosition)) / 60.0f;

			double degree = Double.parseDouble(lon) - minutes;
			double wholeDegrees = 100.0 * degree / 100;

			longitudeDegrees = wholeDegrees + decimalDegrees;

			if (direction.startsWith("W")) {
				longitudeDegrees = -longitudeDegrees;
			}

			LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
					"Longitude conversion begins");
		}
		return longitudeDegrees;
	}

	/**
	 * Decimal to latitude conversion.
	 *
	 * @param lat the lat
	 * @param direction       the direction
	 * @return the float
	 */
	private static double latitude2Decimal(String lat, String direction) {

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "Latitude conversion begins");

		double latitudeDegrees = 0.0;

		if (lat.indexOf('.') != -1) {

			int minutesPosition = lat.indexOf('.') - 2;
			double minutes = Double.parseDouble(lat.substring(minutesPosition));
			double decimalDegrees = Double.parseDouble(lat.substring(minutesPosition)) / 60.0f;

			double degree = Double.parseDouble(lat) - minutes;
			double wholeDegrees = 100.0 * degree / 100;

			latitudeDegrees = wholeDegrees + decimalDegrees;

			if (direction.startsWith("S")) {
				latitudeDegrees = -latitudeDegrees;
			}
			LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "Latitude conversion ends");
		}
		return latitudeDegrees;
	}

	/**
	 * The Class GPSPosition.
	 */
	public class GPSPosition {

		/** The latitudeFromGps. */
		private double latitudeFromGps = 0.0;

		/** The longitudeFromGps. */
		private double longitudeFromGps = 0.0;

		/** The response. */
		private String response = "";

		/**
		 * This method gets the latitude from GPS.
		 *
		 * @return the latitudeFromGps
		 */
		public double getLat() {
			return latitudeFromGps;
		}

		/**
		 * This method sets the latitude.
		 *
		 * @param lat the new lat
		 */
		public void setLat(double lat) {
			this.latitudeFromGps = lat;
		}

		/**
		 * This method gets the longitude from GPS.
		 *
		 * @return the longitudeFromGps
		 */
		public double getLon() {
			return longitudeFromGps;
		}

		/**
		 * This method sets the longitude.
		 *
		 * @param lon the new lon
		 */
		public void setLon(double lon) {
			this.longitudeFromGps = lon;
		}

		/**
		 * This method gets the response.
		 *
		 * @return the response
		 */
		public String getResponse() {
			return response;
		}

		/**
		 * This method sets the response.
		 * 
		 * @param response the response to set
		 */
		public void setResponse(String response) {
			this.response = response;
		}
	}

}
