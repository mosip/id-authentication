package io.mosip.registration.service.sync.impl;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dto.MainResponseDTO;
import io.mosip.registration.dto.PreRegArchiveDTO;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncRequestDTO;
import io.mosip.registration.dto.PreRegistrationIdsDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.external.PreRegZipHandlingService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

@Service
@PropertySource(value = "classpath:spring.properties")
public class PreRegistrationDataSyncServiceImpl extends BaseService implements PreRegistrationDataSyncService {

	@Autowired
	PreRegistrationDataSyncDAO preRegistrationDAO;

	@Autowired
	SyncManager syncManager;

	@Value("${PRE_REG_NO_OF_DAYS_LIMIT}")
	private int noOfDays;

	@Autowired
	private PreRegZipHandlingService preRegZipHandlingService;

	@Value("${PRE_REG_STUB_ENABLED}")
	private String isStubEnabled;

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(PreRegistrationDataSyncServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#
	 * getPreRegistrationIds(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	synchronized public ResponseDTO getPreRegistrationIds(String syncJobId) {

		LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration Id's started");

		ResponseDTO responseDTO = new ResponseDTO();

		/* Check Network Connectivity */
		boolean isOnline = RegistrationAppHealthCheckUtil.isNetworkAvailable();

		/* check if the machine is offline */
		if (!isOnline) {
			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_PACKET_NETWORK_ERROR, null);
			return responseDTO;
		}

		/* prepare request DTO to pass on through REST call */
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = prepareDataSyncRequestDTO();

		try {

			/* REST call to get Pre Registartion Id's */
			LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) serviceDelegateUtil
					.post(RegistrationConstants.GET_PRE_REGISTRATION_IDS, preRegistrationDataSyncDTO);
			TypeReference<MainResponseDTO<LinkedHashMap<String, Object>>> ref = new TypeReference<MainResponseDTO<LinkedHashMap<String, Object>>>() {
			};
			MainResponseDTO<LinkedHashMap<String, Object>> mainResponseDTO = new ObjectMapper()
					.readValue(new JSONObject(response).toString(), ref);
			if (isResponseNotEmpty(mainResponseDTO)) {

				PreRegistrationIdsDTO preRegistrationIdsDTO = new ObjectMapper().readValue(
						new JSONObject(mainResponseDTO.getResponse()).toString(), PreRegistrationIdsDTO.class);

				Map<String, String> preRegIds = (Map<String, String>) preRegistrationIdsDTO.getPreRegistrationIds();

				/* Get Packets Using pre registration ID's */
				for (Entry<String, String> preRegDetail : preRegIds.entrySet()) {

					getPreRegistration(responseDTO, preRegDetail.getKey(), syncJobId,
							Timestamp.from(Instant.parse(preRegDetail.getValue())));
					if (responseDTO.getErrorResponseDTOs() != null) {
						break;
					}
				}
			} else {
				LOGGER.error("PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, RegistrationConstants.PRE_REG_TO_GET_ID_ERROR);

				setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_ID_ERROR, null);
			}

		} catch (HttpClientErrorException | ResourceAccessException | HttpServerErrorException | RegBaseCheckedException
				| java.io.IOException exception) {

			LOGGER.error("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());

			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_ID_ERROR, null);
		}

		LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration Id's ended");

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#
	 * getPreRegistration(java.lang.String)
	 */
	@Override
	public ResponseDTO getPreRegistration(String preRegistrationId) {

		LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration started");

		ResponseDTO responseDTO = new ResponseDTO();

		/** Get Pre Registration Packet */
		getPreRegistration(responseDTO, preRegistrationId, RegistrationConstants.JOB_TRIGGER_POINT_USER, null);

		LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration completed");

		return responseDTO;
	}

	@SuppressWarnings("unchecked")
	private void getPreRegistration(ResponseDTO responseDTO, String preRegistrationId, String syncJobId,
			Timestamp lastUpdatedTimeStamp) {

		LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration started");

		/* Check in Database whether required record already exists or not */
		PreRegistrationList preRegistration = preRegistrationDAO.get(preRegistrationId);

		/* Check Network Connectivity */
		boolean isOnline = RegistrationAppHealthCheckUtil.isNetworkAvailable();

		/* check if the packet is not available in db and the machine is offline */
		if (!isOnline && preRegistration == null) {
			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_PACKET_NETWORK_ERROR, null);
			return;
		}

		boolean isUpdated = false;

		if (isPacketUpdatedInServer(preRegistration)) {

			isUpdated = (preRegistration.getLastUpdatedPreRegTimeStamp().equals(lastUpdatedTimeStamp));
		}

		byte[] decryptedPacket = null;

		boolean isJob = (!RegistrationConstants.JOB_TRIGGER_POINT_USER.equals(syncJobId));

		/*
		 * Get Packet From REST call when the packet is updated in the server or always
		 * if its a manual trigger
		 */
		if (isFetchToBeTriggered(isOnline, isUpdated, isJob)) {

			/* prepare request params to pass through URI */
			Map<String, String> requestParamMap = new HashMap<>();
			requestParamMap.put(RegistrationConstants.PRE_REGISTRATION_ID, preRegistrationId);

			String triggerPoint = getTriggerPoint(isJob);

			try {
				/* REST call to get packet */
				MainResponseDTO<LinkedHashMap<String, Object>> mainResponseDTO = (MainResponseDTO<LinkedHashMap<String, Object>>) serviceDelegateUtil
						.get(RegistrationConstants.GET_PRE_REGISTRATION, requestParamMap, false);

				if (isResponseNotEmpty(mainResponseDTO)) {

					PreRegArchiveDTO preRegArchiveDTO = new ObjectMapper().readValue(
							new JSONObject(mainResponseDTO.getResponse()).toString(), PreRegArchiveDTO.class);

					decryptedPacket = preRegArchiveDTO.getZipBytes();

					/* Get PreRegistrationDTO by taking packet Information */
					PreRegistrationDTO preRegistrationDTO = preRegZipHandlingService
							.encryptAndSavePreRegPacket(preRegistrationId, decryptedPacket);

					// Transaction
					SyncTransaction syncTransaction = syncManager.createSyncTransaction(
							RegistrationConstants.RETRIEVED_PRE_REG_ID, RegistrationConstants.RETRIEVED_PRE_REG_ID,
							triggerPoint, syncJobId);

					// save in Pre-Reg List
					PreRegistrationList preRegistrationList = preparePreRegistration(syncTransaction,
							preRegistrationDTO, lastUpdatedTimeStamp);

					preRegistrationList.setAppointmentDate(
							DateUtils.parseUTCToDate(preRegArchiveDTO.getAppointmentDate(), "yyyy-MM-dd"));

					if (preRegistration == null) {
						preRegistrationDAO.save(preRegistrationList);
					} else {
						preRegistrationList.setId(preRegistration.getId());
						preRegistrationList.setUpdBy(getUserIdFromSession());
						preRegistrationList.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
						preRegistrationDAO.update(preRegistrationList);
					}
					/* set success response */
					setSuccessResponse(responseDTO, RegistrationConstants.PRE_REG_SUCCESS_MESSAGE, null);

				} else if (preRegistration == null) {
					/*
					 * set error message if the packet is not available both in db as well as the
					 * REST service
					 */
					setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
					return;
				}

			} catch (HttpClientErrorException | RegBaseCheckedException | java.io.IOException
					| HttpServerErrorException exception) {

				LOGGER.error("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
						RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						exception.getMessage());

				/* set Error response */
				setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
				return;
			}
		}

		/* Only for Manual Trigger */
		if (!isJob) {
			try {
				if (preRegistration != null && decryptedPacket == null) {
					/*
					 * if the packet is already available,read encrypted packet from disk and
					 * decrypt
					 */
					decryptedPacket = preRegZipHandlingService.decryptPreRegPacket(
							preRegistration.getPacketSymmetricKey(),
							FileUtils.readFileToByteArray(new File(preRegistration.getPacketPath())));
				}

				/* set decrypted packet into Response */
				setPacketToResponse(responseDTO, decryptedPacket, preRegistrationId);

			} catch (IOException exception) {
				LOGGER.error("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - Manual Trigger",
						RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						exception.getMessage());
				setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
				return;
			} catch (RegBaseUncheckedException exception) {
				LOGGER.error("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - Manual Trigger",
						RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						exception.getMessage());
				setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
				return;
			}

		}

		LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Get Pre-Registartion ended");

	}

	private boolean isPacketUpdatedInServer(PreRegistrationList preRegistration) {
		return preRegistration != null && preRegistration.getLastUpdatedPreRegTimeStamp() != null;
	}

	private boolean isFetchToBeTriggered(boolean isOnline, boolean isUpdated, boolean isJob) {
		return isOnline && (!isUpdated || !isJob);
	}

	private String getTriggerPoint(boolean isJob) {
		return isJob ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM : getUserIdFromSession();
	}

	private boolean isResponseNotEmpty(MainResponseDTO<LinkedHashMap<String, Object>> mainResponseDTO) {
		return mainResponseDTO != null && mainResponseDTO.getResponse() != null
				&& mainResponseDTO.getResponse().get("zip-bytes") != null;
	}

	@SuppressWarnings("unused")
	private void setPacketToResponse(ResponseDTO responseDTO, byte[] decryptedPacket, String preRegistrationId) {

		try {
			/* create attributes */
			RegistrationDTO registrationDTO = preRegZipHandlingService.extractPreRegZipFile(decryptedPacket);
			registrationDTO.setPreRegistrationId(preRegistrationId);
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("registrationDto", registrationDTO);
			setSuccessResponse(responseDTO, RegistrationConstants.PRE_REG_SUCCESS_MESSAGE, attributes);
		} catch (RegBaseCheckedException exception) {
			LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
		}

	}

	private PreRegistrationDataSyncDTO prepareDataSyncRequestDTO() {

		// prepare required DTO to send through API
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = new PreRegistrationDataSyncDTO();

		Timestamp reqTime = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		preRegistrationDataSyncDTO.setId(RegistrationConstants.PRE_REGISTRATION_DUMMY_ID);
		preRegistrationDataSyncDTO.setReqTime(dateFormat.format(reqTime));
		preRegistrationDataSyncDTO.setVer(RegistrationConstants.VER);

		PreRegistrationDataSyncRequestDTO preRegistrationDataSyncRequestDTO = new PreRegistrationDataSyncRequestDTO();
		preRegistrationDataSyncRequestDTO.setFromDate(getFromDate(reqTime));
		preRegistrationDataSyncRequestDTO.setRegClientId(RegistrationConstants.REGISTRATION_CLIENT_ID);
		preRegistrationDataSyncRequestDTO.setToDate(getToDate(reqTime));
		preRegistrationDataSyncRequestDTO.setUserId(getUserIdFromSession());

		preRegistrationDataSyncDTO.setDataSyncRequestDto(preRegistrationDataSyncRequestDTO);

		return preRegistrationDataSyncDTO;

	}

	private String getToDate(Timestamp reqTime) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(reqTime);
		cal.add(Calendar.DATE, noOfDays);

		/** To-Date */
		return formatDate(cal);

	}

	private String formatDate(Calendar cal) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		Date toDate = cal.getTime();

		/** To-Date */
		return sdfDate.format(toDate);
	}

	private String getFromDate(Timestamp reqTime) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(reqTime);

		return formatDate(cal);
	}

	private PreRegistrationList preparePreRegistration(SyncTransaction syncTransaction,
			PreRegistrationDTO preRegistrationDTO, Timestamp lastUpdatedTimeStamp) {

		PreRegistrationList preRegistrationList = new PreRegistrationList();

		preRegistrationList.setId(UUID.randomUUID().toString());
		preRegistrationList.setPreRegId(preRegistrationDTO.getPreRegId());
		// preRegistrationList.setAppointmentDate(preRegistrationDTO.getAppointmentDate());
		preRegistrationList.setPacketSymmetricKey(preRegistrationDTO.getSymmetricKey());
		preRegistrationList.setStatusCode(syncTransaction.getStatusCode());
		preRegistrationList.setStatusComment(syncTransaction.getStatusComment());
		preRegistrationList.setPacketPath(preRegistrationDTO.getPacketPath());
		preRegistrationList.setsJobId(syncTransaction.getSyncJobId());
		preRegistrationList.setSynctrnId(syncTransaction.getId());
		preRegistrationList.setLangCode(syncTransaction.getLangCode());
		preRegistrationList.setIsActive(true);
		preRegistrationList.setIsDeleted(false);
		preRegistrationList.setCrBy(syncTransaction.getCrBy());
		preRegistrationList.setCrDtime(new Timestamp(System.currentTimeMillis()));
		preRegistrationList.setLastUpdatedPreRegTimeStamp(lastUpdatedTimeStamp);

		return preRegistrationList;

	}

	public synchronized ResponseDTO fetchAndDeleteRecords() {

		LOGGER.info(
				"REGISTRATION - PRE_REGISTRATION_DATA_DELETION_RECORD_FETCH_STARTED - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching the records started");

		ResponseDTO responseDTO = new ResponseDTO();
		// Set the Date 15 days before the current date
		Calendar startCal = Calendar.getInstance();
		startCal.add(Calendar.DATE,
				-(Integer.parseInt(getGlobalConfigValueOf(RegistrationConstants.PRE_REG_DELETION_CONFIGURED_DAYS))));

		Date startDate = Date.from(startCal.toInstant());

		// fetch the records that needs to be deleted
		List<PreRegistrationList> preRegList = preRegistrationDAO.fetchRecordsToBeDeleted(startDate);

		LOGGER.info(
				"REGISTRATION - PRE_REGISTRATION_DATA_DELETION_RECORD_FETCH_ENDED - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching the records ended");

		deletePreRegRecords(responseDTO, preRegList);

		return responseDTO;
	}

	private void deletePreRegRecords(ResponseDTO responseDTO, final List<PreRegistrationList> preRegList) {
		LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_DELETION_STARTED - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Deletion of records started");

		if (!isNull(preRegList) && !isEmpty(preRegList)) {

			/* Registartions to be deleted */
			List<PreRegistrationList> preRegistartionsToBeDeletedList = new LinkedList<>();

			for (PreRegistrationList preRegRecord : preRegList) {

				/* Get File to be deleted from pre registartion */
				File preRegPacket = new File(preRegRecord.getPacketPath());
				if (preRegPacket.exists() && preRegPacket.delete()) {
					preRegistartionsToBeDeletedList.add(preRegRecord);
				}

			}

			if (!isEmpty(preRegistartionsToBeDeletedList)) {
				deleteRecords(responseDTO, preRegistartionsToBeDeletedList);
			} else {
				/* Set Error response */
				setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_DELETE_FAILURE, null);

			}
		} else {
			setSuccessResponse(responseDTO, RegistrationConstants.PRE_REG_DELETE_SUCCESS, null);
		}

		LOGGER.info("REGISTRATION - PRE_REGISTRATION_DATA_DELETION_ENDED - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Deletion of records Ended");

	}

	private ResponseDTO deleteRecords(ResponseDTO responseDTO, List<PreRegistrationList> preRegList) {

		LOGGER.info(
				"REGISTRATION - PRE_REGISTRATION_DATA_DELETION_UPDATE_STARTED - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"deleted records started");

		try {

			/* Delete All Pre Registartions which were under to be deleted state */
			preRegistrationDAO.deleteAll(preRegList);

			/* Set Success Response */
			setSuccessResponse(responseDTO, RegistrationConstants.PRE_REG_DELETE_SUCCESS, null);
		} catch (RuntimeException runtimeException) {
			LOGGER.info("REGISTRATION - PRE_REGISTRATION_DELETE - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage());
			/* Set Error response */
			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_DELETE_FAILURE, null);
		}

		LOGGER.info(
				"REGISTRATION - PRE_REGISTRATION_DATA_DELETION_UPDATE_ENDED - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID, "deleted records ended");

		return responseDTO;
	}

}
