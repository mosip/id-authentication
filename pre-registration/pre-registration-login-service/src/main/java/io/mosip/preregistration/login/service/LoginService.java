package io.mosip.preregistration.login.service;

import java.time.LocalDateTime;
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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.login.dto.MainRequestDTO;
import io.mosip.preregistration.login.dto.MainResponseDTO;
import io.mosip.preregistration.login.dto.OtpRequestDTO;
import io.mosip.preregistration.login.dto.OtpUser;
import io.mosip.preregistration.login.dto.OtpUserDTO;
import io.mosip.preregistration.login.dto.User;
import io.mosip.preregistration.login.dto.UserOtp;
import io.mosip.preregistration.login.dto.UserOtpDTO;
import io.mosip.preregistration.login.errorcodes.ErrorCodes;
import io.mosip.preregistration.login.errorcodes.ErrorMessages;
import io.mosip.preregistration.login.exception.ConfigFileNotFoundException;
import io.mosip.preregistration.login.exception.LoginServiceException;
import io.mosip.preregistration.login.exception.util.LoginExceptionCatcher;
import io.mosip.preregistration.login.util.LoginCommonUtil;

@Service
public class LoginService {

	private Logger log = LoggerConfiguration.logConfig(LoginService.class);
	
	/**
	 * Autowired reference for {@link #authCommonUtil}
	 */
	@Autowired
	private LoginCommonUtil loginCommonUtil;
	
	@Value("${global.config.file}")
	private String globalFileName;

	@Value("${pre.reg.config.file}")
	private String preRegFileName;

	@Value("${ui.config.params}")
	private String uiConfigParams;
	
	@Value("${mosip.preregistration.sendotp.id}")
	private String sendOtpId;
	
	@Value("${mosip.preregistration.validateotp.id}")
	private String userIdOtpId;
	
	@Value("${mosip.preregistration.invalidatetoken.id}")
	private String invalidateTokenId;
	
	@Value("${mosip.preregistration.config.id}")
	private String configId;
	
	@Value("${mosip.preregistration.login.service.version}")
	private String version;
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
	
	@PostConstruct
	public void setupBookingService() {
		requiredRequestMap.put("version", version);

	}
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
	public MainResponseDTO<AuthNResponse> sendOTP(MainRequestDTO<OtpRequestDTO> userOtpRequest) {
		log.info("sessionId", "idType", "id",
				"In callsendOtp method of login service ");
		MainResponseDTO<AuthNResponse> response  = null;
		OtpRequestDTO otp=userOtpRequest.getRequest();
		requiredRequestMap.put("id",sendOtpId);
		
		boolean isRetrieveSuccess = false;
		try {
			if(ValidationUtil.requestValidator(loginCommonUtil.prepareRequestMap(userOtpRequest),requiredRequestMap)/*authCommonUtil.validateRequest(userOtpRequest)*/) {
				
				auditUserId=otp.getUserId();
				otpChannel=loginCommonUtil.validateUserIdAndLangCode(otp.getUserId(),otp.getLangCode());
				OtpUser user=new OtpUser(otp.getUserId(), otp.getLangCode(), otpChannel, appId, useridtype);
				RequestWrapper<OtpUser> requestSendOtpKernel=new RequestWrapper<>();
				requestSendOtpKernel.setRequest(user);
				requestSendOtpKernel.setRequesttime(LocalDateTime.now());
				response  =	(MainResponseDTO<AuthNResponse>) loginCommonUtil.getMainResponseDto(userOtpRequest);
				String url=sendOtpResourceUrl+"/authenticate/sendotp";
				ResponseEntity<String> responseEntity=(ResponseEntity<String>) loginCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON,requestSendOtpKernel,null,String.class);
				List<ServiceError> validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
				if(!validationErrorList.isEmpty()) {
					throw new LoginServiceException(validationErrorList,response);
				}
				response.setResponsetime(loginCommonUtil.getCurrentResponseTime());
				ResponseWrapper<?> responseKernel=loginCommonUtil.requestBodyExchange(responseEntity.getBody());
				
				response.setResponse((AuthNResponse) loginCommonUtil.requestBodyExchangeObject(loginCommonUtil.responseToString(responseKernel.getResponse()),AuthNResponse.class));
			}
			isRetrieveSuccess = true;
		}
		catch(Exception ex) {
			log.error("sessionId", "idType", "id",
					"In callsendOtp method of kernel service- " + ex.getMessage());
			new LoginExceptionCatcher().handle(ex,"sendOtp");	
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
				"In calluserIdOtp method of login service ");
		MainResponseDTO<ResponseEntity<String>> response  = null;
		requiredRequestMap.put("id",userIdOtpId);
		try {
			if(ValidationUtil.requestValidator(loginCommonUtil.prepareRequestMap(userIdOtpRequest), requiredRequestMap)/*authCommonUtil.validateRequest(userIdOtpRequest)*/) {
				User user=userIdOtpRequest.getRequest();
				loginCommonUtil.validateOtpAndUserid(user);
				UserOtp userOtp=new UserOtp(user.getUserId(), user.getOtp(), appId);
				UserOtpDTO userOtpDTO=new UserOtpDTO();
				userOtpDTO.setRequest(userOtp);
				RequestWrapper<UserOtp> requestSendOtpKernel=new RequestWrapper<>();
				requestSendOtpKernel.setRequest(userOtp);
				requestSendOtpKernel.setRequesttime(LocalDateTime.now());
				response  =	(MainResponseDTO<ResponseEntity<String>>) loginCommonUtil.getMainResponseDto(userIdOtpRequest);
				ResponseEntity<String> responseEntity = null;
				String url=sendOtpResourceUrl+"/authenticate/useridOTP";
				responseEntity=(ResponseEntity<String>) loginCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON_UTF8,requestSendOtpKernel,null,String.class);
				List<ServiceError> validationErrorList=null;
				validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
				if(!validationErrorList.isEmpty()) {
					throw new LoginServiceException(validationErrorList,response);
				}
				response.setResponsetime(loginCommonUtil.getCurrentResponseTime());
				response.setResponse(responseEntity);
			}
		}
		catch(Exception ex) {
			log.error("sessionId", "idType", "id",
					"In calluserIdOtp method of kernel service- " + ex.getMessage());
			new LoginExceptionCatcher().handle(ex,"userIdOtp");	
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
				"In calluserIdOtp method of login service ");
		ResponseEntity<String> responseEntity = null;
		AuthNResponse authNResponse = null;
		boolean isRetrieveSuccess = false;
		try {
			Map<String,String> headersMap=new HashMap<>();
			headersMap.put("Cookie",authHeader);
			String url=sendOtpResourceUrl+"/authorize/invalidateToken";
			responseEntity=(ResponseEntity<String>) loginCommonUtil.getResponseEntity(url,HttpMethod.POST,MediaType.APPLICATION_JSON,null,headersMap,String.class);
			List<ServiceError> validationErrorList=null;
			validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
			if(!validationErrorList.isEmpty()) {
				throw new LoginServiceException(validationErrorList,null);
			}
			ResponseWrapper<?> responseKernel=loginCommonUtil.requestBodyExchange(responseEntity.getBody());
			authNResponse = (AuthNResponse) loginCommonUtil.requestBodyExchangeObject(loginCommonUtil.responseToString(responseKernel.getResponse()), AuthNResponse.class);
			isRetrieveSuccess = true;
		}
		catch(Exception ex) {	
			log.error("sessionId", "idType", "id",
					"In call invalidateToken method of kernel service- " + ex.getMessage());
			new LoginExceptionCatcher().handle(ex,"invalidateToken");	
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
				"In login service of getConfig ");
		MainResponseDTO<Map<String, String>> res = new MainResponseDTO<>();
		List<String> reqParams = new ArrayList<>();
		Map<String, String> configParams = new HashMap<>();
		try {
			String[] uiParams = uiConfigParams.split(",");
			for (int i = 0; i < uiParams.length; i++) {
				reqParams.add(uiParams[i]);
			}
			if (globalFileName != null && preRegFileName != null) {
				String globalParam = loginCommonUtil.configRestCall(globalFileName);
				String preregParam = loginCommonUtil.configRestCall(preRegFileName);
				Properties prop1 = loginCommonUtil.parsePropertiesString(globalParam);
				Properties prop2 = loginCommonUtil.parsePropertiesString(preregParam);
				loginCommonUtil.getConfigParams(prop1,configParams,reqParams);
				loginCommonUtil.getConfigParams(prop2,configParams,reqParams);
		
			} else {
				throw new ConfigFileNotFoundException(ErrorCodes.PRG_AUTH_012.name(),
						ErrorMessages.CONFIG_FILE_NOT_FOUND_EXCEPTION.name());
			}
			
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In login service of getConfig "+ex.getMessage());
			new LoginExceptionCatcher().handle(ex,"config");
		}
		res.setResponse(configParams);
		res.setResponsetime(loginCommonUtil.getCurrentResponseTime());
		return res;
	}
}
