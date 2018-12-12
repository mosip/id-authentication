package io.mosip.registration.device;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * class for gps response parsing and getting latitude,longitude from gps
 * device.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 * 
 */
@Service
public class GPSUtill {

	/** Object for Logger. */

	private static final Logger LOGGER = AppConfig.getLogger(GPSUtill.class);

	/**
	 * Decimal to latitude conversion.
	 *
	 * @param latitudeFromGps the latitudeFromGps
	 * @param direction       the direction
	 * @return the float
	 */
	private static double latitude2Decimal(String lat, String direction) {

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "Latitude conversion begins");

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
			LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
					"Latitude conversion ends");
		}
		return latitudeDegrees;
	}

	/**
	 * Decimal to longitude.
	 *
	 * @param longitudeFromGps the longitudeFromGps
	 * @param direction        the direction
	 * @return the float
	 */
	private static double longitude2Decimal(String lon, String direction) {

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "Longitude conversion begins");

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

			LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
					"Longitude conversion begins");
		}
		return longitudeDegrees;
	}

	/**
	 * Parses the GPRMC.
	 *
	 * @param tokens   the tokens
	 * @param position the position
	 */
	private static void parseGPRMC(String[] tokens, GPSPosition position) {

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "parsing GPRMC Singal");

		if (tokens[2].equals("A")) {

			position.setLat(latitude2Decimal(tokens[3], tokens[4]));
			position.setLon(longitude2Decimal(tokens[5], tokens[6]));
			position.setResponse("success");

		} else {
			position.setResponse("failure");
		}

	}

	/**
	 * Parses the.
	 *
	 * @param line the line
	 * @return the GPS position
	 * @throws RegBaseCheckedException
	 */
	public GPSPosition parse(String line) throws RegBaseCheckedException {

		/** The position. */
		GPSPosition position = new GPSPosition();

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
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
				throw new RegBaseCheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION, exception.toString());
			}
		}

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "parsing GPS singal is Ends");

		return position;
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
		 * @return the latitudeFromGps
		 */
		public double getLat() {
			return latitudeFromGps;
		}

		/**
		 * @param latitudeFromGps the latitudeFromGps to set
		 */
		public void setLat(double lat) {
			this.latitudeFromGps = lat;
		}

		/**
		 * @return the longitudeFromGps
		 */
		public double getLon() {
			return longitudeFromGps;
		}

		/**
		 * @param longitudeFromGps the longitudeFromGps to set
		 */
		public void setLon(double lon) {
			this.longitudeFromGps = lon;
		}

		/**
		 * @return the response
		 */
		public String getResponse() {
			return response;
		}

		/**
		 * @param response the response to set
		 */
		public void setResponse(String response) {
			this.response = response;
		}
	}
}
