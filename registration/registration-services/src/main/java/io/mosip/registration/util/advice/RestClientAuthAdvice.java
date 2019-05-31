package io.mosip.registration.util.advice;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.LoginMode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.tpm.spi.TPMUtil;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * The Class RestClientAuthAdvice checks whether the invoking REST service
 * should required authentication. If required then the auth service is invoked
 * to get the token.
 * 
 * @author Balaji Sridharan
 * @author Mahesh Kumar
 */
@Aspect
@Component
public class RestClientAuthAdvice {

	private static final Logger LOGGER = AppConfig.getLogger(RestClientAuthAdvice.class);
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;
	@Autowired
	private MachineMappingDAO machineMappingDAO;

	/**
	 * The {@link Around} advice method which be invoked for all web services. This
	 * advice adds the Authorization Token to the Web-Service Request Header, if
	 * authorization is required. If Authorization Token had expired, a new token
	 * will be requested.
	 * 
	 * @param joinPoint the join point of the advice
	 * @return the response from the web-service
	 * @throws RegBaseCheckedException
	 * 			- generalized exception with errorCode and errorMessage
	 */
	@Around("execution(* io.mosip.registration.util.restclient.RestClientUtil.invoke(..))")
	public Object addAuthZToken(ProceedingJoinPoint joinPoint) throws RegBaseCheckedException {
		try {
			LOGGER.info(LoggerConstants.AUTHZ_ADVICE, APPLICATION_ID, APPLICATION_NAME,
					"Adding authZ token to web service request header if required");

			RequestHTTPDTO requestHTTPDTO = (RequestHTTPDTO) joinPoint.getArgs()[0];
			
			if (requestHTTPDTO.isRequestSignRequired() && RegistrationConstants.ENABLE
					.equals(String.valueOf(ApplicationContext.map().get(RegistrationConstants.TPM_AVAILABILITY)))) {
				addRequestSignature(requestHTTPDTO.getHttpHeaders(), requestHTTPDTO.getRequestBody());
			}
			
			if (requestHTTPDTO.isAuthRequired()) {
				boolean haveToAuthZByClientId = false;

				String authZToken = getAuthZToken(requestHTTPDTO, haveToAuthZByClientId);

				setAuthHeaders(requestHTTPDTO.getHttpHeaders(), requestHTTPDTO.getAuthZHeader(), authZToken);
			}

			requestHTTPDTO
					.setHttpEntity(new HttpEntity<>(requestHTTPDTO.getRequestBody(), requestHTTPDTO.getHttpHeaders()));
			Object response = joinPoint.proceed(joinPoint.getArgs());

			LOGGER.info(LoggerConstants.AUTHZ_ADVICE, APPLICATION_ID, APPLICATION_NAME,
					"Adding authZ token to web service request header if required completed");

			return response;
			
		} catch (HttpClientErrorException httpClientErrorException) {
			if (401 == httpClientErrorException.getRawStatusCode()) {
				try {
					RequestHTTPDTO requestHTTPDTO = (RequestHTTPDTO) joinPoint.getArgs()[0];
					getNewAuthZToken(requestHTTPDTO);
					return joinPoint.proceed(joinPoint.getArgs());
				} catch (Throwable throwableError) {
					throw new RegBaseCheckedException(
							RegistrationExceptionConstants.AUTHZ_ADDING_AUTHZ_HEADER.getErrorCode(),
							RegistrationExceptionConstants.AUTHZ_ADDING_AUTHZ_HEADER.getErrorMessage(), throwableError);
				}

			}
			throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTHZ_ADDING_AUTHZ_HEADER.getErrorCode(),
					RegistrationExceptionConstants.AUTHZ_ADDING_AUTHZ_HEADER.getErrorMessage(), httpClientErrorException);
		} catch (Throwable throwable) {
			
			throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTHZ_ADDING_AUTHZ_HEADER.getErrorCode(),
					RegistrationExceptionConstants.AUTHZ_ADDING_AUTHZ_HEADER.getErrorMessage(), throwable);
		}
	}

	/**
	 * Gets the new auth Z token.
	 *
	 * @return the new auth Z token
	 * @throws RegBaseCheckedException
	 */
	private void getNewAuthZToken(RequestHTTPDTO requestHTTPDTO) throws RegBaseCheckedException {
		String authZToken = RegistrationConstants.EMPTY;
		boolean haveToAuthZByClientId = false;
		if (RegistrationConstants.JOB_TRIGGER_POINT_USER.equals(requestHTTPDTO.getTriggerPoint())) {
			LoginUserDTO loginUserDTO = (LoginUserDTO) ApplicationContext.map().get(RegistrationConstants.USER_DTO);
			if (loginUserDTO == null || loginUserDTO.getPassword() == null
					|| SessionContext.isSessionContextAvailable()) {
				haveToAuthZByClientId = true;
			} else {
				serviceDelegateUtil.getAuthToken(LoginMode.PASSWORD);
				authZToken = SessionContext.authTokenDTO().getCookie();
			}
		}

		// Get the AuthZ Token By Client ID and Secret Key if
		if ((haveToAuthZByClientId
				|| RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM.equals(requestHTTPDTO.getTriggerPoint()))) {
			serviceDelegateUtil.getAuthToken(LoginMode.CLIENTID);
			authZToken = ApplicationContext.authTokenDTO().getCookie();
		}

		setAuthHeaders(requestHTTPDTO.getHttpHeaders(), requestHTTPDTO.getAuthZHeader(), authZToken);

	}

	private String getAuthZToken(RequestHTTPDTO requestHTTPDTO, boolean haveToAuthZByClientId)
			throws RegBaseCheckedException {
		LOGGER.info(LoggerConstants.AUTHZ_ADVICE, APPLICATION_ID, APPLICATION_NAME, "Getting authZ token");
		String authZToken = null;

		// Get the AuthZ Token from AuthZ Web-Service only if Job is triggered by User
		// and existing AuthZ Token had expired
		if (RegistrationConstants.JOB_TRIGGER_POINT_USER.equals(requestHTTPDTO.getTriggerPoint())) {
			if (SessionContext.isSessionContextAvailable() && null != SessionContext.authTokenDTO().getCookie()) {
				authZToken = SessionContext.authTokenDTO().getCookie();
			} else {
				LoginUserDTO loginUserDTO = (LoginUserDTO) ApplicationContext.map().get(RegistrationConstants.USER_DTO);
				if (loginUserDTO == null || loginUserDTO.getPassword() == null) {
					haveToAuthZByClientId = true;
				} else {
					serviceDelegateUtil.getAuthToken(LoginMode.PASSWORD);
					authZToken = SessionContext.authTokenDTO().getCookie();
				}
			}
		}

		// Get the AuthZ Token By Client ID and Secret Key if
		if ((haveToAuthZByClientId
				|| RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM.equals(requestHTTPDTO.getTriggerPoint()))) {
			if (null == ApplicationContext.authTokenDTO() || null == ApplicationContext.authTokenDTO().getCookie()) {
				serviceDelegateUtil.getAuthToken(LoginMode.CLIENTID);
			}
			authZToken = ApplicationContext.authTokenDTO().getCookie();
		}

		LOGGER.info(LoggerConstants.AUTHZ_ADVICE, APPLICATION_ID, APPLICATION_NAME, "Getting of authZ token completed");

		return authZToken;
	}

	/**
	 * Setup of Auth Headers.
	 *
	 * @param httpHeaders http headers
	 * @param authHeader  auth header
	 * @param authZCookie the Authorization Token or Cookie
	 */
	private void setAuthHeaders(HttpHeaders httpHeaders, String authHeader, String authZCookie) {
		LOGGER.info(LoggerConstants.AUTHZ_ADVICE, APPLICATION_ID, APPLICATION_NAME,
				"Adding authZ token to request header");

		String[] arrayAuthHeaders = null;

		if (authHeader != null) {
			arrayAuthHeaders = authHeader.split(":");
			if (arrayAuthHeaders[1].equalsIgnoreCase(RegistrationConstants.REST_OAUTH)) {
				httpHeaders.add(RegistrationConstants.COOKIE, authZCookie);
			} else if (arrayAuthHeaders[1].equalsIgnoreCase(RegistrationConstants.AUTH_TYPE)) {
				httpHeaders.add(arrayAuthHeaders[0], arrayAuthHeaders[1]);
			}
		}

		LOGGER.info(LoggerConstants.AUTHZ_ADVICE, APPLICATION_ID, APPLICATION_NAME,
				"Adding of authZ token to request header completed");
	}

	/**
	 * Add request signature to the request header
	 * 
	 * @param httpHeaders
	 *            the HTTP headers for the web-service request
	 * @param requestBody
	 *            the request body
	 * @throws RegBaseCheckedException
	 *             exception while generating request signature
	 */
	private void addRequestSignature(HttpHeaders httpHeaders, Object requestBody) throws RegBaseCheckedException {
		LOGGER.info(LoggerConstants.AUTHZ_ADVICE, APPLICATION_ID, APPLICATION_NAME,
				"Adding request signature to request header");

		try {
			httpHeaders.add("request-signature", String.format("Authorization:%s",
					CryptoUtil.encodeBase64(TPMUtil.signData(JsonUtils.javaObjectToJsonString(requestBody).getBytes()))));
			httpHeaders.add(RegistrationConstants.KEY_INDEX, CryptoUtil.encodeBase64String(machineMappingDAO
					.getMachineByName(InetAddress.getLocalHost().getHostName()).getKeyIndex().getBytes()));
		} catch (UnknownHostException | JsonProcessingException jsonProcessingException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTHZ_ADDING_REQUEST_SIGN.getErrorCode(),
					RegistrationExceptionConstants.AUTHZ_ADDING_REQUEST_SIGN.getErrorMessage(),
					jsonProcessingException);
		}

		LOGGER.info(LoggerConstants.AUTHZ_ADVICE, APPLICATION_ID, APPLICATION_NAME,
				"Completed adding request signature to request header completed");
	}

}
