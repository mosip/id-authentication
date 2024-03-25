package io.mosip.authentication.esignet.integration.service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import javax.crypto.Cipher;

import io.mosip.authentication.esignet.integration.dto.IdaVcExchangeResponse;
import io.mosip.esignet.api.exception.VCIExchangeException;
import io.mosip.esignet.api.util.ErrorConstants;
import io.mosip.esignet.core.dto.OIDCTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import foundation.identity.jsonld.JsonLDObject;
import io.mosip.authentication.esignet.integration.dto.IdaResponseWrapper;
import io.mosip.authentication.esignet.integration.dto.IdaVcExchangeRequest;
import io.mosip.authentication.esignet.integration.dto.CredentialDefinitionDTO;
import io.mosip.authentication.esignet.integration.helper.VCITransactionHelper;
import io.mosip.esignet.api.dto.VCRequestDto;
import io.mosip.esignet.api.dto.VCResult;
import io.mosip.esignet.api.spi.VCIssuancePlugin;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.helper.KeymanagerDBHelper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(value = "mosip.esignet.integration.vci-plugin", havingValue = "IdaVCIssuancePluginImpl")
public class IdaVCIssuancePluginImpl implements VCIssuancePlugin {
	private static final String CLIENT_ID = "client_id";
	private static final String ACCESS_TOKEN_HASH = "accessTokenHash";
	public static final String SIGNATURE_HEADER_NAME = "signature";
	public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	public static final String OIDC_SERVICE_APP_ID = "OIDC_SERVICE";
	public static final String AES_CIPHER_FAILED = "aes_cipher_failed";
	public static final String NO_UNIQUE_ALIAS = "no_unique_alias";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	HelperService helperService;

	@Autowired
	private KeyStore keyStore;

	@Autowired
	private KeymanagerDBHelper dbHelper;

	@Autowired
	VCITransactionHelper vciTransactionHelper;

	@Value("${mosip.esignet.ida.vci-exchange-url}")
	private String vciExchangeUrl;

	@Value("${mosip.esignet.ida.vci-exchange-id}")
	private String vciExchangeId;

	@Value("${mosip.esignet.ida.vci-exchange-version}")
	private String vciExchangeVersion;

	@Value("${mosip.esignet.cache.secure.individual-id}")
	private boolean secureIndividualId;

	@Value("${mosip.esignet.cache.store.individual-id}")
	private boolean storeIndividualId;

	@Value("${mosip.esignet.cache.security.algorithm-name}")
	private String aesECBTransformation;

	@Value("${mosip.esignet.cache.security.secretkey.reference-id}")
	private String cacheSecretKeyRefId;

	private Base64.Decoder urlSafeDecoder = Base64.getUrlDecoder();


	@Override
	public VCResult<JsonLDObject> getVerifiableCredentialWithLinkedDataProof(VCRequestDto vcRequestDto, String holderId,
			Map<String, Object> identityDetails) throws VCIExchangeException {
		log.info("Started to created the VCIssuance");
		try {
			OIDCTransaction transaction = vciTransactionHelper
					.getOAuthTransaction(identityDetails.get(ACCESS_TOKEN_HASH).toString());
			String individualId = getIndividualId(transaction.getIndividualId());
			IdaVcExchangeRequest idaVciExchangeRequest = new IdaVcExchangeRequest();
			CredentialDefinitionDTO vciCred = new CredentialDefinitionDTO();
			idaVciExchangeRequest.setId(vciExchangeId);// Configuration
			idaVciExchangeRequest.setVersion(vciExchangeVersion);// Configuration
			idaVciExchangeRequest.setRequestTime(HelperService.getUTCDateTime());
			idaVciExchangeRequest.setTransactionID(transaction.getAuthTransactionId());// Cache input
			idaVciExchangeRequest.setVcAuthToken(transaction.getKycToken()); // Cache input
			idaVciExchangeRequest.setIndividualId(individualId);
			idaVciExchangeRequest.setCredSubjectId(holderId);
			idaVciExchangeRequest.setVcFormat(vcRequestDto.getFormat());
			idaVciExchangeRequest.setLocales(convertLangCodesToISO3LanguageCodes(transaction.getClaimsLocales()));
			vciCred.setCredentialSubject(vcRequestDto.getCredentialSubject());
			vciCred.setType(vcRequestDto.getType());
			vciCred.setContext(vcRequestDto.getContext());
			idaVciExchangeRequest.setCredentialsDefinition(vciCred);

			String requestBody = objectMapper.writeValueAsString(idaVciExchangeRequest);
			RequestEntity requestEntity = RequestEntity
					.post(UriComponentsBuilder.fromUriString(vciExchangeUrl)
							.pathSegment(transaction.getRelyingPartyId(),
									identityDetails.get(CLIENT_ID).toString())
							.build().toUri())
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.header(SIGNATURE_HEADER_NAME, helperService.getRequestSignature(requestBody))
					.header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_NAME).body(requestBody);

			ResponseEntity<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>> responseEntity = restTemplate.exchange(
					requestEntity, new ParameterizedTypeReference<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>>() {});
			if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
				IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>> responseWrapper = responseEntity.getBody();
				if (responseWrapper != null || responseWrapper.getResponse() != null) {
					VCResult vCResult = new VCResult();
					vCResult.setCredential(responseWrapper.getResponse().getVerifiableCredentials());
					vCResult.setFormat(vcRequestDto.getFormat());
					return vCResult;
				}
				log.error("Errors in response received from IDA VCI Exchange: {}", responseWrapper.getErrors());
				throw new VCIExchangeException(CollectionUtils.isEmpty(responseWrapper.getErrors()) ?
						ErrorConstants.DATA_EXCHANGE_FAILED : responseWrapper.getErrors().get(0).getErrorCode());
			}
			log.error("Error response received from IDA (VCI-exchange) with status : {}", responseEntity.getStatusCode());
		} catch (VCIExchangeException e) { throw e; } catch (Exception e) {
			log.error("IDA Vci-exchange failed ", e);
		}
		throw new VCIExchangeException();
	}

	@Override
	public VCResult<String> getVerifiableCredential(VCRequestDto vcRequestDto, String holderId,
			Map<String, Object> identityDetails) throws VCIExchangeException {
		throw new VCIExchangeException(ErrorConstants.NOT_IMPLEMENTED);
	}

	protected String getIndividualId(String encryptedIndividualId) throws Exception {
		if (!storeIndividualId)
			return null;
		return secureIndividualId ? decryptIndividualId(encryptedIndividualId) : encryptedIndividualId;
	}

	private String decryptIndividualId(String encryptedIndividualId) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(aesECBTransformation);
			byte[] decodedBytes = b64Decode(encryptedIndividualId);
			cipher.init(Cipher.DECRYPT_MODE, getSecretKeyFromHSM());
			return new String(cipher.doFinal(decodedBytes, 0, decodedBytes.length));
		} catch (Exception e) {
			log.error("Error Cipher Operations of provided secret data.", e);
			throw new Exception(AES_CIPHER_FAILED);
		}
	}

	private Key getSecretKeyFromHSM() throws Exception {
		String keyAlias = getKeyAlias(OIDC_SERVICE_APP_ID, cacheSecretKeyRefId);
		if (Objects.nonNull(keyAlias)) {
			return keyStore.getSymmetricKey(keyAlias);
		}
		throw new Exception(NO_UNIQUE_ALIAS);
	}

	private String getKeyAlias(String keyAppId, String keyRefId) throws Exception {
		Map<String, List<KeyAlias>> keyAliasMap = dbHelper.getKeyAliases(keyAppId, keyRefId,
				LocalDateTime.now(ZoneOffset.UTC));
		List<KeyAlias> currentKeyAliases = keyAliasMap.get(KeymanagerConstant.CURRENTKEYALIAS);
		if (!currentKeyAliases.isEmpty() && currentKeyAliases.size() == 1) {
			return currentKeyAliases.get(0).getAlias();
		}
		log.error("CurrentKeyAlias is not unique. KeyAlias count: {}", currentKeyAliases.size());
		throw new Exception(NO_UNIQUE_ALIAS);
	}

	private byte[] b64Decode(String value) {
		return urlSafeDecoder.decode(value);
	};

	//Converts an array of two-letter language codes to their corresponding ISO 639-2/T language codes.
	private List<String> convertLangCodesToISO3LanguageCodes(String[] langCodes) {
		if(langCodes == null || langCodes.length == 0)
			return List.of("eng");
		return Arrays.stream(langCodes)
				.map(langCode -> {
					try {
						return new Locale(langCode).getISO3Language();
					} catch (MissingResourceException ex) {}
					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
