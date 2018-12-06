package io.mosip.registration.service.sync.impl;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.ws.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncRequestDTO;
import io.mosip.registration.dto.PreRegistrationResponseDTO;
import io.mosip.registration.dto.PreRegistrationResponseDataSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@Service
public class PreRegistrationDataSyncServiceImpl extends BaseService implements PreRegistrationDataSyncService {

	@Autowired
	PreRegistrationDataSyncDAO preRegistrationDAO;

	@Autowired
	SyncManager syncManager;

	@Value("${PRE_REG_NO_OF_DAYS_LIMIT}")
	private int noOfDays;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#
	 * getPreRegistrationIds()
	 */
	@Override
	public ResponseDTO getPreRegistrationIds() {
		return getPreRegistrationIds(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#
	 * getPreRegistrationIds(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResponseDTO getPreRegistrationIds(String syncJobId) {

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
				| RegBaseCheckedException e) {
			setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_ID_ERROR, null);
		}

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

		ResponseDTO responseDTO = new ResponseDTO();

		/** Get Pre Registration Packet */
		getPreRegistration(responseDTO, preRegistrationId, null);

		return responseDTO;
	}

	private void getPreRegistration(ResponseDTO responseDTO, String preRegistrationId, String syncJobId) {

		/** Check in Database whether required record already exists or not */
		PreRegistrationList preRegistration = preRegistrationDAO.getPreRegistration(preRegistrationId);

		byte[] encryptedPacket = null;
		
		boolean isJob = (syncJobId!=null);


		/** Get Packet From REST call */
		if (preRegistration == null) {

			/** prepare request params to pass through URI */
			Map<String, String> requestParamMap = new HashMap<>();
			requestParamMap.put(RegistrationConstants.PRE_REGISTRATION_ID, preRegistrationId);

			byte[] packet = null;
			PreRegistrationDTO preRegistrationDTO = null;
			
			
			try {
				/** REST call to get packet */
				packet = (byte[]) serviceDelegateUtil.get(RegistrationConstants.GET_PRE_REGISTRATION, requestParamMap);

				String triggerPoint = (syncJobId != null) ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM
						: getUserIdFromSession();
				syncJobId  = isJob ? syncJobId : RegistrationConstants.JOB_TRIGGER_POINT_USER;

				if (packet != null) {

					/** TODO Bala : Get PreRegistrationDTO by taking packet Information */
					preRegistrationDTO = null;

					/** TODO Set encrypted Packet using DTO */

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

				} else {
					// Transaction
					syncManager.createSyncTransaction(RegistrationConstants.UNABLE_TO_RETRIEVE_PRE_REG_ID,
							RegistrationConstants.UNABLE_TO_RETRIEVE_PRE_REG_ID, triggerPoint, syncJobId);

					/** set Error response */
					setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
					return;
				}

			} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException e) {

				/** set Error response */
				setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
				return;
			}
		}

		/** Manual Trigger */
		if (!isJob) {

			if (encryptedPacket == null) {

				/** TODO get encrypted packet by giving packetPath information using Entity */
				encryptedPacket = null;
				
			}

			/** get decrypted packet into Response */
			getPacket(responseDTO, encryptedPacket, preRegistrationId);
		}

	}

	private void getPacket(ResponseDTO responseDTO, byte[] encryptedPacket, String preRegistrationId) {
		/** TODO get decrypted packet by giving encrypted packet info */
		byte[] decryptedPacket = null;

		/** create attributes */
		Map<String, Object> attributes = new HashMap<>();
		attributes.put(preRegistrationId, decryptedPacket);
		setSuccessResponse(responseDTO, RegistrationConstants.PRE_REG_SUCCESS_MESSAGE, attributes);

	}

	private PreRegistrationDataSyncDTO prepareDataSyncRequestDTO() {

		Timestamp reqTime = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			Date parsedDate = dateFormat.parse("2018-01-17T07:22:57.086+0000");
			reqTime = new Timestamp(parsedDate.getTime());

		} catch (Exception e) {
			e.printStackTrace();
		}

		// prepare required DTO to send through API
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = new PreRegistrationDataSyncDTO();

		//Timestamp reqTime = new Timestamp(System.currentTimeMillis());
		preRegistrationDataSyncDTO.setId(RegistrationConstants.PRE_REGISTRATION_DUMMY_ID);
		preRegistrationDataSyncDTO.setReqTime(reqTime);
		preRegistrationDataSyncDTO.setVer(RegistrationConstants.VER);

		PreRegistrationDataSyncRequestDTO preRegistrationDataSyncRequestDTO = new PreRegistrationDataSyncRequestDTO();
		preRegistrationDataSyncRequestDTO.setFromDate(reqTime);
		preRegistrationDataSyncRequestDTO.setRegClientId(RegistrationConstants.REGISTRATION_CLIENT_ID);
		preRegistrationDataSyncRequestDTO.setToDate(getToDate(preRegistrationDataSyncRequestDTO.getFromDate()));
		preRegistrationDataSyncRequestDTO.setUserId(getUserIdFromSession());

		preRegistrationDataSyncDTO.setDataSyncRequestDto(preRegistrationDataSyncRequestDTO);

		return preRegistrationDataSyncDTO;

	}

	private Timestamp getToDate(Timestamp fromDate) {

		Timestamp toDate = null;

		toDate = new Timestamp(System.currentTimeMillis());

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			Date parsedDate = dateFormat.parse("2018-11-17T07:22:57.086+0000");
			toDate = new Timestamp(parsedDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toDate;

		/*
		 * Calendar cal = Calendar.getInstance(); cal.setTime(fromDate);
		 * cal.add(Calendar.DATE, noOfDays);
		 * 
		 *//** To-Date *//*
							 * return new Timestamp(cal.getTime().getTime());
							 */

	}

	private PreRegistrationList preparePreRegistration(SyncTransaction syncTransaction,
			PreRegistrationDTO preRegistrationDTO) {

		PreRegistrationList preRegistrationList = new PreRegistrationList();

		preRegistrationList.setId("Random Id");
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

		return null;

	}

}
