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

import org.springframework.beans.factory.annotation.Autowired;
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
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@Service
public class PreRegistrationDataSyncServiceImpl implements PreRegistrationDataSyncService {
	/**
	 * To perform api calls
	 */
	@Autowired
	ServiceDelegateUtil delegateUtil;

	@Autowired
	PreRegistrationDAO preRegistrationDAO;

	@Autowired
	SyncManager syncManager;

	private String noOfDays = "5";

	private ResponseDTO responseDTO;

	@SuppressWarnings("unchecked")
	@Override
	public ResponseDTO getPreRegistrationIds(String jobId) {

		responseDTO = new ResponseDTO();

		// prepare request DTO to pass on through REST call
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = prepareRequestDTO();

		try {
			// REST call
			PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO> preRegistrationResponseDTO = (PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO>) delegateUtil
					.post(RegistrationConstants.GET_PRE_REGISTRATION_IDS, preRegistrationDataSyncDTO);

			// Get List of responses (Pre-Reg Response)
			ArrayList<?> preRegistrationResponseList = (ArrayList<?>) preRegistrationResponseDTO.getResponse();

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

	@Override
	public ResponseDTO getPreRegistration(String preRegistrationId) {
		
		PreRegistrationList preRegistration = preRegistrationDAO.getPreRegistration(preRegistrationId);
		PreRegistrationDTO preRegistrationDTO=null;
		
		if(preRegistration==null) {
			
			// prepare request params to pass through URI
			Map<String, String> requestParamMap = new HashMap<>();
			requestParamMap.put(RegistrationConstants.PRE_REGISTRATION_ID, preRegistrationId);

			byte[] packet = null;
			
			try {
				packet = (byte[]) delegateUtil.get(RegistrationConstants.GET_PRE_REGISTRATION, requestParamMap);
				
				if(packet!=null) {
					// see Bala : Get PreRegistrationDTO by taking packet Information
					
				}
				
				
				
			} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException e) {

				setErrorMessage(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR);
			} 
		} else {
			preRegistrationDTO =new PreRegistrationDTO();
			preRegistrationDTO.setAppointmentDate(preRegistration.getAppointmentDate());
			preRegistrationDTO.setPacketPath(preRegistration.getPacketPath());
			preRegistrationDTO.setPreRegId(preRegistrationDTO.getPreRegId());
			preRegistrationDTO.setSymmetricKey(preRegistration.getPacketSymmetricKey());
			
		}
		if(preRegistrationDTO!=null) {
			setSuccessMessage(responseDTO, null);
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();

			Map<String, Object> attributes = new HashMap<>();
			attributes.put("pre_reg_dto", preRegistrationDTO);
			successResponseDTO.setOtherAttributes(attributes);
		}

		return responseDTO;
	}

	private void getPreRegistrations(List<String> preRegIds) {

		// List of pre registartions
		LinkedList<PreRegistrationList> preRegistrations = new LinkedList<>();

		preRegIds.forEach(preRegId -> {

			PreRegistrationList preRegistration = preRegistrationDAO.getPreRegistration(preRegId);
			if (preRegistration == null) {
				ResponseDTO responseDTO = getPreRegistration(preRegId);
				
				
				// String statusComment = (responseDTO.getSuccessResponseDTO()!=null) ?

				// TODO save packet

				if (responseDTO != null && responseDTO.getSuccessResponseDTO() != null) {
					// TODO save pre-reg transaction
					// TODO save pre-reg
					// TODO add preReg to preRegistrations list
				}
			}

		});

	}

	private PreRegistrationDataSyncDTO prepareRequestDTO() {

		// TODO prepare required DTO to send through API
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = new PreRegistrationDataSyncDTO();

		Timestamp reqTime = new Timestamp(System.currentTimeMillis());
		preRegistrationDataSyncDTO.setId(RegistrationConstants.PRE_REGISTRATION_DUMMY_ID);
		preRegistrationDataSyncDTO.setReqTime(reqTime);
		preRegistrationDataSyncDTO.setVer(RegistrationConstants.VER);

		PreRegistrationDataSyncRequestDTO preRegistrationDataSyncRequestDTO = new PreRegistrationDataSyncRequestDTO();
		preRegistrationDataSyncRequestDTO.setFromDate(reqTime);
		preRegistrationDataSyncRequestDTO.setRegClientId(RegistrationConstants.REGISTRATION_CLIENT_ID);
		preRegistrationDataSyncRequestDTO.setToDate(getToDate(preRegistrationDataSyncRequestDTO.getFromDate()));
		preRegistrationDataSyncRequestDTO.setUserId(getUserId());

		preRegistrationDataSyncDTO.setDataSyncRequestDto(preRegistrationDataSyncRequestDTO);

		return preRegistrationDataSyncDTO;

	}

	private void setSuccessMessage(ResponseDTO responseDTO, String message) {

		SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
		successResponseDTO.setMessage(message);
		successResponseDTO.setCode(RegistrationConstants.ALERT_INFORMATION);
	}

	private void setErrorMessage(ResponseDTO responseDTO, String message) {

		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setMessage(message);
		errorResponseDTO.setCode(RegistrationConstants.ERROR);
	}

	private String getUserId() {

		String userId = null;
		UserContext userContext = SessionContext.getInstance().getUserContext();
		if (userContext != null) {
			userId = userContext.getUserId();
		}
		return userId;
	}

	private Timestamp getToDate(Timestamp fromDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		cal.add(Calendar.DATE, Integer.valueOf(noOfDays));

		// To-Date
		return new Timestamp(cal.getTime().getTime());

	}
	
	private PreRegistrationList preparePreRegistration(SyncTransaction syncTransaction, PreRegistrationDTO preRegistrationDTO,String parentPreRegId,String status) {
		
		PreRegistrationList preRegistrationList=new PreRegistrationList();
		
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
