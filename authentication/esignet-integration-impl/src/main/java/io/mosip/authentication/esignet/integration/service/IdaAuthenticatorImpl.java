/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.mosip.authentication.esignet.integration.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.esignet.integration.dto.GetAllCertificatesResponse;
import io.mosip.authentication.esignet.integration.dto.IdaKycAuthRequest;
import io.mosip.authentication.esignet.integration.dto.IdaKycAuthResponse;
import io.mosip.authentication.esignet.integration.dto.IdaKycExchangeRequest;
import io.mosip.authentication.esignet.integration.dto.IdaKycExchangeResponse;
import io.mosip.authentication.esignet.integration.dto.IdaResponseWrapper;
import io.mosip.authentication.esignet.integration.dto.IdaSendOtpRequest;
import io.mosip.authentication.esignet.integration.helper.AuthTransactionHelper;
import io.mosip.esignet.api.dto.KycAuthDto;
import io.mosip.esignet.api.dto.KycAuthResult;
import io.mosip.esignet.api.dto.KycExchangeDto;
import io.mosip.esignet.api.dto.KycExchangeResult;
import io.mosip.esignet.api.dto.KycSigningCertificateData;
import io.mosip.esignet.api.dto.SendOtpDto;
import io.mosip.esignet.api.dto.SendOtpResult;
import io.mosip.esignet.api.exception.KycAuthException;
import io.mosip.esignet.api.exception.KycExchangeException;
import io.mosip.esignet.api.exception.KycSigningCertificateException;
import io.mosip.esignet.api.exception.SendOtpException;
import io.mosip.esignet.api.spi.Authenticator;
import io.mosip.esignet.api.util.ErrorConstants;
import io.mosip.kernel.core.http.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;


@ConditionalOnProperty(value = "mosip.esignet.integration.authenticator", havingValue = "IdaAuthenticatorImpl")
@Component
@Slf4j
public class IdaAuthenticatorImpl implements Authenticator {

    public static final String SIGNATURE_HEADER_NAME = "signature";
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String KYC_EXCHANGE_TYPE = "oidc";

    @Value("${mosip.esignet.authenticator.ida-auth-id:mosip.identity.kycauth}")
    private String kycAuthId;

    @Value("${mosip.esignet.authenticator.ida-exchange-id:mosip.identity.kycexchange}")
    private String kycExchangeId;

    @Value("${mosip.esignet.authenticator.ida-version:1.0}")
    private String idaVersion;

    @Value("${mosip.esignet.authenticator.ida-domainUri}")
    private String idaDomainUri;

    @Value("${mosip.esignet.authenticator.ida-env:Staging}")
    private String idaEnv;

    @Value("${mosip.esignet.authenticator.ida.kyc-auth-url}")
    private String kycAuthUrl;
    
    @Value("${mosip.esignet.authenticator.ida.kyc-exchange-url}")
    private String kycExchangeUrl;

    @Value("${mosip.esignet.authenticator.ida.otp-channels}")
    private List<String> otpChannels;

    @Value("${mosip.esignet.authenticator.ida.get-certificates-url}")
    private String getCertsUrl;
    
    @Value("${mosip.esignet.authenticator.ida.application-id:IDA}")
    private String applicationId;
    
    @Value("${mosip.esignet.authenticator.ida.reference-id:SIGN}")
    private String referenceId;
    
    @Value("${mosip.esignet.authenticator.ida.client-id}")
    private String clientId;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    HelperService helperService;
    
    @Autowired
    private AuthTransactionHelper authTransactionHelper;

    @Override
    public KycAuthResult doKycAuth(String relyingPartyId, String clientId, KycAuthDto kycAuthDto)
            throws KycAuthException {
        log.info("Started to build kyc-auth request with transactionId : {} && clientId : {}",
                kycAuthDto.getTransactionId(), clientId);
        try {
            IdaKycAuthRequest idaKycAuthRequest = new IdaKycAuthRequest();
            idaKycAuthRequest.setId(kycAuthId);
            idaKycAuthRequest.setVersion(idaVersion);
            idaKycAuthRequest.setRequestTime(HelperService.getUTCDateTime());
            idaKycAuthRequest.setDomainUri(idaDomainUri);
            idaKycAuthRequest.setEnv(idaEnv);
            idaKycAuthRequest.setConsentObtained(true);
            idaKycAuthRequest.setIndividualId(kycAuthDto.getIndividualId());
            idaKycAuthRequest.setTransactionID(kycAuthDto.getTransactionId());
            helperService.setAuthRequest(kycAuthDto.getChallengeList(), idaKycAuthRequest);

            //set signature header, body and invoke kyc auth endpoint
            String requestBody = objectMapper.writeValueAsString(idaKycAuthRequest);
            RequestEntity requestEntity = RequestEntity
                    .post(UriComponentsBuilder.fromUriString(kycAuthUrl).pathSegment(relyingPartyId, clientId).build().toUri())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .header(SIGNATURE_HEADER_NAME, helperService.getRequestSignature(requestBody))
                    .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_NAME)
                    .body(requestBody);
            ResponseEntity<IdaResponseWrapper<IdaKycAuthResponse>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<IdaResponseWrapper<IdaKycAuthResponse>>() {});

            if(responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                IdaResponseWrapper<IdaKycAuthResponse> responseWrapper = responseEntity.getBody();
                if (responseWrapper != null) {
                    if (responseWrapper.getResponse() != null && responseWrapper.getResponse().isKycStatus() && responseWrapper.getResponse().getKycToken() != null) {
                        return new KycAuthResult(responseWrapper.getResponse().getKycToken(),
                                responseWrapper.getResponse().getAuthToken());
                    } else if (responseWrapper.getErrors() != null && responseWrapper.getResponse() != null) {
                            log.error("Error response received from IDA KycStatus: {} && Errors: {}",
                                        responseWrapper.getResponse().isKycStatus(), responseWrapper.getErrors());
                            throw new KycAuthException(CollectionUtils.isEmpty(responseWrapper.getErrors()) ?
                                    ErrorConstants.AUTH_FAILED : responseWrapper.getErrors().get(0).getErrorCode());
                    }
                }
            }

            log.error("Error response received from IDA (Kyc-auth) with status : {}", responseEntity.getStatusCode());
        } catch (KycAuthException e) { throw e; } catch (Exception e) {
            log.error("KYC-auth failed with transactionId : {} && clientId : {}", kycAuthDto.getTransactionId(),
                    clientId, e);
        }
        throw new KycAuthException(ErrorConstants.AUTH_FAILED);
    }

    @Override
    public KycExchangeResult doKycExchange(String relyingPartyId, String clientId, KycExchangeDto kycExchangeDto)
            throws KycExchangeException {
        log.info("Started to build kyc-exchange request with transactionId : {} && clientId : {}",
                kycExchangeDto.getTransactionId(), clientId);
        try {
            IdaKycExchangeRequest idaKycExchangeRequest = new IdaKycExchangeRequest();
            idaKycExchangeRequest.setId(kycExchangeId);
            idaKycExchangeRequest.setVersion(idaVersion);
            idaKycExchangeRequest.setRequestTime(HelperService.getUTCDateTime());
            idaKycExchangeRequest.setTransactionID(kycExchangeDto.getTransactionId());
            idaKycExchangeRequest.setKycToken(kycExchangeDto.getKycToken());
	    if (!CollectionUtils.isEmpty(kycExchangeDto.getAcceptedClaims())) {
                idaKycExchangeRequest.setConsentObtained(kycExchangeDto.getAcceptedClaims());
            } else {
                idaKycExchangeRequest.setConsentObtained(List.of("sub"));
            }
            idaKycExchangeRequest.setLocales(Arrays.asList(kycExchangeDto.getClaimsLocales()));
            idaKycExchangeRequest.setRespType(kycExchangeDto.getUserInfoResponseType()); //may be either JWT or JWE
            idaKycExchangeRequest.setIndividualId(kycExchangeDto.getIndividualId());

            //set signature header, body and invoke kyc exchange endpoint
            String requestBody = objectMapper.writeValueAsString(idaKycExchangeRequest);
            RequestEntity requestEntity = RequestEntity
                    .post(UriComponentsBuilder.fromUriString(kycExchangeUrl).pathSegment(relyingPartyId,
                            clientId).build().toUri())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .header(SIGNATURE_HEADER_NAME, helperService.getRequestSignature(requestBody))
                    .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_NAME)
                    .body(requestBody);
            ResponseEntity<IdaResponseWrapper<IdaKycExchangeResponse>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<IdaResponseWrapper<IdaKycExchangeResponse>>() {});

            if(responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                IdaResponseWrapper<IdaKycExchangeResponse> responseWrapper = responseEntity.getBody();
                if (responseWrapper != null) {
                    if (responseWrapper.getResponse() != null && responseWrapper.getResponse().getEncryptedKyc() != null) {
                        return new KycExchangeResult(responseWrapper.getResponse().getEncryptedKyc());
                    } else if (responseWrapper.getErrors() != null) {
                            log.error("Errors in response received from IDA Kyc Exchange: {}", responseWrapper.getErrors());
                            throw new KycExchangeException(CollectionUtils.isEmpty(responseWrapper.getErrors()) ?
                                    ErrorConstants.DATA_EXCHANGE_FAILED : responseWrapper.getErrors().get(0).getErrorCode());
                    }
                }
            }

            log.error("Error response received from IDA (Kyc-exchange) with status : {}", responseEntity.getStatusCode());
        } catch (KycExchangeException e) { throw e; } catch (Exception e) {
            log.error("IDA Kyc-exchange failed with clientId : {}", clientId, e);
        }
        throw new KycExchangeException();
    }

    @Override
    public SendOtpResult sendOtp(String relyingPartyId, String clientId, SendOtpDto sendOtpDto)  throws SendOtpException {
        log.info("Started to build send-otp request with transactionId : {} && clientId : {}",
                sendOtpDto.getTransactionId(), clientId);
        try {
            IdaSendOtpRequest idaSendOtpRequest = new IdaSendOtpRequest();
            idaSendOtpRequest.setOtpChannel(sendOtpDto.getOtpChannels());
            idaSendOtpRequest.setIndividualId(sendOtpDto.getIndividualId());
            idaSendOtpRequest.setTransactionID(sendOtpDto.getTransactionId());
            return helperService.sendOTP(relyingPartyId, clientId, idaSendOtpRequest);
        } catch (SendOtpException e) {
            throw e;
        } catch (Exception e) {
            log.error("send-otp failed with clientId : {}", clientId, e);
        }
        throw new SendOtpException();
    }

    @Override
    public boolean isSupportedOtpChannel(String channel) {
        return channel != null && otpChannels.contains(channel.toLowerCase());
    }

    @Override
    public List<KycSigningCertificateData> getAllKycSigningCertificates() throws KycSigningCertificateException {
    	try {
    		String authToken = authTransactionHelper.getAuthToken();

            RequestEntity requestEntity = RequestEntity
                     .get(UriComponentsBuilder.fromUriString(getCertsUrl).queryParam("applicationId", applicationId).queryParam("referenceId", referenceId).build().toUri())
                     .header(HttpHeaders.COOKIE, "Authorization=" + authToken)
                     .build();
            
            ResponseEntity<ResponseWrapper<GetAllCertificatesResponse>> responseEntity = restTemplate.exchange(requestEntity,
                     new ParameterizedTypeReference<ResponseWrapper<GetAllCertificatesResponse>>() {});
            
            if(responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            	ResponseWrapper<GetAllCertificatesResponse> responseWrapper = responseEntity.getBody();
                if (responseWrapper != null) {
                    if (responseWrapper.getResponse() != null && responseWrapper.getResponse().getAllCertificates() != null) {
                        return responseWrapper.getResponse().getAllCertificates();
                    } else if (responseWrapper.getErrors() != null) {
                            log.error("Error response received from getAllSigningCertificates with errors: {}", responseWrapper.getErrors());
                            throw new KycSigningCertificateException(CollectionUtils.isEmpty(responseWrapper.getErrors()) ?
                                    ErrorConstants.KYC_SIGNING_CERTIFICATE_FAILED : responseWrapper.getErrors().get(0).getErrorCode());
                    }
                }
            }
            log.error("Error response received from getAllSigningCertificates with status : {}", responseEntity.getStatusCode());
    	} catch (KycSigningCertificateException e) { throw e; } catch (Exception e) {
            log.error("getAllKycSigningCertificates failed with clientId : {}", clientId, e);
        }
    	throw new KycSigningCertificateException();
    }
}
