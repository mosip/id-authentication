package io.mosip.resident.service.impl;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import io.mosip.resident.exception.OtpValidationFailedException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.dto.AuthRequestDTO;
import io.mosip.resident.dto.AuthResponseDTO;
import io.mosip.resident.dto.AuthTypeDTO;
import io.mosip.resident.dto.AuthTypeStatus;
import io.mosip.resident.dto.AuthTypeStatusRequestDto;
import io.mosip.resident.dto.AuthTypeStatusResponseDto;
import io.mosip.resident.dto.OtpAuthRequestDTO;
import io.mosip.resident.dto.PublicKeyResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.service.IdAuthService;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;

@Component
public class IdAuthServiceImpl implements IdAuthService {

	private static final Logger logger = LoggerConfiguration.logConfig(IdAuthServiceImpl.class);

	@Value("${auth.internal.id}")
	private String internalAuthId;

	@Value("${auth.internal.version}")
	private String internalAuthVersion;

	@Value("${auth.type.status.id}")
	private String authTypeStatusId;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private KeyGenerator keyGenerator;

	@Autowired
	private TokenGenerator tokenGenerator;

	@Autowired
	private Environment environment;

	@Autowired
	private ResidentServiceRestClient restClient;

	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> encryptor;

	@Override
	public boolean validateOtp(String transactionID, String individualId, String individualIdType, String otp) throws OtpValidationFailedException {
		AuthResponseDTO response = null;
		try {
			response = internelOtpAuth(transactionID, individualId, individualIdType, otp);
		} catch (ApisResourceAccessException | InvalidKeySpecException | NoSuchAlgorithmException | IOException
				| JsonProcessingException e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), null,
					"IdAuthServiceImpl::validateOtp():: validate otp method call" + e.getStackTrace());
			throw new OtpValidationFailedException(e.getMessage());
		}
		if (response.getErrors() != null && response.getErrors().size() > 0) {
			response.getErrors().stream().forEach(error -> logger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.USERID.toString(), error.getErrorCode(), error.getErrorMessage()));
			OtpValidationFailedException e = new OtpValidationFailedException(
					response.getErrors().get(0).getErrorCode(), response.getErrors().get(0).getErrorMessage());
			throw e;
		}

		return response != null && response.getResponse().isAuthStatus();
	}

	public AuthResponseDTO internelOtpAuth(String transactionID, String individualId, String individualIdType,
			String otp) throws ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException,
			IOException, JsonProcessingException {
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), individualId,
				"IdAuthServiceImpl::internelOtpAuth()::entry");

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId(internalAuthId);
		authRequestDTO.setVersion(internalAuthVersion);
		authRequestDTO.setRequestTime(DateUtils.getUTCCurrentDateTimeString());
		authRequestDTO.setTransactionID(transactionID);

		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		authRequestDTO.setRequestedAuth(authType);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setIndividualId(individualId);
		authRequestDTO.setIndividualIdType(individualIdType);

		OtpAuthRequestDTO request = new OtpAuthRequestDTO();
		request.setOtp(otp);
		request.setTimestamp(DateUtils.getUTCCurrentDateTimeString());

		String identityBlock = mapper.writeValueAsString(request);

		final SecretKey secretKey = keyGenerator.getSymmetricKey();
		// Encrypted request with session key
		byte[] encryptedIdentityBlock = encryptor.symmetricEncrypt(secretKey, identityBlock.getBytes(), null);
		// rbase64 encoded for request
		authRequestDTO.setRequest(Base64.encodeBase64URLSafeString(encryptedIdentityBlock));
		// encrypted with MOSIP public key and encoded session key
		byte[] encryptedSessionKeyByte = encryptRSA(secretKey.getEncoded(), "INTERNAL");
		authRequestDTO.setRequestSessionKey(Base64.encodeBase64URLSafeString(encryptedSessionKeyByte));

		// sha256 of the request block before encryption and the hash is encrypted
		// using the requestSessionKey
		byte[] byteArray = encryptor.symmetricEncrypt(secretKey,
				HMACUtils.digestAsPlainText(HMACUtils.generateHash(identityBlock.getBytes())).getBytes(), null);
		authRequestDTO.setRequestHMAC(Base64.encodeBase64String(byteArray));

		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), individualId,
				"internelOtpAuth()::INTERNALAUTH POST service call started with request data "
						+ JsonUtils.javaObjectToJsonString(authRequestDTO));

		AuthResponseDTO response;
		try {
			response = (AuthResponseDTO) restClient.postApi(environment.getProperty(ApiName.INTERNALAUTH.name()),
					MediaType.APPLICATION_JSON, authRequestDTO, AuthResponseDTO.class, tokenGenerator.getToken());
		} catch (Exception e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), null,
					"IdAuthServiceImp::internelOtpAuth():: INTERNALAUTH GET service call" + e.getStackTrace());
			throw new ApisResourceAccessException("Could not fetch public key from kernel keymanager", e);
		}
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), individualId,
				"IdAuthServiceImpl::internelOtpAuth()::INTERNALAUTH POST service call ended with response data "
						+ JsonUtils.javaObjectToJsonString(response));

		return response;

	}

	private byte[] encryptRSA(final byte[] sessionKey, String refId)
			throws ApisResourceAccessException, InvalidKeySpecException, java.security.NoSuchAlgorithmException,
			IOException, JsonProcessingException {

		// encrypt AES Session Key using RSA public key
		ResponseWrapper<?> responseWrapper = null;
		PublicKeyResponseDto publicKeyResponsedto;

		String uri = environment.getProperty(ApiName.KERNELENCRYPTIONSERVICE.name());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);

		builder.pathSegment("IDA");
		builder.queryParam("referenceId", refId);
		builder.queryParam("timeStamp", DateUtils.getUTCCurrentDateTimeString());

		UriComponents uriComponent = builder.build(false).encode();

		try {
			responseWrapper = (ResponseWrapper<?>) restClient.getApi(uriComponent.toUri(), ResponseWrapper.class,
					tokenGenerator.getToken());
		} catch (Exception e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), refId,
					"IdAuthServiceImp::lencryptRSA():: ENCRYPTIONSERVICE GET service call" + e.getStackTrace());
			throw new ApisResourceAccessException("Could not fetch public key from kernel keymanager", e);
		}
		publicKeyResponsedto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
				PublicKeyResponseDto.class);

		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), refId,
				"IdAuthServiceImpl::encryptRSA():: ENCRYPTIONSERVICE GET service call ended with response data "
						+ JsonUtils.javaObjectToJsonString(responseWrapper));

		PublicKey publicKey = KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKeyResponsedto.getPublicKey())));

		return encryptor.asymmetricEncrypt(publicKey, sessionKey);

	}

	@Override
	public boolean authTypeStatusUpdate(String individualId, String individualIdType, List<String> authType,
			boolean isLock) throws ApisResourceAccessException {
		boolean isAuthTypeStatusSuccess = false;
		AuthTypeStatusRequestDto authTypeStatusRequestDto = new AuthTypeStatusRequestDto();
		authTypeStatusRequestDto.setConsentObtained(true);
		authTypeStatusRequestDto.setId(authTypeStatusId);
		authTypeStatusRequestDto.setIndividualIdType(individualIdType);
		authTypeStatusRequestDto.setIndividualId(individualId);
		authTypeStatusRequestDto.setVersion(internalAuthVersion);
		authTypeStatusRequestDto.setRequestTime(DateUtils.getUTCCurrentDateTimeString());
		List<AuthTypeStatus> authTypes = new ArrayList<>();
		for (String type : authType) {

			String[] types = type.split("-");
			AuthTypeStatus authTypeStatus = new AuthTypeStatus();
			if (types.length == 1) {
				authTypeStatus.setAuthType(types[0]);
			} else {
				authTypeStatus.setAuthType(types[0]);
				authTypeStatus.setAuthSubType(types[1]);
			}
			authTypeStatus.setLocked(isLock);
			authTypes.add(authTypeStatus);
		}
		authTypeStatusRequestDto.setRequest(authTypes);
		AuthTypeStatusResponseDto response = new AuthTypeStatusResponseDto();
		try {
			response = restClient.postApi(environment.getProperty(ApiName.AUTHTYPESTATUSUPDATE.name()),
					MediaType.APPLICATION_JSON, authTypeStatusRequestDto, AuthTypeStatusResponseDto.class, tokenGenerator.getToken());

			logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), individualId,
					"IdAuthServiceImp::authLock():: AUTHLOCK POST service call ended with response data "
							+ JsonUtils.javaObjectToJsonString(response));

		} catch (Exception e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), null,
					"IdAuthServiceImp::authLock():: AUTHLOCK POST service call" + e.getStackTrace());
			throw new ApisResourceAccessException("Could not able call auth status api", e);
		}

		if (response.getErrors() != null && response.getErrors().size() > 0) {
			response.getErrors().stream().forEach(error -> logger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.USERID.toString(), error.getErrorCode(), error.getErrorMessage()));

		} else {
			isAuthTypeStatusSuccess = true;
		}

		return isAuthTypeStatusSuccess;
	}

}
