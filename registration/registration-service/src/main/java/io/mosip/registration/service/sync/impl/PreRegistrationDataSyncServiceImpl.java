package io.mosip.registration.service.sync.impl;

import java.io.File;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncRequestDTO;
import io.mosip.registration.dto.PreRegistrationResponseDTO;
import io.mosip.registration.dto.PreRegistrationResponseDataSyncDTO;
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
	public ResponseDTO getPreRegistrationIds(String syncJobId) {

		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration Id's started");

		ResponseDTO responseDTO = new ResponseDTO();

		/** prepare request DTO to pass on through REST call */
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = prepareDataSyncRequestDTO();

		try {

			/** REST call to get Pre Registartion Id's */
			PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO> preRegistrationResponseDTO = (PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO>) serviceDelegateUtil
					.post(RegistrationConstants.GET_PRE_REGISTRATION_IDS, preRegistrationDataSyncDTO);

			/** TODO Need to change to DTO response, Once Kernel team changes */
			ArrayList<?> preRegistrationResponseList = (ArrayList<?>) preRegistrationResponseDTO.getResponse();
			HashMap<String, Object> map = (HashMap<String, Object>) preRegistrationResponseList.get(0);

			ArrayList<String> preRegIds = (ArrayList<String>) map.get("preRegistrationIds");

			/** Get Packets Using pre registration ID's */
			for (String preRegistrationId : preRegIds) {
				getPreRegistration(responseDTO, preRegistrationId, syncJobId);
				if (responseDTO.getErrorResponseDTOs() != null) {
					break;
				}

			}

		} catch (HttpClientErrorException | ResourceAccessException | SocketTimeoutException
				| RegBaseCheckedException exception) {

			LOGGER.error("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());

			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_ID_ERROR, null);
		}

		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
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

		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration started");

		ResponseDTO responseDTO = new ResponseDTO();

		/** Get Pre Registration Packet */
		getPreRegistration(responseDTO, preRegistrationId, RegistrationConstants.JOB_TRIGGER_POINT_USER);

		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration completed");

		return responseDTO;
	}

	private void getPreRegistration(ResponseDTO responseDTO, String preRegistrationId, String syncJobId) {

		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Fetching Pre-Registration started");

		/** Check in Database whether required record already exists or not */
		PreRegistrationList preRegistration = preRegistrationDAO.getPreRegistration(preRegistrationId);

		byte[] encryptedPacket = null;
		
		String symmetricKey=null;

		boolean isJob = (!RegistrationConstants.JOB_TRIGGER_POINT_USER.equals(syncJobId));

		/** Get Packet From REST call */
		if (preRegistration == null) {

			/** prepare request params to pass through URI */
			Map<String, String> requestParamMap = new HashMap<>();
			requestParamMap.put(RegistrationConstants.PRE_REGISTRATION_ID, preRegistrationId);

			String triggerPoint = isJob ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM : getUserIdFromSession();

			try {
				/** REST call to get packet */
				byte[] packet = (byte[]) serviceDelegateUtil.get(RegistrationConstants.GET_PRE_REGISTRATION, requestParamMap);

				if (packet != null) {

					/** Get PreRegistrationDTO by taking packet Information */
					PreRegistrationDTO preRegistrationDTO = preRegZipHandlingService.encryptAndSavePreRegPacket(preRegistrationId,
							packet);
					
					encryptedPacket = preRegistrationDTO.getEncryptedPacket();
					symmetricKey = preRegistrationDTO.getSymmetricKey();
					
					// Transaction
					SyncTransaction syncTransaction = syncManager.createSyncTransaction(
							RegistrationConstants.RETRIEVED_PRE_REG_ID, RegistrationConstants.RETRIEVED_PRE_REG_ID,
							triggerPoint, syncJobId);

					// save in Pre-Reg List
					PreRegistrationList preRegistrationList = preparePreRegistration(syncTransaction,
							preRegistrationDTO);
					preRegistrationDAO.savePreRegistration(preRegistrationList);

					/** set success response */
					setSuccessResponse(responseDTO, RegistrationConstants.PRE_REG_SUCCESS_MESSAGE, null);

				} else if (!isJob) {
					/*
					 * set error message if the packet is not available both in db as well as the
					 * REST service
					 */
					setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
					return;
				}

			} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException exception) {

				LOGGER.error("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
						RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						exception.getMessage());

				// Transaction
				syncManager.createSyncTransaction(RegistrationConstants.UNABLE_TO_RETRIEVE_PRE_REG_ID,
						RegistrationConstants.UNABLE_TO_RETRIEVE_PRE_REG_ID, triggerPoint, syncJobId);

				/** set Error response */
				setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
				return;
			}
		}

		/** Only for Manual Trigger */
		if (!isJob) {
			try {
				if (preRegistration != null) {

					encryptedPacket = FileUtils.readFileToByteArray(new File(preRegistration.getPacketPath()));
					symmetricKey = preRegistration.getPacketSymmetricKey();

				}

				byte[] decryptedPacket = preRegZipHandlingService.decryptPreRegPacket(symmetricKey, encryptedPacket);
				/** set decrypted packet into Response */
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

		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Get Pre-Registartion ended");

	}

	@SuppressWarnings("unused")
	private void setPacketToResponse(ResponseDTO responseDTO, byte[] decryptedPacket, String preRegistrationId) {

		try {
			if (decryptedPacket != null) {
				/** create attributes */
				RegistrationDTO registrationDTO = preRegZipHandlingService.extractPreRegZipFile(decryptedPacket);
				registrationDTO.setPreRegistrationId(preRegistrationId);
				Map<String, Object> attributes = new HashMap<>();
				attributes.put("registrationDTO", registrationDTO);
				setSuccessResponse(responseDTO, RegistrationConstants.PRE_REG_SUCCESS_MESSAGE, attributes);
			}
		} catch (RegBaseCheckedException exception) {
			LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
		}catch (RuntimeException exception) {
			LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
		}

	}

	private PreRegistrationDataSyncDTO prepareDataSyncRequestDTO() {

		// prepare required DTO to send through API
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = new PreRegistrationDataSyncDTO();

		Timestamp reqTime = new Timestamp(System.currentTimeMillis());
		preRegistrationDataSyncDTO.setId(RegistrationConstants.PRE_REGISTRATION_DUMMY_ID);
		preRegistrationDataSyncDTO.setReqTime(reqTime);
		preRegistrationDataSyncDTO.setVer(RegistrationConstants.VER.get(0));

		PreRegistrationDataSyncRequestDTO preRegistrationDataSyncRequestDTO = new PreRegistrationDataSyncRequestDTO();
		preRegistrationDataSyncRequestDTO.setFromDate(reqTime);
		preRegistrationDataSyncRequestDTO.setRegClientId(RegistrationConstants.REGISTRATION_CLIENT_ID);
		preRegistrationDataSyncRequestDTO.setToDate(getToDate(preRegistrationDataSyncRequestDTO.getFromDate()));
		preRegistrationDataSyncRequestDTO.setUserId(getUserIdFromSession());

		preRegistrationDataSyncDTO.setDataSyncRequestDto(preRegistrationDataSyncRequestDTO);

		return preRegistrationDataSyncDTO;

	}

	private Timestamp getToDate(Timestamp fromDate) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		cal.add(Calendar.DATE, noOfDays);

		/** To-Date */
		return new Timestamp(cal.getTime().getTime());

	}

	private PreRegistrationList preparePreRegistration(SyncTransaction syncTransaction,
			PreRegistrationDTO preRegistrationDTO) {

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
		preRegistrationList.setCrBy(syncTransaction.getCrBy());
		preRegistrationList.setCrDtime(new Timestamp(System.currentTimeMillis()));

		return preRegistrationList;

	}

}
