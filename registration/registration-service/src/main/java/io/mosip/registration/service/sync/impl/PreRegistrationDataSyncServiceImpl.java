package io.mosip.registration.service.sync.impl;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.PreRegistrationDAO;
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
	/**
	 * To perform api calls
	 */
	@Autowired
	ServiceDelegateUtil delegateUtil;

	@Autowired
	PreRegistrationDAO preRegistrationDAO;

	@Autowired
	SyncManager syncManager;

	@Value("${PRE_REG_NO_OF_DAYS_LIMIT}")
	private int noOfDays ;

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#getPreRegistrationIds()
	 */
	@Override
	public ResponseDTO getPreRegistrationIds() {
		return getPreRegistrationIds(null);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#getPreRegistrationIds(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResponseDTO getPreRegistrationIds(String jobId) {

		ResponseDTO responseDTO = new ResponseDTO();

		// prepare request DTO to pass on through REST call
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = prepareDataSyncRequestDTO();

		try {
			// REST call
			PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO> preRegistrationResponseDTO = (PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO>) delegateUtil
					.post(RegistrationConstants.GET_PRE_REGISTRATION_IDS, preRegistrationDataSyncDTO);

			// Get List of responses (Pre-Reg Response)
			ArrayList<?> preRegistrationResponseList = (ArrayList<?>) preRegistrationResponseDTO.getResponse();

			//TODO dependency kernel
			HashMap<String, Object> map = (HashMap<String, Object>) preRegistrationResponseList.get(0);
			ArrayList<String> preregIds = (ArrayList<String>) map.get("preRegistrationIds");

			getPreRegistrations(preregIds);

			setSuccessMessage(responseDTO, RegistrationConstants.PRE_REG_GET_ID_SUCCESS_MESSAGE);

		} catch (HttpClientErrorException | ResourceAccessException | SocketTimeoutException
				| RegBaseCheckedException e) {
			setErrorMessage(responseDTO, RegistrationConstants.PRE_REG_TO_GET_ID_ERROR);
		}

		return responseDTO;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#getPreRegistration(java.lang.String)
	 */
	@Override
	public ResponseDTO getPreRegistration(String preRegistrationId) {

		return getPreRegistration(preRegistrationId, null);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#getPreRegistration(java.lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO getPreRegistration(String preRegistrationId, String syncJobId) {

		PreRegistrationList preRegistration = preRegistrationDAO.getPreRegistration(preRegistrationId);
		PreRegistrationDTO preRegistrationDTO = null;

		ResponseDTO responseDTO = new ResponseDTO();

		if (preRegistration == null) {

			// prepare request params to pass through URI
			Map<String, String> requestParamMap = new HashMap<>();
			requestParamMap.put(RegistrationConstants.PRE_REGISTRATION_ID, preRegistrationId);

			byte[] packet = null;

			try {
				packet = (byte[]) delegateUtil.get(RegistrationConstants.GET_PRE_REGISTRATION, requestParamMap);

				String triggerPoint = (syncJobId != null) ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM
						: getUserIdFromSession();

				if (packet != null) {
					// see Bala : Get PreRegistrationDTO by taking packet Information

					// Transaction
					SyncTransaction syncTransaction = syncManager.createSyncTransaction(
							RegistrationConstants.RETRIEVED_PRE_REG_ID, RegistrationConstants.RETRIEVED_PRE_REG_ID,
							triggerPoint, syncJobId);

					// save in Pre-Reg List
					PreRegistrationList preRegistrationList = preparePreRegistration(syncTransaction,
							preRegistrationDTO);

					preRegistrationDAO.savePreRegistration(preRegistrationList);

				} else {
					// Transaction
					syncManager.createSyncTransaction(RegistrationConstants.UNABLE_TO_RETRIEVE_PRE_REG_ID,
							RegistrationConstants.UNABLE_TO_RETRIEVE_PRE_REG_ID, triggerPoint, syncJobId);
				}

			} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException e) {

				setErrorMessage(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR);
			}
		} else {
			//TODO 
			preRegistrationDTO = new PreRegistrationDTO();
			preRegistrationDTO.setAppointmentDate(preRegistration.getAppointmentDate());
			preRegistrationDTO.setPacketPath(preRegistration.getPacketPath());
			preRegistrationDTO.setPreRegId(preRegistrationDTO.getPreRegId());
			preRegistrationDTO.setSymmetricKey(preRegistration.getPacketSymmetricKey());

		}
		if (preRegistrationDTO != null) {
			setSuccessMessage(responseDTO, null);
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();

			Map<String, Object> attributes = new HashMap<>();
			attributes.put(preRegistrationId, preRegistrationDTO);
			successResponseDTO.setOtherAttributes(attributes);
		}

		return responseDTO;
	}
	
	private void savePreRegistration(String preRegId) {
		
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.sync.PreRegistrationDataSyncService#getPreRegistrations(java.util.List)
	 */
	private ResponseDTO getPreRegistrations(List<String> preRegIds) {

		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		setSuccessMessage(responseDTO, "Retrieved Pre Registartions");
		Map<String, Object> attributes = new HashMap<>();

		// Get Pre Registrations
		preRegIds.forEach(preRegId -> {

			ResponseDTO currentResponseDTO = getPreRegistration(preRegId);
			attributes.put(preRegId, currentResponseDTO);

		});
		successResponseDTO.setOtherAttributes(attributes);

		return responseDTO;

	}

	private PreRegistrationDataSyncDTO prepareDataSyncRequestDTO() {

		// prepare required DTO to send through API
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = new PreRegistrationDataSyncDTO();

		Timestamp reqTime = new Timestamp(System.currentTimeMillis());
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

	private void setSuccessMessage(ResponseDTO responseDTO, String message) {

		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		successResponseDTO.setMessage(message);
		successResponseDTO.setCode(RegistrationConstants.ALERT_INFORMATION);

		responseDTO.setSuccessResponseDTO(successResponseDTO);
	}

	private void setErrorMessage(ResponseDTO responseDTO, String message) {

		List<ErrorResponseDTO> errorResponseDTOs = new LinkedList<>();

		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		if (responseDTO.getErrorResponseDTOs() != null) {
			// Get if already existing error responses
			errorResponseDTOs = responseDTO.getErrorResponseDTOs();
		}
		errorResponseDTO.setMessage(message);
		errorResponseDTO.setCode(RegistrationConstants.ERROR);

		errorResponseDTOs.add(errorResponseDTO);

		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
	}

	

	private Timestamp getToDate(Timestamp fromDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		cal.add(Calendar.DATE, noOfDays);

		// To-Date
		return new Timestamp(cal.getTime().getTime());

	}

	private PreRegistrationList preparePreRegistration(SyncTransaction syncTransaction,
			PreRegistrationDTO preRegistrationDTO) {

		PreRegistrationList preRegistrationList = new PreRegistrationList();

		preRegistrationList.setId("Random Id");
		preRegistrationList.setPreRegId(preRegistrationDTO.getPreRegId());
		preRegistrationList.setAppointmentDate(preRegistrationDTO.getAppointmentDate());
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
