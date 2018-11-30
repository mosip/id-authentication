package io.mosip.registration.service.sync.impl;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import io.mosip.registration.dto.PreRegistrationDataSyncDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncRequestDTO;
import io.mosip.registration.dto.PreRegistrationResponseDTO;
import io.mosip.registration.dto.PreRegistrationResponseDataSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.PreRegistration;
import io.mosip.registration.exception.RegBaseCheckedException;
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

	@SuppressWarnings("unchecked")
	@Override
	public ResponseDTO getPreRegistrationIds() {

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

		} catch (HttpClientErrorException | ResourceAccessException | SocketTimeoutException
				| RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public byte[] getPreRegistration(String preRegistrationId) {

		// prepare request params to pass through URI
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.PRE_REGISTRATION_ID, preRegistrationId);

		byte[] packet = null;
		try {
			packet = (byte[]) delegateUtil.get(RegistrationConstants.GET_PRE_REGISTRATION, requestParamMap);

		} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException e) {
			e.printStackTrace();
		}

		return packet;
	}

	private void setSuccessMessage(ResponseDTO responseDTO, String message) {

		SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
		successResponseDTO.setMessage(message);
		successResponseDTO.setCode(RegistrationConstants.ALERT_INFORMATION);
	}

	private void setErrorMessage(ResponseDTO responseDTO, String message) {

	}

	private String getId() {
		return RegistrationConstants.PRE_REGISTRATION_DUMMY_ID;
	}

	private String getUserId() {

		String userId = null;
		UserContext userContext = SessionContext.getInstance().getUserContext();
		if (userContext != null) {
			userId = userContext.getUserId();
		}
		return userId;
	}

	private String getRegistrationClientId() {

		return RegistrationConstants.REGISTRATION_CLIENT_ID;
	}

	private Timestamp getToDate(Timestamp fromDate) {

		Timestamp toDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			Date parsedDate = dateFormat.parse("2018-11-17T07:22:57.086+0000");
			toDate = new Timestamp(parsedDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toDate;

		// Calendar cal = Calendar.getInstance();
		// cal.setTime(fromDate);
		// cal.add(Calendar.DATE, noOfDays);
		//
		// //To-Date
		// return new Timestamp(cal.getTime().getTime());

	}

	private String getVer() {
		return RegistrationConstants.VER;
	}

	private PreRegistrationDataSyncDTO prepareRequestDTO() {

		// TODO prepare required DTO to send through API
		PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = new PreRegistrationDataSyncDTO();
		Timestamp reqTime = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			Date parsedDate = dateFormat.parse("2018-01-17T07:22:57.086+0000");
			reqTime = new Timestamp(parsedDate.getTime());
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Timestamp reqTime = new Timestamp(System.currentTimeMillis());
		preRegistrationDataSyncDTO.setId(getId());
		preRegistrationDataSyncDTO.setReqTime(reqTime);
		preRegistrationDataSyncDTO.setVer(getVer());

		PreRegistrationDataSyncRequestDTO preRegistrationDataSyncRequestDTO = new PreRegistrationDataSyncRequestDTO();
		preRegistrationDataSyncRequestDTO.setFromDate(reqTime);
		preRegistrationDataSyncRequestDTO.setRegClientId(getRegistrationClientId());
		preRegistrationDataSyncRequestDTO.setToDate(getToDate(preRegistrationDataSyncRequestDTO.getFromDate()));
		preRegistrationDataSyncRequestDTO.setUserId(getUserId());

		preRegistrationDataSyncDTO.setDataSyncRequestDto(preRegistrationDataSyncRequestDTO);

		return preRegistrationDataSyncDTO;

	}

	private List<PreRegistration> getPreRegistrations(List<String> preRegIds) {

		// List of pre registartions
		LinkedList<PreRegistration> preRegistrations = new LinkedList<>();

		preRegIds.forEach(preRegId -> {

			try {
				PreRegistration preRegistration = preRegistrationDAO.getPreRegistration(preRegId);
				if (preRegistration == null) {
					byte[] packet = getPreRegistration(preRegId);

					// TODO save packet

					if (packet != null) {
						// TODO save pre-reg transaction
						// TODO save pre-reg
						// TODO add preReg to preRegistrations list
					}
				}

			} catch(Exception e) {
				e.printStackTrace();
			}
			
		});

		return preRegistrations;
	}

}
