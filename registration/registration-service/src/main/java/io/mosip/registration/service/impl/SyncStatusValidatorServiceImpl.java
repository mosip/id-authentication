package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.dao.SyncJobDAO.SyncJobInfo;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.GeoLocationCapture;
import io.mosip.registration.service.SyncStatusValidatorService;

/**
 * {@code SyncStatusValidatorServiceImpl} is the sync status validate service class.
 *
 * @author Chukka Sreekar
 */
@Service
public class SyncStatusValidatorServiceImpl implements SyncStatusValidatorService {

	/** Object for getting the attribute values from properties file. */
	@Autowired
	private Environment environment;

	/** Object for SyncJobDAO class. */
	@Autowired
	private SyncJobDAO syncJObDao;

	/** Object for GeoLocationCapture class. */
	@Autowired
	private GeoLocationCapture geoLocationCapture;

	/** Object for Logger. */

	private static MosipLogger LOGGER;

	/**
	 * Initializing logger.
	 *
	 * @param mosipRollingFileAppender the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.SyncStatusValidatorService#validateSyncStatus()
	 */
	public ResponseDTO validateSyncStatus() {
		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID, "Validating the sync status started");
		String geoFrequnecyFlag = environment.getProperty(RegistrationConstants.OPT_TO_REG_GEO_CAP_FREQ);
		ResponseDTO responseDTO = new ResponseDTO();
		List<ErrorResponseDTO> errorResponseDTOList = new ArrayList<>();
		double yetToExportCount = 0;
		SyncJobInfo syncJobInfo;
		int syncFailureCount = 0;
		
		try {
			syncJobInfo = syncJObDao.getSyncStatus();
			List<SyncControl> syncControlList = syncJobInfo.getSyncControlList();
			yetToExportCount = syncJobInfo.getYetToExportCount();
			LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
					APPLICATION_ID,
					"Fetched SyncJobInfo containing the synccontrol list and yettoexportpacket count");
			if (null != syncControlList && !syncControlList.isEmpty()) {
				for (SyncControl syncControl : syncControlList) {
					if (null != syncControl) {
						OffsetDateTime date = syncControl.getLastSyncDtimez();
						long epochMilli = date.toInstant().toEpochMilli();
						Date lastSyncDate = new Date(epochMilli);
						String freq = environment.getProperty(syncControl.getSJobId().trim());
						int actualFreqDays = getActualDays(lastSyncDate);
						LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
								APPLICATION_ID,
								"Checking the actualdaysfrequency with the configured frequency");
						if (Integer.parseInt(freq) <= (actualFreqDays)) {
							syncFailureCount++;
							if (RegistrationConstants.OPT_TO_REG_LER_J00009.equals(syncControl.getSJobId().trim())) {
								getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_002, RegistrationConstants.OPT_TO_REG_ICS‌_002_MSG,
										RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
							}
						}
					}
				}
			}
			if (syncFailureCount > 0) {
				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_001, RegistrationConstants.OPT_TO_REG_ICS‌_001_MSG,
						RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
			}

			if (yetToExportCount >= Double
					.parseDouble(environment.getProperty(RegistrationConstants.OPT_TO_REG_PAK_MAX_CNT_OFFLINE_FREQ))) {
				LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
						APPLICATION_ID,
						"Checking the yet to export packets frequency with the configured limit count");
				auditFactory.audit(AuditEvent.SYNC_PKT_COUNT_VALIDATE, AppModule.SYNC_VALIDATE,
						"Validating yet to export packets frequency with the configured limit count", "refId",
						"refIdType");
				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_003, RegistrationConstants.OPT_TO_REG_ICS‌_003_MSG,
						RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
			}
			if (RegistrationConstants.OPT_TO_REG_GEO_FLAG_SINGLETIME.equals(geoFrequnecyFlag)) {
				if (!isCapturedForTheDay()) {
					captureGeoLocation(errorResponseDTOList);
				}
			} else if (RegistrationConstants.OPT_TO_REG_GEO_FLAG_MULTIPLETIME.equals(geoFrequnecyFlag)) {
				captureGeoLocation(errorResponseDTOList);
			}

			LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
					APPLICATION_ID, "Validating the sync status ended");
			auditFactory.audit(AuditEvent.SYNC_INFO_VALIDATE, AppModule.SYNC_VALIDATE,
					"Validating the sync status ended successfully", "refId", "refIdType");

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_STATUS_VALIDATE,
					runtimeException.toString());
		}
		responseDTO.setErrorResponseDTOs(errorResponseDTOList);
		return responseDTO;

	}

	/**
	 * {@code isCapturedForTheDay} to capture time for first time login for the day.
	 * 
	 * @return boolean
	 */
	private boolean isCapturedForTheDay() {
		Instant lastCapturedTime;
		
		//lastCapturedTime - get this from session context if available....
		Map<String,Object> map=SessionContext.getInstance().getMapObject();
		lastCapturedTime=(Instant) map.get("lastCapturedTime");
		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID, "Capturing the login time Firsttime login");
		return lastCapturedTime != null && Duration.between(lastCapturedTime, Instant.now()).toHours() < 24;
	}

	/**
	 * {@code captureGeoLocation} to capture the Geo location and calculate and
	 * validate the distance between the registration center and machine.
	 *
	 * @param errorResponseDTOList the error response DTO list
	 */
	private void captureGeoLocation(List<ErrorResponseDTO> errorResponseDTOList) {

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID,
				"Validating the geo location of machine w.r.t registration center started");
		Instant lastCapturedTime;
		double distance;
		Map<String, Object> map;
		map = geoLocationCapture.getLatLongDtls();

		double centerLatitude = Double.parseDouble(SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
				.getRegistrationCenterLatitude());
		double centerLongitude = Double.parseDouble(SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
				.getRegistrationCenterLongitude());
		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID, "Getting the center latitude and longitudes from session conext");

		distance = actualDistance(centerLatitude, centerLongitude, map);
		if ("success".equals(map.get("errorMessage"))) {
			if (Double
					.parseDouble(environment.getProperty(RegistrationConstants.OPT_TO_REG_DIST_FRM_MACHN_TO_CENTER)) <= distance) {
				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_004, RegistrationConstants.OPT_TO_REG_ICS‌_004_MSG,
						RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
			}
		} else {
			getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_005, RegistrationConstants.OPT_TO_REG_ICS‌_005_MSG,
					RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
		}
		lastCapturedTime = Instant.now();
		SessionContext.getInstance().getMapObject().put("lastCapturedTime",lastCapturedTime);

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID,
				"Validating the geo location of machine w.r.t registration center ended");
		auditFactory.audit(AuditEvent.SYNC_GEO_VALIDATE, AppModule.SYNC_VALIDATE,
				"Validating the geo information ended successfully", "refId", "refIdType");

	}

	/**
	 * {@code distFrom} class is to calculate the distance between the given
	 * latitudes and longitudes.
	 *
	 * @param fromlat the fromlat
	 * @param fromlng the fromlng
	 * @param tolat   the tolat
	 * @param tolng   the tolng
	 * @return double
	 */
	public static double actualDistance(double fromlat, double fromlng, Map<String, Object> map) {
		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID,
				"Calculation of distance between the geo location of machine and registration center started");
		double earthRadius = RegistrationConstants.OPT_TO_REG_EARTH_RADIUS;
		double distanceLat = Math.toRadians((double) map.get("latitude") - fromlat);
		double distanceLng = Math.toRadians((double) map.get("longitude") - fromlng);
		double tempDist = Math.sin(distanceLat / 2) * Math.sin(distanceLat / 2)
				+ Math.cos(Math.toRadians(fromlat)) * Math.cos(Math.toRadians((double) map.get("latitude")))
						* Math.sin(distanceLng / 2) * Math.sin(distanceLng / 2);
		double radius = 2 * Math.atan2(Math.sqrt(tempDist), Math.sqrt(1 - tempDist));

		double dist = earthRadius * radius;
		double meterConversion = RegistrationConstants.OPT_TO_REG_METER_CONVERSN;
		double rounding = dist * meterConversion / 1000;

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID,
				"Calculation of distance between the geo location of machine and registration center started");
		return Math.round(rounding * 10000.0) / 10000.0;

	}

	/**
	 * Gets the calculated days from the given time to present date.
	 *
	 * @param lastSyncDate the date
	 * @return the date format
	 */
	public static int getActualDays(Date lastSyncDate) {
		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID,
				"Calculation of days between the last sync date and present time started");
		long diffDays = 0;
		if (null != lastSyncDate) {
			long diff = new Date().getTime() - lastSyncDate.getTime();
			diffDays = diff / (24 * 60 * 60 * 1000) + 1;
		}

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME,
				APPLICATION_ID,
				"Calculation of days between the last sync date and present time ended");
		return (int) diffDays;

	}

	/**
	 * Gets the error response.
	 *
	 * @param code the code
	 * @param message the message
	 * @param infoType the info type
	 * @param errorResponseDTOList the error response DTO list
	 * @return the error response
	 */
	private void getErrorResponse(String code, String message, String infoType,
			List<ErrorResponseDTO> errorResponseDTOList) {
		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setCode(code);
		errorResponseDTO.setMessage(message);
		errorResponseDTO.setInfoType(infoType);
		errorResponseDTOList.add(errorResponseDTO);
	}
}
