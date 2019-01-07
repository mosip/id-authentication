package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.util.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationTransactionType;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.RegPacketStatusService;

/**
 * This class will update the packet status in the table after sync with the
 * server.
 * 
 * @author Himaja Dhanyamraju
 */
@Service
public class RegPacketStatusServiceImpl extends BaseService implements RegPacketStatusService {

	@Autowired
	private RegPacketStatusDAO regPacketStatusDAO;

	@Autowired
	private RegistrationDAO registrationDAO;

	private static final Logger LOGGER = AppConfig.getLogger(RegPacketStatusServiceImpl.class);

	/**
	 * Required no days to maintain registrations
	 */
	@Value("${REG_NO_OF_DAYS_LIMIT_TO_DELETE}")
	private int noOfDays;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.packet.RegistrationDeletionService#
	 * deleteReRegistrationPackets()
	 */
	@Override
	public ResponseDTO deleteReRegistrationPackets() {

		ResponseDTO responseDTO = new ResponseDTO();

		Timestamp reqTime = new Timestamp(System.currentTimeMillis());

		List<Registration> registrations = registrationDAO.getRegistrationsToBeDeleted(
				getPacketDeletionLastDate(reqTime), RegistrationClientStatusCode.DELETED.getCode());

		try {
			for (Registration registration : registrations) {
				delete(registration, registration.getStatusCode(), true);
			}
			setSuccessResponse(responseDTO, RegistrationConstants.REGISTRATION_DELETION_BATCH_JOBS_SUCCESS, null);

		} catch (RuntimeException runtimeException) {

			setErrorResponse(responseDTO, RegistrationConstants.REGISTRATION_DELETION_BATCH_JOBS_SUCCESS, null);
		}

		return responseDTO;

	}

	private Timestamp getPacketDeletionLastDate(Timestamp reqTime) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(reqTime);
		cal.add(Calendar.DATE, -noOfDays);

		/** To-Date */
		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * Get all registrationIDs for which the clientStatus is post-sync
	 *
	 * @return List<String> list of registrationId's required for packet status sync
	 *         with server
	 */
	private List<String> getPacketIds() {
		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status post-sync has been ended");

		List<Registration> registrationList = regPacketStatusDAO.getPacketIdsByStatusUploaded();

		List<String> packetIds = new ArrayList<>();
		for (Registration registration : registrationList) {
			packetIds.add(registration.getId());
		}
		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
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
	private void updatePacketIdsByServerStatus(List<LinkedHashMap<String, String>> registrations)
			throws RegBaseUncheckedException {
		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"packets status sync from server has been started");

		List<RegPacketStatusDTO> packetStatusDTO = new ArrayList<>();
		for (Map<String, String> registration : registrations) {
			packetStatusDTO.add(
					new RegPacketStatusDTO(registration.get(RegistrationConstants.PACKET_STATUS_SYNC_REGISTRATION_ID),
							registration.get(RegistrationConstants.PACKET_STATUS_SYNC_STATUS_CODE)));
		}

		try {
			for (RegPacketStatusDTO regPacketStatusDTO : packetStatusDTO) {
				Registration registration = regPacketStatusDAO.get(regPacketStatusDTO.getPacketId());
				registration.setServerStatusCode(regPacketStatusDTO.getStatus());
				registration.setServerStatusTimestamp(new Timestamp(System.currentTimeMillis()));

				delete(registration, regPacketStatusDTO.getStatus(), false);

			}
			LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, "packets status sync from server has been ended");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, runtimeException.getMessage());

		}

	}

	@SuppressWarnings("unchecked")
	public ResponseDTO packetSyncStatus() {

		LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"packet status sync called");

		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();
		List<String> packetIds = getPacketIds();
		LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"PacketIds for sync with server have been retrieved");
		/** Create Response to Return to UI layer */
		ResponseDTO response = new ResponseDTO();
		SuccessResponseDTO successResponse;

		/** Validator response service API creation */
		final String SERVICE_NAME = RegistrationConstants.PACKET_STATUS_SYNC_SERVICE_NAME;

		/** prepare request params to pass through URI */
		Map<String, String> requestParamMap = new HashMap<>();
		String packetIdList = packetIds.stream().map(Object::toString).collect(Collectors.joining(","));
		requestParamMap.put(RegistrationConstants.PACKET_STATUS_SYNC_URL_PARAMETER, packetIdList);

		try {
			/** Obtain RegistrationStatusDTO from service delegate util */
			registrations = (List<LinkedHashMap<String, String>>) serviceDelegateUtil.get(SERVICE_NAME,
					requestParamMap);
			if (!registrations.isEmpty()) {
				/** update the status of packets after sync with server */
				try {
					updatePacketIdsByServerStatus(registrations);
				} catch (RegBaseUncheckedException regBaseUncheckedException) {
					getErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE);
				}
				LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
						"packet status has been synced with server");
				/** Create Success response */
				successResponse = new SuccessResponseDTO();
				successResponse.setCode(RegistrationConstants.ALERT_INFORMATION);
				successResponse.setMessage(RegistrationConstants.PACKET_STATUS_SYNC_SUCCESS_MESSAGE);
				Map<String, Object> otherAttributes = new HashMap<>();
				otherAttributes.put(RegistrationConstants.PACKET_STATUS_SYNC_RESPONSE_ENTITY, registrations);
				successResponse.setOtherAttributes(otherAttributes);
				response.setSuccessResponseDTO(successResponse);
				LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
						"Success Response Created");
			} else {
				/** Create Error response */
				getErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE);
				LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
						"Error Response Created");
			}

		} catch (SocketTimeoutException | RegBaseCheckedException | IllegalArgumentException | HttpClientErrorException
				| ResourceAccessException exception) {
			/** Create Error response */
			getErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE);
			LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
					"Error Response Created");
		}
		LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"Packet Status Sync ended");

		return response;
	}

	private Registration delete(final Registration registration, final String clientStatus,
			final boolean isToBeDeleted) {

		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Delete Registration Packet started");

		/** Get Registration Transaction List for each transaction */
		List<RegistrationTransaction> transactionList = registration.getRegistrationTransaction();
		if (transactionList == null) {
			transactionList = new LinkedList<>();
		}
		/** Prepare Registration Transaction */
		RegistrationTransaction registrationTxn = new RegistrationTransaction();

		registrationTxn.setRegId(registration.getId());
		registrationTxn.setTrnTypeCode(RegistrationTransactionType.CREATED.getCode());
		registrationTxn.setLangCode("ENG");
		registrationTxn.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
		registrationTxn.setCrDtime(new Timestamp(System.currentTimeMillis()));

		File ackFile = null;
		File zipFile = null;

		/**
		 * Check whether the requirement matches to delete the registration packet or
		 * not
		 */
		if (clientStatus.equalsIgnoreCase(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED) || isToBeDeleted) {

			registration.setClientStatusCode(RegistrationClientStatusCode.DELETED.getCode());
			registrationTxn.setStatusCode(registration.getClientStatusCode());
			String ackPath = registration.getAckFilename();
			ackFile = new File(ackPath);
			String zipPath = ackPath.replace("_Ack.png", RegistrationConstants.ZIP_FILE_EXTENSION);
			zipFile = new File(zipPath);
		} else {
			registrationTxn.setStatusCode(registration.getClientStatusCode());
		}
		transactionList.add(registrationTxn);
		registration.setRegistrationTransaction(transactionList);
		Registration updatedRegistration = regPacketStatusDAO.update(registration);
		if (ackFile != null && updatedRegistration != null) {
			Files.delete(ackFile);
			Files.delete(zipFile);
		}

		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Delete Registration Packet ended");

		return updatedRegistration;

	}

}
