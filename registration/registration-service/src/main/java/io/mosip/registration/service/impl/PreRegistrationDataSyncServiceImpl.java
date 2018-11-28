package io.mosip.registration.service.impl;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.PreRegistrationDataSyncDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncRequestDTO;
import io.mosip.registration.dto.PreRegistrationResponseDTO;
import io.mosip.registration.dto.PreRegistrationResponseDataSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.PreRegistrationDataSyncService;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class PreRegistrationDataSyncServiceImpl implements PreRegistrationDataSyncService {

	/**
	 * To perform api calls
	 */
	@Autowired
	ServiceDelegateUtil delegateUtil;

	@SuppressWarnings("unchecked")
	@Override
	public ResponseDTO getPreRegistrationIds(String id, Timestamp reqTime, Timestamp fromDate, Timestamp toDate,
			String regClientId, String userId, String ver) {
		
		// TODO prepare required DTO to send through API
		PreRegistrationDataSyncDTO requestDTO = new PreRegistrationDataSyncDTO();
		PreRegistrationDataSyncRequestDTO requestDetailedDTO = new PreRegistrationDataSyncRequestDTO();
		requestDTO.setDataSyncRequestDto(requestDetailedDTO);

		try {
			// API call
			PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO> preRegistrationResponseDTO = (PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO>) delegateUtil
					.post(RegistrationConstants.GET_PRE_REGISTRATION_IDS, requestDTO);

			// Get List of responses (Pre-Reg Response)
			List<PreRegistrationResponseDataSyncDTO> preRegistrationResponseList = preRegistrationResponseDTO
					.getResponse();

			preRegistrationResponseList.forEach(response -> {
				List<String> preRegIds = response.getPreRegistrationIds();

				// @see
				preRegIds.forEach((PreRegid) -> getPreRegistration(PreRegid));

				// TODO DAO call to save all the preRegIds into local database
			});

		} catch (HttpClientErrorException | ResourceAccessException | SocketTimeoutException
				| RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public ResponseDTO getPreRegistration(String preRegistrationId) {
		// prepare request params to pass through URI
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.PRE_REGISTRATION_ID, preRegistrationId);
		try {
			byte[] packet = (byte[]) delegateUtil.get("ServiceName ??", requestParamMap);
		
		} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException e) {
			
		}

		return null;
	}
	
	
	private void setSuccessMessage(ResponseDTO responseDTO,String message, Object attribute, String attributeName) {
		
	}
	
	private void setErrorMessage(ResponseDTO responseDTO,String message) {
		
	}

}
