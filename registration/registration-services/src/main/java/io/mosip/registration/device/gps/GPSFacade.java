package io.mosip.registration.device.gps;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.device.gps.impl.GPSBU343Connector;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * GPSIntegrationImpl class for GPS response parsing and getting
 * latitude,longitude from GPS device.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 * 
 */
@Component
public class GPSFacade extends GPSBU343Connector {
	// need to chnage facade

	/** Object for gpsConnectionsList class. */
	private List<MosipGPSProvider> gpsConnectionsList;

	/** Object for gpsUtill class. */
	@Autowired
	private MosipGPSProvider mosipGPSProvider;

	/** Object for Logger. */

	private static final Logger LOGGER = AppConfig.getLogger(GPSFacade.class);

	/**
	 * This method gets the latitude and longitude details from GPS device.
	 *
	 * @param centerLat           the center latitude
	 * @param centerLngt          the center longitude
	 * @param gpsConnectionDevice the GPS connection device
	 * @return the latitude and longitude details from GPS device
	 */
	public Map<String, Object> getLatLongDtls(double centerLat, double centerLngt, String gpsConnectionDevice) {

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
				"Entering GPS fetch details methos");

		String serialPortConnected = String
				.valueOf(ApplicationContext.map().get(RegistrationConstants.GPS_SERIAL_PORT_WINDOWS));

		if (System.getProperty("os.name").equals("Linux")) {

			serialPortConnected = String.valueOf(ApplicationContext.map().get(RegistrationConstants.GPS_PORT_LINUX));
		}

		Map<String, Object> gpsResponseMap = new WeakHashMap<>();

		try {

			MosipGPSProvider gpsConnector = getConnectorFactory(gpsConnectionDevice);

			String gpsRawData = gpsConnector != null
					? gpsConnector.getComPortGPSData(serialPortConnected,
							Integer.parseInt(String
									.valueOf(ApplicationContext.map().get(RegistrationConstants.GPS_PORT_TIMEOUT))))
					: RegistrationConstants.GPS_CAPTURE_FAILURE;

			LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
					"GPS SIGNAL ============>" + gpsRawData);

			if (RegistrationConstants.GPS_CAPTURE_FAILURE.equals(gpsRawData)
					|| RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE.equals(gpsRawData)
					|| RegistrationConstants.GPS_CAPTURE_PORT_FAILURE_MSG.equals(gpsRawData)) {

				gpsResponseMap.put(RegistrationConstants.LATITUDE, null);
				gpsResponseMap.put(RegistrationConstants.LONGITUDE, null);
				gpsResponseMap.put(RegistrationConstants.GPS_DISTANCE, null);
				gpsResponseMap.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, gpsRawData);

			} else {

				String temp[] = gpsRawData.split("\\$");

				GPSPosition gpsdata = getGPRMCLatLong(temp);

				if (null != gpsdata && !gpsdata.getResponse().equals("failure")) {

					LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
							RegistrationConstants.LATITUDE + " =====>" + gpsdata.getLat() / 100
									+ RegistrationConstants.LONGITUDE + " ====>" + gpsdata.getLon() / 100
									+ RegistrationConstants.GPS_DISTANCE + " =====>" + gpsdata.getResponse());

					double deviceLat = gpsdata.getLat() / 100;
					double deviceLongi = gpsdata.getLon() / 100;

					double distance = actualDistance(deviceLat, deviceLongi, centerLat, centerLngt);

					LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
							"Distance between GPS Device and Registartion Stationin meters ====>" + Math.round(distance / 1000));

					if (deviceLat != 0 && deviceLongi != 0 && distance != 0) {

						gpsResponseMap.put(RegistrationConstants.LATITUDE, deviceLat);
						gpsResponseMap.put(RegistrationConstants.LONGITUDE, deviceLongi);
						gpsResponseMap.put(RegistrationConstants.GPS_DISTANCE, Math.round(distance / 1000));
						gpsResponseMap.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG,
								RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

					}
				} else {
					gpsResponseMap.put(RegistrationConstants.LATITUDE, null);
					gpsResponseMap.put(RegistrationConstants.LONGITUDE, null);
					gpsResponseMap.put(RegistrationConstants.GPS_DISTANCE, null);
					gpsResponseMap.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG,
							RegistrationConstants.GPS_CAPTURE_FAILURE_MSG);
					gpsResponseMap.put(RegistrationConstants.GPS_ERROR_CODE, RegistrationConstants.GPS_REG_LGE‌_002);

					LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
							"Unable to Calculate Distance");
				}

			}
		} catch (RegBaseCheckedException regBaseCheckedException) {

			gpsResponseMap.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, regBaseCheckedException.getMessage());

			LOGGER.error(RegistrationConstants.GPS_LOGGER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ExceptionUtils.getStackTrace(regBaseCheckedException));
		}

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
				"GPS map details" + gpsResponseMap);

		// TODO: Hard codded because if gps device and signa is not connected and weak
		// it wont allow for new registarion

		/*
		 * gpsResponseMap.put(RegistrationConstants.LATITUDE, 12.9913);
		 * gpsResponseMap.put(RegistrationConstants.LONGITUDE, 80.2457);
		 * gpsResponseMap.put(RegistrationConstants.GPS_DISTANCE, 180);
		 * gpsResponseMap.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG,
		 * RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);
		 */

		return gpsResponseMap;
	}

	/**
	 * This method is used to calculate the distance between the given latitudes and
	 * longitudes.
	 *
	 * @param fromlat from latitude
	 * @param fromlng from longitude
	 * @param tolat   to latitude
	 * @param tolng   to longitude
	 * @return the distance between given latitudes and longitudes
	 */
	private double actualDistance(double fromlat, double fromlng, double centerLat, double centerLngt) {

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
				"Calculation of distance between the geo location of machine and registration center started");

		double a = (fromlat - centerLat) * distPerLat(fromlat);
		double b = (fromlng - centerLngt) * distPerLng(fromlat);

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
				"Calculation of distance between the geo location of machine and registration center started");

		return Math.sqrt(a * a + b * b);

	}

	/**
	 * Distance per longitude.
	 *
	 * @param lat the lat
	 * @return the double
	 */
	private static double distPerLng(double lat) {
		return 0.0003121092 * Math.pow(lat, 4) + 0.0101182384 * Math.pow(lat, 3) - 17.2385140059 * lat * lat
				+ 5.5485277537 * lat + 111301.967182595;
	}

	/**
	 * Distance per latitude.
	 * 
	 * @param lat
	 * @return
	 */
	private static double distPerLat(double lat) {
		return -0.000000487305676 * Math.pow(lat, 4) - 0.0033668574 * Math.pow(lat, 3) + 0.4601181791 * lat * lat
				- 1.4558127346 * lat + 110579.25662316;
	}

	/**
	 * This method gets the geo location.
	 *
	 * @param gpsData - the GPS data
	 * @return the {@link GPSPosition}
	 * @throws RegBaseCheckedException - the exception class that handles all the
	 *                                 checked exceptions
	 */
	private GPSPosition getGPRMCLatLong(String[] gpsData) throws RegBaseCheckedException {

		LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID, "Entering into GRPMC method");

		GPSPosition geoLocation = null;

		try {

			for (int i = 0; i < gpsData.length; i++) {

				String gpsSignal = "$" + gpsData[i].trim();

				if (StringUtils.startsWith(gpsSignal, "$GPRMC")) {

					LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
							"GPRMC Singal GPS Signal =========>" + gpsSignal);

					geoLocation = mosipGPSProvider.signlaParser(gpsSignal);
				}
			}

			LOGGER.info(RegistrationConstants.GPS_LOGGER, APPLICATION_NAME, APPLICATION_ID,
					"GPS Response after parsing" + geoLocation);

		} catch (Exception exception) {
			throw new RegBaseCheckedException(RegistrationConstants.GPS_CAPTURING_EXCEPTION, exception.toString(),
					exception);
		}
		return geoLocation;

	}

	/**
	 * This method sets the GPS connections list.
	 *
	 * @param gpsConnectionsList the list of {@link MosipGPSProvider}
	 */
	@Autowired
	public void setGpsConnectionsList(List<MosipGPSProvider> gpsConnectionsList) {
		this.gpsConnectionsList = gpsConnectionsList;
	}

	private MosipGPSProvider getConnectorFactory(String gpsConnectionDevice) {
		MosipGPSProvider igpsConnector = null;

		if (!gpsConnectionsList.isEmpty()) {
			for (MosipGPSProvider connector : gpsConnectionsList) {
				if (connector.getClass().getName().contains(gpsConnectionDevice)) {
					igpsConnector = connector;
					break;
				}
			}
		}
		return igpsConnector;
	}

}
