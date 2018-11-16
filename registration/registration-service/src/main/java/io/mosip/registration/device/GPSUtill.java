package io.mosip.registration.device;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseUncheckedException;

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
	private static float latitude2Decimal(String lat, String direction) {

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "Latitude conversion begins");

		float latitudeDegrees = 0.0f;

		if (lat.indexOf('.') != -1) {

			int minutesPosition = lat.indexOf('.') - 2;
			float minutes = Float.parseFloat(lat.substring(minutesPosition));
			float decimalDegrees = Float.parseFloat(lat.substring(minutesPosition)) / 60.0f;

			float degree = Float.parseFloat(lat) - minutes;
			float wholeDegrees = (int) degree / 100;

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
	private static float longitude2Decimal(String lon, String direction) {

		LOGGER.debug(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "Longitude conversion begins");

		float longitudeDegrees = 0.0f;

		if (lon.indexOf('.') != -1) {

			int minutesPosition = lon.indexOf('.') - 2;
			float minutes = Float.parseFloat(lon.substring(minutesPosition));
			float decimalDegrees = Float.parseFloat(lon.substring(minutesPosition)) / 60.0f;

			float degree = Float.parseFloat(lon) - minutes;
			float wholeDegrees = (int) degree / 100;

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
	 */
	public GPSPosition parse(String line) {

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
				throw new RegBaseUncheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION,
						exception.toString());
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
		private float latitudeFromGps = 0.0f;

		/** The longitudeFromGps. */
		private float longitudeFromGps = 0.0f;

		/** The response. */
		private String response = "";

		/**
		 * @return the latitudeFromGps
		 */
		public float getLat() {
			return latitudeFromGps;
		}

		/**
		 * @param latitudeFromGps the latitudeFromGps to set
		 */
		public void setLat(float lat) {
			this.latitudeFromGps = lat;
		}

		/**
		 * @return the longitudeFromGps
		 */
		public float getLon() {
			return longitudeFromGps;
		}

		/**
		 * @param longitudeFromGps the longitudeFromGps to set
		 */
		public void setLon(float lon) {
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
