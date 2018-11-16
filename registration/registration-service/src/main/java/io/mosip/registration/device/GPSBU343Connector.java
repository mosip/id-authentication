package io.mosip.registration.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.springframework.stereotype.Service;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.device.GPSUtill.GPSPosition;
import io.mosip.registration.exception.RegBaseUncheckedException;

/**
 * Class for implementing GPS Connection and Latitude and distance.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Service
public class GPSBU343Connector implements IGPSConnector, SerialPortEventListener {

	/** Object for serialPort. */
	private SerialPort serialPortId;

	/** Object for portEnumList. */
	private CommPortIdentifier portEnumList;

	/** Object for inputStream. */
	private InputStream inputStream;

	/** Object for deviceData. */
	private StringBuilder deviceData;

	/** Object for Logger. */

	private static final Logger LOGGER = AppConfig.getLogger(GPSBU343Connector.class);

	/**
	 * {@code getGPSData} is to get GPS data to get latitude and longitude .
	 *
	 * @param portNo           the port no
	 * @param portReadWaitTime the port read wait time
	 * @return String GPRS details
	 */
	@Override
	public String getGPSData(String portNo, int portReadWaitTime) {

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Entering to featch GPS inforamtion" + "Port Name" + portNo + "wait time" + portReadWaitTime);

		if (null == portNo || "".equals(portNo) || portNo.isEmpty()) {
			getGPSData(portReadWaitTime);
			return deviceData.toString();
		}

		deviceData = new StringBuilder();

		try {

			portEnumList = CommPortIdentifier.getPortIdentifier(portNo);

			if (null != portEnumList) {
				if (portEnumList.getPortType() == CommPortIdentifier.PORT_SERIAL
						&& portEnumList.getName().equals(portNo)) {

					readDataFromComPort();
					Thread.sleep(portReadWaitTime);
				}
			}

		} catch (IOException | InterruptedException | PortInUseException | TooManyListenersException
				| UnsupportedCommOperationException | NoSuchPortException exception) {
			throw new RegBaseUncheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION, exception.toString());
		}

		String gpsResponse = deviceData.toString();

		if (gpsResponse.isEmpty()) {

			gpsResponse = RegistrationConstants.GPS_CAPTURE_FAILURE;
		}
		return gpsResponse;

	}

	/**
	 * {@code getGPSData} is a overloaded method to get GPS data to get latitude and
	 * longitude when port is not provided.
	 *
	 * @param portReadWaitTime the port read wait time
	 * @return String GPRS details
	 */
	public String getGPSData(int portReadWaitTime) {

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Entering to featch GPS inforamtion" + "wait time" + portReadWaitTime);

		deviceData = new StringBuilder();

		try {

			@SuppressWarnings("unchecked")
			Enumeration<CommPortIdentifier> portListEnumeration = CommPortIdentifier.getPortIdentifiers();

			while (portListEnumeration.hasMoreElements()) {

				portEnumList = portListEnumeration.nextElement();

				if (portEnumList.getPortType() == CommPortIdentifier.PORT_SERIAL) {

					readDataFromComPort();
					Thread.sleep(portReadWaitTime);

					if (deviceData.toString().contains("$GP")) {
						break;

					}
				}

			}
			if (portEnumList == null) {
				deviceData.append("Please connect the GPS Device");
			}
		} catch (IOException | PortInUseException | TooManyListenersException | UnsupportedCommOperationException
				| InterruptedException exception) {

			throw new RegBaseUncheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION, exception.toString());

		}

		String gpsResponse = deviceData.toString();

		if (gpsResponse.isEmpty()) {

			gpsResponse = RegistrationConstants.GPS_CAPTURE_FAILURE;
		}
		return gpsResponse;
	}

	/**
	 * Read data from com port and process the data.
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

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Reading date from GPS devie");

		if (null == serialPortId) {

			serialPortId = (SerialPort) portEnumList.open("", 0);
			inputStream = serialPortId.getInputStream();

			serialPortId.addEventListener(this);
			serialPortId.notifyOnDataAvailable(true);
			serialPortId.setSerialPortParams(4800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		} else {
			inputStream = serialPortId.getInputStream();
		}

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Ends Reading date from GPS devie");
	}

	/**
	 * Serial event.
	 *
	 * @param event the event
	 */
	public void serialEvent(SerialPortEvent event) {

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
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

			try {

				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				// read data
				while (inputStream.available() > 0) {

					while (reader.ready()) {

						line = reader.readLine();
						deviceData.append(line);
					}

				}
			} catch (IOException exception) {
				throw new RegBaseUncheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION,
						exception.toString());
			}
			break;

		default:
			break;
		}

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Reading byte stream from GPS ends");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.device.IGPSConnector#parse(java.lang.String[],
	 * io.mosip.registration.device.GPSUtill.GPSPosition)
	 */
	@Override
	public boolean parse(String[] tokens, GPSPosition position) {
		return false;
	}
}
