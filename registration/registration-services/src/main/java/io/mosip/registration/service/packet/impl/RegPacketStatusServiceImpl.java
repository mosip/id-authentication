package io.mosip.registration.service.packet.impl;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.assertj.core.util.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationTransactionType;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.PacketStatusReaderDTO;
import io.mosip.registration.dto.RegistrationIdDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.security.AESEncryptionService;
import io.mosip.registration.service.sync.PacketSynchService;

/**
 * The implementation class of {@link RegPacketStatusService}to update status of
 * the registration packets based on Packet Status Reader service and delete the
 * Registration Packets based on the status of the packets
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Service
public class RegPacketStatusServiceImpl extends BaseService implements RegPacketStatusService {

	@Autowired
	private RegPacketStatusDAO regPacketStatusDAO;

	@Autowired
	private RegistrationDAO registrationDAO;

	@Autowired
	private PacketSynchService packetSynchService;

	@Autowired
	private AESEncryptionService aesEncryptionService;

	private static final Logger LOGGER = AppConfig.getLogger(RegPacketStatusServiceImpl.class);

	private HashMap<String, Registration> registrationMap = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.packet.RegPacketStatusService#
	 * deleteRegistrationPackets()
	 */
	@Override
	public synchronized ResponseDTO deleteRegistrationPackets() {

		LOGGER.info(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID, "Delete  Reg-packets started");

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			/* Get Registrations to be deleted */
			List<Registration> registrations = registrationDAO.get(
					getPacketDeletionLastDate(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime())),
					RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);

			if (!isNull(registrations) && !isEmpty(registrations)) {
				deleteRegistrations(registrations);

			}

			setSuccessResponse(responseDTO, RegistrationConstants.REGISTRATION_DELETION_BATCH_JOBS_SUCCESS, null);

		} catch (RuntimeException runtimeException) {

			LOGGER.error(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

			setErrorResponse(responseDTO, RegistrationConstants.REGISTRATION_DELETION_BATCH_JOBS_FAILURE, null);
		}

		LOGGER.info(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID, "Delete  Reg-packets ended");

		return responseDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.packet.RegPacketStatusService#
	 * deleteAllProcessedRegPackets()
	 */
	@Override
	public void deleteAllProcessedRegPackets() {

		LOGGER.info("REGISTRATION - DELETE-PACKETS-WHEN-MACHINE-REMAPPED - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME,
				APPLICATION_ID, "packet deletion when the machine is remapped is started");

		List<Registration> registrations = registrationDAO
				.findByServerStatusCodeIn(RegistrationConstants.PACKET_STATUS_CODES_FOR_REMAPDELETE);
		if (registrations != null && !registrations.isEmpty()) {

			for (Registration registration : registrations) {

				delete(registration);
			}
		}

	}

	private Timestamp getPacketDeletionLastDate(Timestamp reqTime) {

		/* Get Calendar instance */
		Calendar cal = Calendar.getInstance();
		cal.setTime(reqTime);
		cal.add(Calendar.DATE,
				-(Integer.parseInt(getGlobalConfigValueOf(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS))));

		/* To-Date */
		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * Get all registrationIDs for which the clientStatus is post-sync
	 *
	 * @return List<String> list of registrationId's required for packet status sync
	 *         with server
	 */
	private List<String> getPacketIds() {
		LOGGER.info(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status post-sync has been ended");

		List<Registration> registrationList = regPacketStatusDAO.getPacketIdsByStatusUploaded();

		List<String> packetIds = new ArrayList<>();
		for (Registration registration : registrationList) {
			String registrationId = registration.getId();

			registrationMap.put(registrationId, registration);
			packetIds.add(registrationId);
		}
		LOGGER.info(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status post-sync has been ended");
		return packetIds;
	}

	/**
	 * update status for all packets that are synced with server
	 *
	 * @param registrations
	 *            list of registration entities which are represented as
	 *            LinkedHashMap which maps the attributes of registration entity to
	 *            their respective values that are obtained after sync with server
	 */
	private void updatePacketIdsByServerStatus(List<LinkedHashMap<String, String>> registrationStatuses) {
		LOGGER.info(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID,
				"packets status sync from server has been started");

		try {
			for (LinkedHashMap<String, String> registrationStatus : registrationStatuses) {
				Registration registration = registrationMap
						.get(registrationStatus.get(RegistrationConstants.PACKET_STATUS_READER_REGISTRATION_ID));
				registration.setServerStatusCode(
						registrationStatus.get(RegistrationConstants.PACKET_STATUS_READER_STATUS_CODE));
				registration.setServerStatusTimestamp(new Timestamp(System.currentTimeMillis()));

				updateRegistration(registration,
						registrationStatus.get(RegistrationConstants.PACKET_STATUS_READER_STATUS_CODE));
			}

			LOGGER.info(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID,
					"packets status sync from server has been ended");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_UPDATE_STATUS,
					runtimeException.toString());

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.RegPacketStatusService#packetSyncStatus(
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public synchronized ResponseDTO packetSyncStatus(String triggerPoint) throws RegBaseCheckedException {

		LOGGER.info(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID, "packet status sync called");

		/* Create Response to Return to UI layer */
		ResponseDTO response = new ResponseDTO();

		if (validateTriggerPoint(triggerPoint)) {
			List<String> packetIds = getPacketIds();
			LOGGER.info(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"PacketIds for sync with server have been retrieved");

			SuccessResponseDTO successResponse;

			/* Validator response service API creation */
			final String SERVICE_NAME = RegistrationConstants.PACKET_STATUS_SYNC_SERVICE_NAME;

			PacketStatusReaderDTO packetStatusReaderDTO = new PacketStatusReaderDTO();
			packetStatusReaderDTO.setId(RegistrationConstants.PACKET_STATUS_READER_ID);
			packetStatusReaderDTO.setVersion(RegistrationConstants.PACKET_SYNC_VERSION);
			packetStatusReaderDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());

			List<RegistrationIdDTO> registrationIdDTOs = new ArrayList<>();
			for (String packetId : packetIds) {
				RegistrationIdDTO registrationIdDTO = new RegistrationIdDTO();
				registrationIdDTO.setRegistrationId(packetId);
				registrationIdDTOs.add(registrationIdDTO);
			}

			packetStatusReaderDTO.setRequest(registrationIdDTOs);

			try {
				if (!packetIds.isEmpty()) {
					/* Obtain RegistrationStatusDTO from service delegate util */
					LinkedHashMap<String, Object> packetStatusResponse = (LinkedHashMap<String, Object>) serviceDelegateUtil
							.post(SERVICE_NAME, packetStatusReaderDTO, triggerPoint);
					List<LinkedHashMap<String, String>> registrations = (List<LinkedHashMap<String, String>>) packetStatusResponse
							.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE);
					if (registrations != null && !registrations.isEmpty()) {
						/* update the status of packets after sync with server */
						try {
							updatePacketIdsByServerStatus(registrations);
						} catch (RegBaseUncheckedException regBaseUncheckedException) {
							LOGGER.error(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID,
									regBaseUncheckedException.getMessage()
											+ ExceptionUtils.getStackTrace(regBaseUncheckedException));

							setErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, null);
							return response;
						}
						/* Create Success response */
						successResponse = new SuccessResponseDTO();
						successResponse.setCode(RegistrationConstants.ALERT_INFORMATION);
						successResponse.setMessage(RegistrationConstants.PACKET_STATUS_SYNC_SUCCESS_MESSAGE);
						Map<String, Object> otherAttributes = new WeakHashMap<>();
						otherAttributes.put(RegistrationConstants.PACKET_STATUS_SYNC_RESPONSE_ENTITY, registrations);
						successResponse.setOtherAttributes(otherAttributes);
						response.setSuccessResponseDTO(successResponse);
						LOGGER.info(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID,
								"Success Response Created");
					} else {
						/* Create Error response */
						setErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, null);
						return response;
					}
				} else {
					/* If there are no uploaded packets to check the status from the server */
					successResponse = new SuccessResponseDTO();
					successResponse.setCode(RegistrationConstants.ALERT_INFORMATION);
					successResponse.setMessage(RegistrationConstants.PACKET_STATUS_SYNC_SUCCESS_MESSAGE);
					Map<String, Object> otherAttributes = new WeakHashMap<>();
					/* sending empty success response as there are no packets to check status */
					otherAttributes.put(RegistrationConstants.PACKET_STATUS_SYNC_RESPONSE_ENTITY,
							RegistrationConstants.EMPTY);
					successResponse.setOtherAttributes(otherAttributes);
					response.setSuccessResponseDTO(successResponse);
					LOGGER.info(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID,
							"Success Response Created");
				}
			} catch (SocketTimeoutException | RegBaseCheckedException | IllegalArgumentException
					| HttpClientErrorException | HttpServerErrorException | ResourceAccessException exception) {
				LOGGER.error(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));

				setErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, null);
				return response;
			} catch (RuntimeException runtimeException) {
				LOGGER.error(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

				setErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, null);
				return response;
			}
			LOGGER.info(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID, "Packet Status Sync ended");
			return response;
		} else {
			LOGGER.error(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"Trigger point cannot be empty or null");
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_TRIGGER_POINT_MISSING.getErrorCode(),
					RegistrationExceptionConstants.REG_TRIGGER_POINT_MISSING.getErrorMessage());
		}
	}

	private boolean validateTriggerPoint(String triggerPoint) {
		if (StringUtils.isEmpty(triggerPoint)) {
			LOGGER.info(LoggerConstants.LOG_PKT_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"Trigger point is empty or null");
			return false;
		}
		return true;
	}

	private Registration updateRegistration(final Registration registration, final String serverStatus) {

		LOGGER.info(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID,
				"Delete Registration Packet started");

		/* Get Registration Transaction List for each transaction */
		List<RegistrationTransaction> transactionList = registration.getRegistrationTransaction();
		if (isNull(transactionList)) {
			transactionList = new LinkedList<>();
		}
		/* Prepare Registration Transaction */
		RegistrationTransaction registrationTxn = new RegistrationTransaction();

		registrationTxn.setRegId(registration.getId());
		registrationTxn.setTrnTypeCode(RegistrationTransactionType.CREATED.getCode());
		registrationTxn.setLangCode("ENG");
		registrationTxn.setCrBy(SessionContext.userContext().getUserId());
		registrationTxn.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));

		registrationTxn.setStatusCode(serverStatus);

		transactionList.add(registrationTxn);
		registration.setRegistrationTransaction(transactionList);

		Registration updatedRegistration = regPacketStatusDAO.update(registration);
		LOGGER.info(LoggerConstants.LOG_PKT_DELETE, APPLICATION_NAME, APPLICATION_ID,
				"Delete Registration Packet ended");

		return updatedRegistration;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.packet.RegPacketStatusService#
	 * deleteRegistrations(java.util.List)
	 */
	@Override
	public void deleteRegistrations(final List<Registration> registrations) {
		for (Registration registration : registrations) {

			if (registration.getServerStatusCode()
					.equalsIgnoreCase(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED)) {
				/* Delete Registration */
				delete(registration);
			}
		}

	}

	private void delete(Registration registration) {
		File ackFile = null;
		File zipFile = null;
		String ackPath = registration.getAckFilename();
		ackFile = FileUtils.getFile(ackPath);
		String zipPath = ackPath.replace("_Ack.html", RegistrationConstants.ZIP_FILE_EXTENSION);
		zipFile = FileUtils.getFile(zipPath);

		if (zipFile.exists()) {

			Files.delete(ackFile);
			Files.delete(zipFile);

		}

		/* Delete row from DB */
		regPacketStatusDAO.delete(registration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.RegPacketStatusService#syncPacket(java.
	 * lang.String)
	 */
	public ResponseDTO syncPacket(String triggerPoint) {

		LOGGER.debug("REGISTRATION - SYNC_PACKETS_TO_SERVER - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME,
				APPLICATION_ID, "Sync the packets to the server");
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		List<ErrorResponseDTO> errorList = new ArrayList<>();
		try {

			List<Registration> packetsToBeSynched = registrationDAO
					.getPacketsToBeSynched(RegistrationConstants.PACKET_STATUS);
			List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
			List<PacketStatusDTO> packetDto = new ArrayList<>();
			List<PacketStatusDTO> synchedPackets = new ArrayList<>();
			for (Registration reg : packetsToBeSynched) {
				packetDto.add(packetStatusDtoPreperation(reg));
			}
			ResponseDTO response = new ResponseDTO();
			if (!packetDto.isEmpty()) {

				for (PacketStatusDTO packetToBeSynch : packetDto) {
					SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
					syncDto.setLangCode(getGlobalConfigValueOf(RegistrationConstants.PRIMARY_LANGUAGE));
					syncDto.setRegistrationId(packetToBeSynch.getFileName());
					syncDto.setRegistrationType(packetToBeSynch.getPacketStatus().toUpperCase());
					syncDto.setPacketHashValue(packetToBeSynch.getPacketHash());
					syncDto.setPacketSize(packetToBeSynch.getPacketSize());
					if (RegistrationClientStatusCode.RE_REGISTER.getCode()
							.equalsIgnoreCase(packetToBeSynch.getPacketClientStatus())) {
						syncDto.setSupervisorStatus(RegistrationConstants.CLIENT_STATUS_APPROVED);
					} else {
						syncDto.setSupervisorStatus(packetToBeSynch.getSupervisorStatus());
					}
					syncDto.setSupervisorComment(packetToBeSynch.getSupervisorComments());
					syncDtoList.add(syncDto);
				}
				RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
				registrationPacketSyncDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
				registrationPacketSyncDTO.setSyncRegistrationDTOs(syncDtoList);
				registrationPacketSyncDTO.setId(RegistrationConstants.PACKET_SYNC_STATUS_ID);
				registrationPacketSyncDTO.setVersion(RegistrationConstants.PACKET_SYNC_VERSION);
				response = packetSynchService.syncPacketsToServer(CryptoUtil.encodeBase64(
						aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes())),
						triggerPoint);
			} else {
				response.setSuccessResponseDTO(new SuccessResponseDTO());
			}
			if (response != null && response.getSuccessResponseDTO() != null) {
				for (PacketStatusDTO registration : packetDto) {
					String status = (String) response.getSuccessResponseDTO().getOtherAttributes()
							.get(registration.getFileName());
					if (status != null && status.equalsIgnoreCase(RegistrationConstants.SUCCESS)) {

						registration.setPacketClientStatus(RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode());

						synchedPackets.add(registration);
					}
				}
				packetSynchService.updateSyncStatus(synchedPackets);
				successResponseDTO.setMessage(RegistrationConstants.SUCCESS);
				responseDTO.setSuccessResponseDTO(successResponseDTO);
			}
			LOGGER.debug("REGISTRATION - SYNC_PACKETS_TO_SERVER_END - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, "Sync the packets to the server ending");

		} catch (RegBaseUncheckedException | RegBaseCheckedException | JsonProcessingException
				| URISyntaxException exception) {
			LOGGER.error("REGISTRATION - SYNC_PACKETS_TO_SERVER - REG_PACKET_STATUS_SYNC", APPLICATION_NAME,
					APPLICATION_ID, exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setMessage(exception.getMessage());
			errorList.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorList);
		}
		return responseDTO;
	}

}
