package io.mosip.registration.service.sync.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.Duration;
import java.time.Instant;
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
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.dao.SyncJobDAO.SyncJobInfo;
import io.mosip.registration.device.gps.IGPSIntegrator;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.sync.SyncStatusValidatorService;

/**
 * {@code SyncStatusValidatorServiceImpl} is the sync status validate service
 * class.
 *
 * @author Chukka Sreekar
 * @author Mahesh Kumar
 */
@Service

public class SyncStatusValidatorServiceImpl implements SyncStatusValidatorService {

	@Value("${MDS_J00001}")
	private int mdsJobId;
	@Value("${LCS_J00002}")
	private int lcsJobId;
	@Value("${PDS_J00003}")
	private int pdsJobId;
	@Value("${RSS_J00004}")
	private int rssJobId;
	@Value("${RCS_J00005}")
	private int rcsJobId;
	@Value("${RPS_J00006}")
	private int rpsJobId;
	@Value("${URS_J00007}")
	private int ursJobId;
	@Value("${POS_J00008}")
	private int posJobId;
	@Value("${LER_J00009}")
	private int lerJobId;
	@Value("${GEO_CAP_FREQ}")
	private String geoFrequnecyFlag;
	@Value("${REG_PAK_MAX_CNT_OFFLINE_FREQ}")
	private double packetMaxCount;
	@Value("${DIST_FRM_MACHN_TO_CENTER}")
	private double machnToCenterDistance;
	@Value("${GPS_DEVICE_MODEL}")
	private String gpsDeviceModel;

	/** Object for SyncJobDAO class. */
	@Autowired
	private SyncJobDAO syncJObDao;

	@Autowired
	private IGPSIntegrator gpsIntegrator;

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
		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the sync status started");

		Map<String, Integer> map = new HashMap<>();

		map.put(RegistrationConstants.OPT_TO_REG_MDS_J00001, mdsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_LCS_J00002, lcsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_PDS_J00003, pdsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_RSS_J00004, rssJobId);
		map.put(RegistrationConstants.OPT_TO_REG_RCS_J00005, rcsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_RPS_J00006, rpsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_URS_J00007, ursJobId);
		map.put(RegistrationConstants.OPT_TO_REG_POS_J00008, posJobId);
		map.put(RegistrationConstants.OPT_TO_REG_LER_J00009, lerJobId);

		List<ErrorResponseDTO> errorResponseDTOList = new ArrayList<>();

		int syncFailureCount = 0;

		try {
			SyncJobInfo syncJobInfo = syncJObDao.getSyncStatus();

			LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
					"Fetched SyncJobInfo containing the synccontrol list and yettoexportpacket count");

			if (syncJobInfo.getSyncControlList() != null && !syncJobInfo.getSyncControlList().isEmpty()) {

				for (SyncControl syncControl : syncJobInfo.getSyncControlList()) {

					// Comment:
					Date lastSyncDate = new Date(syncControl.getLastSyncDtimes().getTime());

					LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
							"Checking the actualdaysfrequency with the configured frequency [" + lastSyncDate + "]");

					if (map.get(syncControl.getSyncJobId().trim()) <= getActualDays(lastSyncDate)) {

						syncFailureCount++;

						if (RegistrationConstants.OPT_TO_REG_LER_J00009.equals(syncControl.getSyncJobId().trim())) {
							getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_002,
									RegistrationConstants.OPT_TO_REG_TIME_EXPORT_EXCEED,
									RegistrationConstants.ERROR, errorResponseDTOList);

						}
					}
				}

				if (syncFailureCount > 0) {
					getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_001,
							RegistrationConstants.OPT_TO_REG_TIME_SYNC_EXCEED, RegistrationConstants.ERROR,
							errorResponseDTOList);
				}
			}

			if (syncJobInfo.getYetToExportCount() >= packetMaxCount) {

				LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
						"Checking the yet to export packets frequency with the configured limit count");

				auditFactory.audit(AuditEvent.SYNC_PKT_COUNT_VALIDATE, Components.SYNC_VALIDATE,
						"Validating yet to export packets frequency with the configured limit count", "refId",
						"refIdType");

				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_003,
						RegistrationConstants.OPT_TO_REG_REACH_MAX_LIMIT, RegistrationConstants.ERROR,
						errorResponseDTOList);
			}

			if (RegistrationConstants.OPT_TO_REG_GEO_FLAG_SINGLETIME.equals(geoFrequnecyFlag)) {
				if (!isCapturedForTheDay()) {
					captureGeoLocation(errorResponseDTOList);
				}
			} else if (RegistrationConstants.OPT_TO_REG_GEO_FLAG_MULTIPLETIME.equals(geoFrequnecyFlag)) {
				captureGeoLocation(errorResponseDTOList);
			}

			LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
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
	 * {@code isCapturedForTheDay} is to capture time for first time login for the
	 * day.
	 * 
	 * @return boolean
	 */
	private boolean isCapturedForTheDay() {

		// TODO : lastCapturedTime - get this from application context....
		Map<String, Object> map = SessionContext.getInstance().getMapObject();
		Instant lastCapturedTime = (Instant) map.get(RegistrationConstants.OPT_TO_REG_LAST_CAPTURED_TIME);

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
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

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the geo location of machine w.r.t registration center started");

		double centerLatitude = Double.parseDouble(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterLatitude());

		double centerLongitude = Double.parseDouble(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterLongitude());

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Getting the center latitude and longitudes from session conext");

		Map<String, Object> gpsMapDetails = gpsIntegrator.getLatLongDtls(centerLatitude, centerLongitude,gpsDeviceModel);

		if (RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG
				.equals(gpsMapDetails.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG))) {

			if (machnToCenterDistance <= Double
					.parseDouble(gpsMapDetails.get(RegistrationConstants.GPS_DISTANCE).toString())) {

				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_004,
						RegistrationConstants.OPT_TO_REG_OUTSIDE_LOCATION, RegistrationConstants.ERROR,
						errorResponseDTOList);
			} else {

				SessionContext.getInstance().getMapObject().put(RegistrationConstants.OPT_TO_REG_LAST_CAPTURED_TIME,
						Instant.now());
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
			getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_007, RegistrationConstants.OPT_TO_REG_GPS_PORT_MISMATCH,
					RegistrationConstants.ERROR, errorResponseDTOList);
		}

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
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
}
