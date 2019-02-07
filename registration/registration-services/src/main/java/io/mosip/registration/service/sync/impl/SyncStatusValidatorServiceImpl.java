package io.mosip.registration.service.sync.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dao.SyncJobControlDAO;
import io.mosip.registration.dao.SyncJobControlDAO.SyncJobInfo;
import io.mosip.registration.device.gps.GPSFacade;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;

/**
 * {@code SyncStatusValidatorServiceImpl} is the sync status validate service
 * class.
 *
 * @author Chukka Sreekar
 * @author Mahesh Kumar
 */
@Service

public class SyncStatusValidatorServiceImpl extends BaseService implements SyncStatusValidatorService {

	@Value("${GPS_DEVICE_MODEL}")
	private String gpsDeviceModel;
	/** Object forserialPortConnected. */
	@Value("${GPS_DEVICE_ENABLE_FLAG}")
	private String gpsEnableFlag;

	/** Object for SyncJobDAO class. */
	@Autowired
	private SyncJobControlDAO syncJObDao;

	@Autowired
	private GPSFacade gpsFacade;

	@Autowired
	private SyncJobConfigDAO jobConfigDAO;
	/** Object for Logger. */

	private static final Logger LOGGER = AppConfig.getLogger(SyncStatusValidatorServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.SyncStatusValidatorService#validateSyncStatus()
	 */
	public ResponseDTO validateSyncStatus() {
		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the sync status started");

		List<ErrorResponseDTO> errorResponseDTOList = new ArrayList<>();

		try {

			validatingRegisteredPacketCountAndDuration(errorResponseDTOList);
			validatingSyncJobsConfigAndYetToExportPacketCount(errorResponseDTOList);
			validatingCenterToMachineDistance(errorResponseDTOList);

			LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
					"Validating the sync status ended");

			auditFactory.audit(AuditEvent.SYNC_INFO_VALIDATE, Components.SYNC_VALIDATE,
					"Validating the sync status ended successfully", "refId", "refIdType");

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_STATUS_VALIDATE,
					runtimeException.toString());
		}

		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setErrorResponseDTOs(errorResponseDTOList);

		return responseDTO;

	}

	/**
	 * Validating center to machine distance.
	 *
	 * @param errorResponseDTOList the error response DTO list
	 */
	private void validatingCenterToMachineDistance(List<ErrorResponseDTO> errorResponseDTOList) {
		if (RegistrationConstants.ENABLE.equals(getGlobalConfigValueOf(RegistrationConstants.GEO_CAP_FREQ))) {
			if (!isCapturedForTheDay()) {
				captureGeoLocation(errorResponseDTOList);
			}
		} else if (RegistrationConstants.DISABLE.equals(getGlobalConfigValueOf(RegistrationConstants.GEO_CAP_FREQ))) {
			captureGeoLocation(errorResponseDTOList);
		}
	}

	/**
	 * Validating last successful sync jobs config days and yet to export packet
	 * count.
	 *
	 * @param errorResponseDTOList the error response DTO list
	 */
	private void validatingSyncJobsConfigAndYetToExportPacketCount(List<ErrorResponseDTO> errorResponseDTOList) {

		Map<String, String> map = getSyncJobId();

		int syncFailureCount = 0;

		SyncJobInfo syncJobInfo = syncJObDao.getSyncStatus();

		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Fetched SyncJobInfo containing the synccontrol list and yettoexportpacket count");

		if (syncJobInfo.getSyncControlList() != null && !syncJobInfo.getSyncControlList().isEmpty()) {

			for (SyncControl syncControl : syncJobInfo.getSyncControlList()) {

				// Comment:
				Date lastSyncDate = new Date(syncControl.getLastSyncDtimes().getTime());

				LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
						"Checking the actualdaysfrequency with the configured frequency [" + lastSyncDate + "]");

				if (map.get(syncControl.getSyncJobId()) != null
						&& Integer.parseInt(map.get(syncControl.getSyncJobId())) <= getActualDays(lastSyncDate)) {

					syncFailureCount++;

					if (RegistrationConstants.OPT_TO_REG_LER_J00009.equals(syncControl.getSyncJobId().trim())) {
						getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_002,
								RegistrationConstants.OPT_TO_REG_TIME_EXPORT_EXCEED, RegistrationConstants.ERROR,
								errorResponseDTOList);

					}
				}
			}

			if (syncFailureCount > 0) {
				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_001,
						RegistrationConstants.OPT_TO_REG_TIME_SYNC_EXCEED, RegistrationConstants.ERROR,
						errorResponseDTOList);
			}
		}

		if (syncJobInfo.getYetToExportCount() >= Double
				.parseDouble(getGlobalConfigValueOf(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ))) {

			LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
					"Checking the yet to export packets frequency with the configured limit count");

			auditFactory.audit(AuditEvent.SYNC_PKT_COUNT_VALIDATE, Components.SYNC_VALIDATE,
					"Validating yet to export packets frequency with the configured limit count", "refId", "refIdType");

			getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_003,
					RegistrationConstants.OPT_TO_REG_REACH_MAX_LIMIT, RegistrationConstants.ERROR,
					errorResponseDTOList);
		}
	}

	/**
	 * Validating registered packet count and duration.
	 *
	 * @param errorResponseDTOList the error response DTO list
	 */
	private void validatingRegisteredPacketCountAndDuration(List<ErrorResponseDTO> errorResponseDTOList) {
		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Fetching Registration details where status is Registered");

		List<Registration> registrationDetails = syncJObDao.getRegistrationDetails();

		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the count of packets of status Registered with configured value");

		auditFactory.audit(AuditEvent.PENDING_PKT_CNT_VALIDATE, Components.SYNC_VALIDATE,
				"Validating the count of packets of status Registered with configured value", "refId", "refIdType");

		if (registrationDetails.size() >= Integer
				.parseInt(String.valueOf(getGlobalConfigValueOf(RegistrationConstants.REG_PAK_MAX_CNT_APPRV_LIMIT)))) {

			getErrorResponse(RegistrationConstants.PAK_APPRVL_MAX_CNT, RegistrationConstants.REG_PKT_APPRVL_CNT_EXCEED,
					RegistrationConstants.ERROR, errorResponseDTOList);

			LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
					"Generating Error Response if count of packets of status Registered is greater than configured value");

			auditFactory.audit(AuditEvent.PENDING_PKT_CNT_VALIDATE, Components.SYNC_VALIDATE,
					"Generating Error Response if count of packets of status Registered is greater than configured value",
					"refId", "refIdType");
		}

		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the Duration of oldest packet of status Registered with configured duration");

		auditFactory.audit(AuditEvent.PENDING_PKT_DUR_VALIDATE, Components.SYNC_VALIDATE,
				"Validating the Duration of oldest packet of status Registered with configured duration", "refId",
				"refIdType");

		if (getDifference(!registrationDetails.isEmpty() ? registrationDetails.get(RegistrationConstants.PARAM_ZERO)
				: null) < 0) {

			getErrorResponse(RegistrationConstants.PAK_APPRVL_MAX_TIME,
					RegistrationConstants.REG_PKT_APPRVL_TIME_EXCEED, RegistrationConstants.ERROR,
					errorResponseDTOList);

			LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
					"Generating Error Response if Duration of oldest packet of status Registered is greater than configured value");

			auditFactory.audit(AuditEvent.PENDING_PKT_DUR_VALIDATE, Components.SYNC_VALIDATE,
					"Generating Error Response if Duration of oldest packet of status Registered is greater than configured value",
					"refId", "refIdType");
		}
	}

	/**
	 * {@code isCapturedForTheDay} is to capture time for first time login for the
	 * day.
	 * 
	 * @return boolean
	 */
	private boolean isCapturedForTheDay() {

		Map<String, Object> map = ApplicationContext.map();
		Instant lastCapturedTime = (Instant) map.get(RegistrationConstants.OPT_TO_REG_LAST_CAPTURED_TIME);

		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Capturing the login time Firsttime login");

		return lastCapturedTime != null && Duration.between(lastCapturedTime, Instant.now()).toHours() < 24;
	}

	/**
	 * {@code captureGeoLocation} is to capture the Geo location and calculate and
	 * validate the distance between the registration center and machine.
	 *
	 * @param errorResponseDTOList the error response DTO list
	 */
	private void captureGeoLocation(List<ErrorResponseDTO> errorResponseDTOList) {

		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the geo location of machine w.r.t registration center started");

		double centerLatitude = Double.parseDouble(
				SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterLatitude());

		double centerLongitude = Double.parseDouble(
				SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterLongitude());

		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Getting the center latitude and longitudes from session conext");

		if (gpsEnableFlag.equals(RegistrationConstants.ENABLE)) {

			Map<String, Object> gpsMapDetails = gpsFacade.getLatLongDtls(centerLatitude, centerLongitude,
					gpsDeviceModel);

			if (RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG
					.equals(gpsMapDetails.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG))) {

				if (Double.parseDouble(getGlobalConfigValueOf(RegistrationConstants.DIST_FRM_MACHN_TO_CENTER)) <= Double
						.parseDouble(gpsMapDetails.get(RegistrationConstants.GPS_DISTANCE).toString())) {

					getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_004,
							RegistrationConstants.OPT_TO_REG_OUTSIDE_LOCATION, RegistrationConstants.ERROR,
							errorResponseDTOList);
				} else {

					ApplicationContext.map().put(RegistrationConstants.OPT_TO_REG_LAST_CAPTURED_TIME, Instant.now());
				}
			} else if (RegistrationConstants.GPS_CAPTURE_FAILURE_MSG
					.equals(gpsMapDetails.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG))) {
				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_006, RegistrationConstants.OPT_TO_REG_WEAK_GPS,
						RegistrationConstants.ERROR, errorResponseDTOList);
			} else if (RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE_ERRO_MSG
					.equals(gpsMapDetails.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG))
					|| RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE
							.equals(gpsMapDetails.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG))) {
				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_005, RegistrationConstants.OPT_TO_REG_INSERT_GPS,
						RegistrationConstants.ERROR, errorResponseDTOList);
			} else {
				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_007,
						RegistrationConstants.OPT_TO_REG_GPS_PORT_MISMATCH, RegistrationConstants.ERROR,
						errorResponseDTOList);
			}
		}

		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the geo location of machine w.r.t registration center ended");

		auditFactory.audit(AuditEvent.SYNC_GEO_VALIDATE, Components.SYNC_VALIDATE,
				"Validating the geo information ended successfully", "refId", "refIdType");

	}

	/**
	 * {@code getActualDays} will calculate the difference of days between the given
	 * date and present date.
	 *
	 * @param lastSyncDate date
	 * @return the number of days
	 */
	private int getActualDays(Date lastSyncDate) {
		/* */
		return (int) (lastSyncDate != null
				? ((new Date().getTime() - lastSyncDate.getTime()) / (24 * 60 * 60 * 1000) + 1)
				: 0);
	}

	/**
	 * {@code getDifference} is to get difference between dates day.
	 * 
	 * @return long
	 */
	private long getDifference(Registration registration) {

		if (registration != null && registration.getCrDtime() != null) {

			/* This will subtract configured number of days from current Date */
			Date differDate = new Date(new Date().getTime() - (Long.parseLong(
					String.valueOf(getGlobalConfigValueOf(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT))) * 24
					* 3600 * 1000));

			/* This will convert timestamp to Date */
			Date createdDate = new Date(registration.getCrDtime().getTime());

			/* This will return differnce between 2 dates in minutes */
			return ChronoUnit.MINUTES.between(differDate.toInstant(), createdDate.toInstant());
		}
		return 0;
	}

	/**
	 * Gets the error response.
	 *
	 * @param code                 the code
	 * @param message              the message
	 * @param infoType             the info type
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

	/**
	 * Gets the sync job id.
	 *
	 * @return the sync job id
	 */
	public Map<String, String> getSyncJobId() {
		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Fetching Job ID's from sync_job_def table using API name started");

		Map<String, String> jobsMap = new HashMap<>();
		List<SyncJobDef> syncJobDefs = jobConfigDAO.getAll();
		for (SyncJobDef syncJobDef : syncJobDefs) {
			if (syncJobDef.getApiName() != null) {
				String configuredValue = getGlobalConfigValueOf(syncJobDef.getApiName());
				if (configuredValue != null) {
					jobsMap.put(syncJobDef.getId(), configuredValue);
				}
			}
		}
		LOGGER.info(LoggerConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Fetching Job ID's from sync_job_def table using API name ended");

		return jobsMap;
	}
}
