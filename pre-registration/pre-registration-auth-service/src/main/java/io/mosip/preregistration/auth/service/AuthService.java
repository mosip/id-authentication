package io.mosip.preregistration.auth.service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides different methods for login called by the controller 
 * 
 * @author M1050360
 * @since 1.0.0
 */

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.Otp;
import io.mosip.preregistration.auth.dto.OtpUser;
import io.mosip.preregistration.auth.dto.OtpUserDTO;
import io.mosip.preregistration.auth.dto.User;
import io.mosip.preregistration.auth.dto.UserOtp;
import io.mosip.preregistration.auth.dto.UserOtpDTO;
import io.mosip.preregistration.auth.errorcodes.ErrorCodes;
import io.mosip.preregistration.auth.errorcodes.ErrorMessages;
import io.mosip.preregistration.auth.exceptions.AuthServiceException;
import io.mosip.preregistration.auth.exceptions.ConfigFileNotFoundException;
import io.mosip.preregistration.auth.exceptions.util.AuthExceptionCatcher;
import io.mosip.preregistration.auth.util.AuthCommonUtil;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;

@Service
public class AuthService {

	private Logger log = LoggerConfiguration.logConfig(AuthService.class);
	
	/**
	 * Autowired reference for {@link #authCommonUtil}
	 */
	@Autowired
	private AuthCommonUtil authCommonUtil;
	
	@Value("${global.config.file}")
	private String globalFileName;

	@Value("${pre.reg.config.file}")
	private String preRegFileName;

	@Value("${ui.config.params}")
	private String uiConfigParams;
	
	/**
	 * Reference for ${sendOtp.resource.url} from property file
	 */
	@Value("${sendOtp.resource.url}")
	private String sendOtpResourceUrl;
	
	private List<String> otpChannel;
	
	@Value("${userIdType}")
	private String useridtype;
	
	@Value("${appId}")
	private String appId;
	
	@Autowired
	AuditLogUtil auditLogUtil;
	
	Map<String, String> requiredRequestMap = new HashMap<>();
	
	/**
	 * Reference for ${mosip.prereg.app-id} from property file
	 */
//	@Value("${mosip.prereg.app-id}")
//	private String appId;
	
	/**
	 * UserId for auditing
	 */
	private String auditUserId;
	/**
	 * It will fetch otp from Kernel auth service  and send to the userId provided
	 * 
	 * @param userOtpRequest
	 * @return MainResponseDTO<AuthNResponse>
	 */
	public MainResponseDTO<AuthNResponse> sendOTP(MainRequestDTO<Otp> userOtpRequest) {
		log.info("sessionId", "idType", "id",
				"In callsendOtp method of kernel service ");
		MainResponseDTO<AuthNResponse> response  = null;
		Otp otp=userOtpRequest.getRequest();
		
		boolean isRetrieveSuccess = false;
		try {
			if(authCommonUtil.validateRequest(userOtpRequest)) {
				
				
				otpChannel=authCommonUtil.validateUserIdAndLangCode(otp.getUserId(),otp.getLangCode());
				OtpUser user=new OtpUser(otp.getUserId(), otp.getLangCode(), otpChannel, appId, useridtype);
				OtpUserDTO otpUserDTO=new OtpUserDTO();
				auditUserId=otp.getUserId();
				otpUserDTO.setRequest(user);
				response  =	(MainResponseDTO<AuthNResponse>) authCommonUtil.getMainResponseDto(userOtpRequest);
				String url=sendOtpResourceUrl+"/v1.0/authenticate/sendotp";
				ResponseEntity<String> responseEntity=(ResponseEntity<String>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON,otpUserDTO,null,String.class);
				List<ServiceError> validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
				if(!validationErrorList.isEmpty()) {
					throw new AuthServiceException(validationErrorList,response);
				}
				response.setResponsetime(authCommonUtil.getCurrentResponseTime());
				response.setResponse(authCommonUtil.requestBodyExchange(responseEntity.getBody()));
			}
			isRetrieveSuccess = true;
		}
		catch(Exception ex) {
			log.error("sessionId", "idType", "id",
					"In callsendOtp method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"sendOtp");	
		}
		finally {
			if (isRetrieveSuccess) {
				setAuditValues(EventId.PRE_410.toString(), EventName.AUTHENTICATION.toString(), EventType.BUSINESS.toString(),
						"Send otp to user successfully",
						AuditLogVariables.MULTIPLE_ID.toString(),otp.getUserId(),otp.getUserId());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Failed to send otp to user ", AuditLogVariables.NO_ID.toString(),otp.getUserId(),otp.getUserId());
			}
		}
		return response;
	}
	
	
	/**
	 * It will validate userId & otp and provide with a access token 
	 * 
	 * @param userIdOtpRequest
	 * @return MainResponseDTO<AuthNResponse>
	 */
	public MainResponseDTO<ResponseEntity<String>> validateWithUserIdOtp(MainRequestDTO<User> userIdOtpRequest){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of kernel service ");
		MainResponseDTO<ResponseEntity<String>> response  = null;
		try {
			if(authCommonUtil.validateRequest(userIdOtpRequest)) {
				User user=userIdOtpRequest.getRequest();
				authCommonUtil.validateOtpAndUserid(user);
				UserOtp userOtp=new UserOtp(user.getUserId(), user.getOtp(), appId);
				UserOtpDTO userOtpDTO=new UserOtpDTO();
				userOtpDTO.setRequest(userOtp);
				response  =	(MainResponseDTO<ResponseEntity<String>>) authCommonUtil.getMainResponseDto(userIdOtpRequest);
				ResponseEntity<String> responseEntity = null;
				String url=sendOtpResourceUrl+"/v1.0/authenticate/useridOTP";
				responseEntity=(ResponseEntity<String>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON_UTF8,userOtpDTO,null,String.class);
				List<ServiceError> validationErrorList=null;
				validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
				if(!validationErrorList.isEmpty()) {
					throw new AuthServiceException(validationErrorList,response);
				}
				response.setResponsetime(authCommonUtil.getCurrentResponseTime());
				response.setResponse(responseEntity);
			}
		}
		catch(Exception ex) {
			log.error("sessionId", "idType", "id",
					"In calluserIdOtp method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"userIdOtp");	
		}
		
		return response;
	}
	
	/**
	 * This method will invalidate the access token
	 * 
	 * @param authHeader
	 * @return AuthNResponse
	 */

	public AuthNResponse invalidateToken(String authHeader){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of kernel service ");
		ResponseEntity<String> responseEntity = null;
		AuthNResponse authNResponse = null;
		boolean isRetrieveSuccess = false;
		try {
			Map<String,String> headersMap=new HashMap<>();
			headersMap.put("Cookie",authHeader);
			String url=sendOtpResourceUrl+"/v1.0/authorize/invalidateToken";
			responseEntity=(ResponseEntity<String>) authCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON,null,headersMap,String.class);
			List<ServiceError> validationErrorList=null;
			validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
			if(!validationErrorList.isEmpty()) {
				throw new AuthServiceException(validationErrorList,null);
			}
			authNResponse = authCommonUtil.requestBodyExchange(responseEntity.getBody());
			isRetrieveSuccess = true;
		}
		catch(Exception ex) {	
			log.error("sessionId", "idType", "id",
					"In call invalidateToken method of kernel service- " + ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"invalidateToken");	
		}
		finally {
			if (isRetrieveSuccess) {
				setAuditValues(EventId.PRE_410.toString(), EventName.AUTHENTICATION.toString(), EventType.BUSINESS.toString(),
						"Logout successfully",
						AuditLogVariables.MULTIPLE_ID.toString(),auditUserId,auditUserId);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Failed to logout    ", AuditLogVariables.NO_ID.toString(),auditUserId,auditUserId);
			}
		}
		return authNResponse;
	}
	
	/**
	 * This method is used to audit all the Authentication events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,String userId,String userName) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setDescription(description);
		auditRequestDto.setId(idType);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setModuleId(AuditLogVariables.DEM.toString());
		auditRequestDto.setModuleName(AuditLogVariables.DEMOGRAPHY_SERVICE.toString());
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}
	
	/**
	 * This will return UI related configurations return
	 */
	public MainResponseDTO<Map<String, String>> getConfig() {
		log.info("sessionId", "idType", "id",
				"In notification service of getConfig ");
		MainResponseDTO<Map<String, String>> res = new MainResponseDTO<>();
		List<String> reqParams = new ArrayList<>();
		Map<String, String> configParams = new HashMap<>();
		try {
			String[] uiParams = uiConfigParams.split(",");
			for (int i = 0; i < uiParams.length; i++) {
				reqParams.add(uiParams[i]);
			}
			if (globalFileName != null && preRegFileName != null) {
				String globalParam = authCommonUtil.configRestCall(globalFileName);
				String preregParam = authCommonUtil.configRestCall(preRegFileName);
				Properties prop1 = authCommonUtil.parsePropertiesString(globalParam);
				Properties prop2 = authCommonUtil.parsePropertiesString(preregParam);
				authCommonUtil.getConfigParams(prop1,configParams,reqParams);
				authCommonUtil.getConfigParams(prop2,configParams,reqParams);
		
			} else {
				throw new ConfigFileNotFoundException(ErrorCodes.PRG_AUTH_012.name(),
						ErrorMessages.CONFIG_FILE_NOT_FOUND_EXCEPTION.name());
			}
			
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In notification service of getConfig "+ex.getMessage());
			new AuthExceptionCatcher().handle(ex,"config");
		}
		res.setResponse(configParams);
		res.setResponsetime(authCommonUtil.getCurrentResponseTime());
		return res;
	}
}
