package io.mosip.admin.packetstatusupdater.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.constant.AuditErrorCode;
import io.mosip.kernel.masterdata.dto.AuditResponseDto;
import io.mosip.kernel.masterdata.dto.request.AuditRequestDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.ValidationException;

/**
 * AuditUtil.
 */

@Component
public class AuditUtil {

	/** The Constant APPLICATION_ID. */
	private static final String APPLICATION_ID = "10009";

	/** The Constant APPLICATION_NAME. */
	private static final String APPLICATION_NAME = "Admin_Portal";

	/** The Constant UNKNOWN_HOST. */
	private static final String UNKNOWN_HOST = "Unknown Host";

	private String hostIpAddress = null;

	private String hostName = null;

	private volatile AtomicInteger eventCounter;

	@Value("${mosip.kernel.masterdata.audit-url}")
	private String auditUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Audit request.
	 *
	 * @param auditRequestDto
	 *            the audit request dto
	 */
	@PostConstruct
	private void init() {
		if(System.getProperty("seqGen")==null) {
		eventCounter = new AtomicInteger(500);
		}else {
			Integer eventCount=Integer.getInteger(System.getProperty("seqGen"));
			eventCounter=new AtomicInteger(eventCount);
		}
		
	}
	
	@PreDestroy
    public void shutdown() throws Exception {
		System.out.println("destroy");
		System.setProperty("seqGen", String.valueOf(eventCounter));
    }
	
		
	

	public void auditRequest(String eventName, String eventType, String description) {

		String eventId = "ADM-" + eventCounter.incrementAndGet();
		setAuditRequestDto(eventName, eventType, description, eventId);
	}

	public void auditRequest(String eventName, String eventType, String description, String eventId) {

		setAuditRequestDto(eventName, eventType, description, eventId);
	}

	/**
	 * Sets the audit request dto.
	 *
	 * @param auditRequestDto
	 *            the new audit request dto
	 */
	private void setAuditRequestDto(String eventName, String eventType, String description, String eventId) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		if (!validateSecurityContextHolder()) {

		}

		auditRequestDto.setEventId(eventId);
		auditRequestDto.setId("NO_ID");
		auditRequestDto.setIdType("NO_ID_TYPE");
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setModuleId("KER-MSD");
		auditRequestDto.setModuleName("Kernel masterdata");
		auditRequestDto.setDescription(description);
		auditRequestDto.setActionTimeStamp(DateUtils.getUTCCurrentDateTime());
		auditRequestDto.setHostIp(hostIpAddress);
		auditRequestDto.setHostName(hostName);
		auditRequestDto.setApplicationId(APPLICATION_ID);
		auditRequestDto.setApplicationName(APPLICATION_NAME);
		auditRequestDto.setSessionUserId(SecurityContextHolder.getContext().getAuthentication().getName());
		auditRequestDto.setSessionUserName(SecurityContextHolder.getContext().getAuthentication().getName());
		auditRequestDto.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
		callAuditManager(auditRequestDto);

	}

	/**
	 * Validate security context holder.
	 *
	 * @return true, if successful
	 */
	private boolean validateSecurityContextHolder() {
		Predicate<SecurityContextHolder> contextPredicate = i -> SecurityContextHolder.getContext() != null;
		Predicate<SecurityContextHolder> authPredicate = i -> SecurityContextHolder.getContext()
				.getAuthentication() != null;
		Predicate<SecurityContextHolder> principlePredicate = i -> SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal() != null;
		return contextPredicate.and(authPredicate).and(principlePredicate) != null;

	}

	/**
	 * Gets the server ip.
	 *
	 * @return the server ip
	 */
	public String getServerIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return UNKNOWN_HOST;
		}
	}

	/**
	 * Gets the server name.
	 *
	 * @return the server name
	 */
	public String getServerName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return UNKNOWN_HOST;
		}
	}

	/**
	 * To Set the Host Ip & Host Name
	 */
	@PostConstruct
	public void getHostDetails() {
		hostIpAddress = getServerIp();
		hostName = getServerName();
	}

	/**
	 * For Auditing Login Services
	 * 
	 * @param auditRequestDto
	 * @param token
	 * @return
	 */
	public void callAuditManager(AuditRequestDto auditRequestDto) {

		RequestWrapper<AuditRequestDto> auditReuestWrapper = new RequestWrapper<>();
		auditReuestWrapper.setRequest(auditRequestDto);
		HttpEntity<RequestWrapper<AuditRequestDto>> httpEntity = new HttpEntity<>(auditReuestWrapper);
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(auditUrl, HttpMethod.POST, httpEntity, String.class);

		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			handlException(ex);
		}
		String responseBody = response.getBody();
		getAuditDetailsFromResponse(responseBody);

	}

	private AuditResponseDto getAuditDetailsFromResponse(String responseBody) {

		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		AuditResponseDto auditResponseDto = null;
		if (!validationErrorsList.isEmpty()) {
			throw new ValidationException(validationErrorsList);
		}
		ResponseWrapper<AuditResponseDto> responseObject = null;
		try {

			responseObject = objectMapper.readValue(responseBody,
					new TypeReference<ResponseWrapper<AuditResponseDto>>() {
					});
			auditResponseDto = responseObject.getResponse();
		} catch (IOException | NullPointerException exception) {
			throw new MasterDataServiceException(AuditErrorCode.AUDIT_PARSE_EXCEPTION.getErrorCode(),
					AuditErrorCode.AUDIT_PARSE_EXCEPTION.getErrorMessage());
		}

		return auditResponseDto;
	}

	private void handlException(HttpStatusCodeException ex) {
		List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

		if (ex.getRawStatusCode() == 401) {
			if (!validationErrorsList.isEmpty()) {
				throw new AuthNException(validationErrorsList);
			} else {
				throw new BadCredentialsException("Authentication failed from AuthManager");
			}
		}
		if (ex.getRawStatusCode() == 403) {
			if (!validationErrorsList.isEmpty()) {
				throw new AuthZException(validationErrorsList);
			} else {
				throw new AccessDeniedException("Access denied from AuthManager");
			}
		}
		throw new MasterDataServiceException(AuditErrorCode.AUDIT_EXCEPTION.getErrorCode(),
				AuditErrorCode.AUDIT_EXCEPTION.getErrorMessage() + ex);

	}

}
