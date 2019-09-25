/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.util;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.cryptomanager.constant.CryptomanagerErrorCode;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyResponseDto;
import io.mosip.kernel.cryptomanager.exception.CryptoManagerSerivceException;
import io.mosip.kernel.cryptomanager.exception.KeymanagerServiceException;
import io.mosip.kernel.cryptomanager.exception.ParseResponseException;

/**
 * Util class for this project
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@RefreshScope
@Component
public class CryptomanagerUtils {

	
	private static final String RESPONSE = "response";

	private static final String ACCESS_DENIED = "Access denied for ";

	private static final String AUTHENTICATION_FAILED = "Authentication failed for ";

	private static final String REFERENCE_ID = "referenceId";

	private static final String TIMESTAMP = "timeStamp";

	private static final String APPLICATION_ID = "applicationId";

	private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Asymmetric Algorithm Name
	 */
	@Value("${mosip.kernel.keygenerator.asymmetric-algorithm-name}")
	private String asymmetricAlgorithmName;

	/**
	 * Symmetric Algorithm Name
	 */
	@Value("${mosip.kernel.keygenerator.symmetric-algorithm-name}")
	private String symmetricAlgorithmName;

	/**
	 * Keymanager URL to Get PublicKey
	 */
	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String getPublicKeyUrl;


	/**
	 * Keymanager URL to Decrypt Symmetric key
	 */
	@Value("${mosip.kernel.keymanager-service-decrypt-url}")
	private String decryptSymmetricKeyUrl;

	/**
	 * Keymanager URL to Decrypt Symmetric key
	 */
	@Value("${mosip.kernel.keymanager-service-auth-decrypt-url}")
	private String decryptAuthSymmetricKeyUrl;
	/**
	 * Key Splitter
	 */
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;

	@Value("${mosip.kernel.cryptomanager.request_id}")
	private String cryptomanagerRequestID;

	@Value("${mosip.kernel.cryptomanager.request_version}")
	private String cryptomanagerRequestVersion;
	
	
	
	/**
	 * {@link DataMapper} instance.
	 */
	@Autowired
	private DataMapper<CryptomanagerRequestDto, KeymanagerSymmetricKeyRequestDto> dataMapper;

	/**
	 * {@link RestTemplate} instance
	 */
	@Autowired
	private RestTemplate restTemplate;
	
	private static final String KEYMANAGER="Keymanager";
	
	private static final String PUBLIC_KEY="Public Key";

	/**
	 * Calls Key-Manager-Service to get public key of an application
	 * 
	 * @param cryptomanagerRequestDto {@link CryptomanagerRequestDto} instance
	 * @return {@link PublicKey} returned by Key Manager Service
	 */
	public PublicKey getPublicKey(CryptomanagerRequestDto cryptomanagerRequestDto) {
		PublicKey key = null;
		ResponseEntity<String> response = null;
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put(APPLICATION_ID, cryptomanagerRequestDto.getApplicationId());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getPublicKeyUrl)
				.queryParam(TIMESTAMP, DateUtils.formatToISOString(cryptomanagerRequestDto.getTimeStamp()))
				.queryParam(REFERENCE_ID, cryptomanagerRequestDto.getReferenceId());
		try {
			response = restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.GET, null,
					String.class);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {

			authExceptionHandler(ex, PUBLIC_KEY);

		}
		throwExceptionIfExist(response);
		KeymanagerPublicKeyResponseDto keyManagerResponseDto = getResponse(response, KeymanagerPublicKeyResponseDto.class);

		try {
			key = KeyFactory.getInstance(asymmetricAlgorithmName).generatePublic(
					new X509EncodedKeySpec(CryptoUtil.decodeBase64(keyManagerResponseDto.getPublicKey())));
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeyException(CryptomanagerErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorCode(),
					CryptomanagerErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new io.mosip.kernel.core.exception.NoSuchAlgorithmException(
					CryptomanagerErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					CryptomanagerErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
		return key;
	}

	/**
	 * Calls Key-Manager-Service to decrypt symmetric key
	 * 
	 * @param cryptomanagerRequestDto {@link CryptomanagerRequestDto} instance
	 * @return Decrypted {@link SecretKey} from Key Manager Service
	 */
	public SecretKey getDecryptedSymmetricKey(CryptomanagerRequestDto cryptomanagerRequestDto) {
		RequestWrapper<KeymanagerSymmetricKeyRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(cryptomanagerRequestID);
		requestWrapper.setVersion(cryptomanagerRequestVersion);
		KeymanagerSymmetricKeyRequestDto keyManagerSymmetricKeyRequestDto = new KeymanagerSymmetricKeyRequestDto();
		dataMapper.map(cryptomanagerRequestDto, keyManagerSymmetricKeyRequestDto,
				new KeymanagerSymmetricKeyConverter());
		requestWrapper.setRequest(keyManagerSymmetricKeyRequestDto);
		HttpHeaders keyManagerRequestHeaders = new HttpHeaders();
		keyManagerRequestHeaders.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> response = null;
		HttpEntity<RequestWrapper<KeymanagerSymmetricKeyRequestDto>> keyManagerRequestEntity = new HttpEntity<>(
				requestWrapper, keyManagerRequestHeaders);
		try {
			response = restTemplate.exchange(decryptSymmetricKeyUrl, HttpMethod.POST, keyManagerRequestEntity,
					String.class);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
          authExceptionHandler(ex,KEYMANAGER);
		}
		throwExceptionIfExist(response);
		KeymanagerSymmetricKeyResponseDto keyManagerSymmetricKeyResponseDto = getResponse(response, KeymanagerSymmetricKeyResponseDto.class);
		byte[] symmetricKey = CryptoUtil.decodeBase64(keyManagerSymmetricKeyResponseDto.getSymmetricKey());
		return new SecretKeySpec(symmetricKey, 0, symmetricKey.length, symmetricAlgorithmName);
	}
	
	/**
	 * Change Parameter form to trim if not null
	 * 
	 * @param parameter parameter
	 * @return null if null;else trimmed string
	 */
	public static String nullOrTrim(String parameter) {
		return parameter == null ? null : parameter.trim();
	}
	
	/**
	 * Function to check is salt is valid
	 * 
	 * @param salt salt
	 * @return true if salt is valid, else false
	 */
	public boolean isValidSalt(String salt) {
		return salt != null && !salt.trim().isEmpty();
	}
	
	
	private void authExceptionHandler(HttpStatusCodeException ex, String source) {
		List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());
		
		if (ex.getRawStatusCode() == 401) {
			if (!validationErrorsList.isEmpty()) {
				throw new AuthNException(validationErrorsList);
			} else {
				throw new BadCredentialsException(AUTHENTICATION_FAILED+source);
			}
		}
		if (ex.getRawStatusCode() == 403) {
			if (!validationErrorsList.isEmpty()) {
				throw new AuthZException(validationErrorsList);
			} else {
				throw new AccessDeniedException(ACCESS_DENIED+source);
			}
		}
		

		if (!validationErrorsList.isEmpty()) {
			throw new KeymanagerServiceException(validationErrorsList);
		} else {
			throw new CryptoManagerSerivceException(CryptomanagerErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorCode(),
					CryptomanagerErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorMessage() + " "
							+ ex.getResponseBodyAsString());
		}
	}
	
	public void throwExceptionIfExist(ResponseEntity<String> response) {
		if(response == null) {
			throw new ParseResponseException(CryptomanagerErrorCode.CANNOT_CONNECT_TO_KEYMANAGER_SERVICE.getErrorCode(),
					CryptomanagerErrorCode.CANNOT_CONNECT_TO_KEYMANAGER_SERVICE.getErrorMessage() );
		}
		String responseBody = response.getBody();
		List<ServiceError> validationErrorList = ExceptionUtils.getServiceErrorList(responseBody);
		if (!validationErrorList.isEmpty()) {
			throw new KeymanagerServiceException(validationErrorList);
		}
	}
	
	public <S> S getResponse(ResponseEntity<String> response, Class<S> clazz) {
		try {
			JsonNode res =objectMapper.readTree(response.getBody());
			return objectMapper.readValue(res.get(RESPONSE).toString(), clazz);
		} catch (IOException|NullPointerException exception) {
			throw new ParseResponseException(CryptomanagerErrorCode.RESPONSE_PARSE_ERROR.getErrorCode(),
					CryptomanagerErrorCode.RESPONSE_PARSE_ERROR.getErrorMessage() + exception.getMessage(), exception);
		}
	}
	
	/**
	 * Parse a date string of pattern UTC_DATETIME_PATTERN into
	 * {@link LocalDateTime}
	 * 
	 * @param dateTime of type {@link String} of pattern UTC_DATETIME_PATTERN
	 * @return a {@link LocalDateTime} of given pattern
	 */
	public LocalDateTime parseToLocalDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
	}

	
	
	

}
