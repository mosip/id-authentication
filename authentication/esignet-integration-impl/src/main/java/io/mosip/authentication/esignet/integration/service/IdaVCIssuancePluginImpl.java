package io.mosip.authentication.esignet.integration.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import foundation.identity.jsonld.JsonLDObject;
import io.mosip.authentication.esignet.integration.dto.IdaResponseWrapper;
import io.mosip.authentication.esignet.integration.dto.IdaVcExchangeRequest;
import io.mosip.authentication.esignet.integration.dto.VciCredentialsDefinitionRequestDTO;
import io.mosip.authentication.esignet.integration.helper.VCITransactionHelper;
import io.mosip.esignet.api.dto.VCRequestDto;
import io.mosip.esignet.api.dto.VCResult;
import io.mosip.esignet.api.spi.VCIssuancePlugin;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(value = "mosip.esignet.integration.vci-plugin", havingValue = "IdaVCIssuancePluginImpl")
public class IdaVCIssuancePluginImpl implements VCIssuancePlugin {
	private static final String CLIENT_ID = "client_id";
	private static final String RELYING_PARTY_ID = "relyingPartyId";
	private static final String ACCESS_TOKEN_HASH = "accessTokenHash";
	private static final String INDIVIDUAL_ID = "individualId";
	private static final String KYC_TOKEN = "kycToken";
	private static final String AUTH_TRANSACTION_ID = "authTransactionId";
	public static final String SIGNATURE_HEADER_NAME = "signature";
	public static final String AUTHORIZATION_HEADER_NAME = "Authorization";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	HelperService helperService;

	@Autowired
	VCITransactionHelper vciTransactionHelper;

	@Value("${mosip.esignet.ida.vci-exchange-url}")
	private String vciExchangeUrl;

	@Value("${mosip.esignet.ida.vci-exchange-id}")
	private String vciExchangeId;

	@Value("${mosip.esignet.ida.vci-exchange-version}")
	private String vciExchangeVersion;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public VCResult getVerifiableCredentialWithLinkedDataProof(VCRequestDto vcRequestDto, String holderId,
			Map<String, Object> identityDetails) {
		log.info("Started to created the VCIssuance");
		log.info("Started to build vci-exchange request : {} && clientId : {}",
				identityDetails.get(CLIENT_ID).toString());
		try {
			Map<String, Object> vciTransaction = vciTransactionHelper
					.getOAuthTransaction(identityDetails.get(ACCESS_TOKEN_HASH).toString());
			IdaVcExchangeRequest idaVciExchangeRequest = new IdaVcExchangeRequest();
			VciCredentialsDefinitionRequestDTO vciCred = new VciCredentialsDefinitionRequestDTO();
			idaVciExchangeRequest.setId(vciExchangeId);// Configuration
			idaVciExchangeRequest.setVersion(vciExchangeVersion);// Configuration
			idaVciExchangeRequest.setRequestTime(HelperService.getUTCDateTime());
			idaVciExchangeRequest.setTransactionID(vciTransaction.get(AUTH_TRANSACTION_ID).toString());// Cache input
			idaVciExchangeRequest.setVcAuthToken(vciTransaction.get(KYC_TOKEN).toString()); // Cache input
			idaVciExchangeRequest.setIndividualId(vciTransaction.get(INDIVIDUAL_ID).toString());
			idaVciExchangeRequest.setCredSubjectId(holderId);
			idaVciExchangeRequest.setVcFormat(vcRequestDto.getFormat());
			vciCred.setCredentialSubject(vcRequestDto.getCredentialSubject());
			vciCred.setType(List.of(
					(vcRequestDto.getTypes().length > 1 ? vcRequestDto.getTypes()[1] : vcRequestDto.getTypes()[0])));
			idaVciExchangeRequest.setCredentialsDefinition(vciCred);

			String requestBody = objectMapper.writeValueAsString(idaVciExchangeRequest);
			RequestEntity requestEntity = RequestEntity
					.post(UriComponentsBuilder.fromUriString(vciExchangeUrl)
							.pathSegment(vciTransaction.get(RELYING_PARTY_ID).toString(),
									identityDetails.get(CLIENT_ID).toString())
							.build().toUri())
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.header(SIGNATURE_HEADER_NAME, helperService.getRequestSignature(requestBody))
					.header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_NAME).body(requestBody);

			switch (vcRequestDto.getFormat()) {
			case "ldp_vc":
				ResponseEntity<IdaResponseWrapper<JsonLDObject>> responseEntity = restTemplate.exchange(requestEntity,
						new ParameterizedTypeReference<IdaResponseWrapper<JsonLDObject>>() {
						});
				return getLinkedDataProofCredential(responseEntity);
			default:
				log.error("Errors in response received from IDA VCI Exchange: {}");
				break;
			}
		} catch (Exception e) {
			log.error("IDA Vci-exchange failed ", e);
		}
		return null;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public VCResult getLinkedDataProofCredential(ResponseEntity responseEntity) {
		if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
			IdaResponseWrapper<JsonLDObject> responseWrapper = (IdaResponseWrapper<JsonLDObject>) responseEntity
					.getBody();
			if (responseWrapper.getResponse() != null) {
				VCResult vCResult = new VCResult();
				vCResult.setCredential(responseWrapper.getResponse());
				return vCResult;
			}
		}
		return null;
	}

	@Override
	public VCResult<String> getVerifiableCredential(VCRequestDto vcRequestDto, String holderId,
			Map<String, Object> identityDetails) {
		throw new NotImplementedException("This method is not implemented");
	}

}
