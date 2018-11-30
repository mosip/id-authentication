package io.mosip.registration.service.sync.impl;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dto.PreRegistrationDataSyncDTO;
import io.mosip.registration.dto.PreRegistrationDataSyncRequestDTO;
import io.mosip.registration.dto.PreRegistrationResponseDTO;
import io.mosip.registration.dto.PreRegistrationResponseDataSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
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
     
     /**
     * To calculate toDate to retrieve pre reg Ids
     */
     private int noOfDays=5;

     @SuppressWarnings("unchecked")
     @Override
     public ResponseDTO getPreRegistrationIds() {

           // TODO prepare required DTO to send through API
           PreRegistrationDataSyncDTO preRegistrationDataSyncDTO = new PreRegistrationDataSyncDTO();
          Timestamp reqTime=null;
           try {
                  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                  dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));           

                  Date parsedDate = dateFormat. parse("2018-01-17T07:22:57.086+0000");
                  reqTime = new Timestamp(parsedDate.getTime());
                  System.out.println();
                  } catch(Exception e) { e.printStackTrace();
                  }
           
           //Timestamp reqTime = new Timestamp(System.currentTimeMillis());
           preRegistrationDataSyncDTO.setId(getId());
           preRegistrationDataSyncDTO.setReqTime(reqTime);
           preRegistrationDataSyncDTO.setVer(getVer());
           
           PreRegistrationDataSyncRequestDTO preRegistrationDataSyncRequestDTO = new PreRegistrationDataSyncRequestDTO();
            preRegistrationDataSyncRequestDTO.setFromDate(reqTime);
     preRegistrationDataSyncRequestDTO.setRegClientId(getRegistrationClientId());
     preRegistrationDataSyncRequestDTO.setToDate(getToDate(preRegistrationDataSyncRequestDTO.getFromDate()));
            preRegistrationDataSyncRequestDTO.setUserId(getUserId());
           
     preRegistrationDataSyncDTO.setDataSyncRequestDto(preRegistrationDataSyncRequestDTO);

           try {
                  // API call
             PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO> preRegistrationResponseDTO = (PreRegistrationResponseDTO<PreRegistrationResponseDataSyncDTO>) delegateUtil
                               .post(RegistrationConstants.GET_PRE_REGISTRATION_IDS, preRegistrationDataSyncDTO);

                  // Get List of responses (Pre-Reg Response)
                  ArrayList preRegistrationResponseList = (ArrayList) preRegistrationResponseDTO
                               .getResponse();
                  
                  HashMap<String, Object> map=(HashMap<String, Object>) preRegistrationResponseList.get(0);
                  ArrayList<String> preregIds = (ArrayList<String>) map.get("preRegistrationIds");

                  

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

     private void setSuccessMessage(ResponseDTO responseDTO, String message, Object attribute, String attributeName) {

     }

     private void setErrorMessage(ResponseDTO responseDTO, String message) {

     }

     private String getId() {
           return RegistrationConstants.PRE_REGISTRATION_DUMMY_ID;
     }

     private String getUserId() {

           String userId=null;
           UserContext userContext = SessionContext.getInstance().getUserContext();
           if(userContext!=null) {
                  userId = userContext.getUserId();
           }
           return userId;
     }

     private String getRegistrationClientId() {
           
           return RegistrationConstants.REGISTRATION_CLIENT_ID;
     }

     private Timestamp getToDate(Timestamp fromDate) {
           
           Timestamp toDate=null;
           try {
                  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                  dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));           

                  Date parsedDate = dateFormat. parse("2018-11-17T07:22:57.086+0000");
                  toDate = new Timestamp(parsedDate.getTime());
                  } catch(Exception e) { e.printStackTrace();
                  }
           return toDate;
           
//           Calendar cal = Calendar.getInstance();
//           cal.setTime(fromDate);
//           cal.add(Calendar.DATE, noOfDays);
//           
//           //To-Date
//           return new Timestamp(cal.getTime().getTime());

     }
     
     private String getVer() {
           return RegistrationConstants.VER;
     }

}
