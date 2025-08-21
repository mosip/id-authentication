/**
 * 
 */
package io.mosip.authentication.service.kyc.facade;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.EMPTY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_CLAIMS_ATTRIBS;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.KycTokenData;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.TokenValidationHelper;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.KycTokenDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.KycTokenStatusType;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.BaseAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.EKycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.EKycResponseDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.EncryptedKycRespDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRespDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRespDTOV2;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTOV2;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTOV2;
import io.mosip.authentication.core.indauth.dto.KycExchangeResponseDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.KYCAttributes;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.facade.KycFacade;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.service.kyc.util.ExchangeDataAttributesUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import reactor.util.function.Tuple3;

/**
 * 
 *
 * Facade to authentication KYC details
 * 
 * @author Dinesh Karuppiah.T
 */
@Component
public class KycFacadeImpl implements KycFacade {

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(KycFacadeImpl.class);

	@Value("${ida.idp.consented.individual_id.attribute.name:individual_id}")
	private String consentedIndividualIdAttributeName;

	/** The env. */
	@Autowired
	private EnvUtil env;

	@Autowired
	private AuthFacade authFacade;

	/** The Kyc Service */
	@Autowired
	private KycService kycService;

	/** The Id Info Service */
	@Autowired
	private IdService<AutnTxn> idService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	@Autowired
	private IdaUinHashSaltRepo uinHashSaltRepo;

	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private PartnerService partnerService;
	
	@Autowired
	private IdAuthFraudAnalysisEventManager fraudEventManager;	
	
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KycTokenDataRepository kycTokenDataRepo;

	@Autowired
	private TokenValidationHelper tokenValidationHelper;

	@Autowired
	private ExchangeDataAttributesUtil exchangeDataAttributesUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.KycFacade#
	 * authenticateIndividual(io.mosip.authentication.core.indauth.dto.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request, String partnerId, String partnerApiKey, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		return authFacade.authenticateIndividual(authRequest, request, partnerId, partnerApiKey, IdAuthCommonConstants.CONSUME_VID_DEFAULT, requestWithMetadata);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.KycFacade#
	 * authenticateIndividual(io.mosip.authentication.core.indauth.dto.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request, String partnerId, 
							String partnerApiKey, ObjectWithMetadata requestWithMetadata, boolean markVidConsumed)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		return authFacade.authenticateIndividual(authRequest, request, partnerId, partnerApiKey, markVidConsumed, requestWithMetadata);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.facade.KycFacade#processKycAuth(io.
	 * mosip.authentication.core.indauth.dto.KycAuthRequestDTO,
	 * io.mosip.authentication.core.indauth.dto.AuthResponseDTO, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EKycAuthResponseDTO processEKycAuth(@Nonnull EkycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId, Map<String, Object>  metadata) throws IdAuthenticationBusinessException {
		boolean status;
		String token = null;
		String idHash = null;
		EKycAuthResponseDTO kycAuthResponseDTO = null;
		try {
			Map<String, Object> idResDTO = (Map<String, Object>) metadata.get(IdAuthCommonConstants.IDENTITY_DATA);
			token = idService.getToken(idResDTO);
			idHash = idService.getIdHash(idResDTO);
			Map<String, List<IdentityInfoDTO>> idInfo = (Map<String, List<IdentityInfoDTO>>) metadata.get(IdAuthCommonConstants.IDENTITY_INFO);

			Entry<EKycAuthResponseDTO, Boolean> kycAuthResponse = doProcessEKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId,
					idInfo, token);
			kycAuthResponseDTO = kycAuthResponse.getKey();
			status = kycAuthResponse.getValue();
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, token, authResponseDTO, kycAuthResponseDTO, metadata, false);
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.EKYC_REQUEST_RESPONSE,
						kycAuthRequestDTO.getTransactionID(), IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()),
						"kycAuthentication status : " + status);
			return kycAuthResponseDTO;
		} catch (IdAuthenticationBusinessException e) {
			status = false;
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, token, authResponseDTO, kycAuthResponseDTO, metadata, false);
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.EKYC_REQUEST_RESPONSE,
					kycAuthRequestDTO.getTransactionID(),	IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()), e);
			throw e;
		}
	}

	private void saveToTxnTable(AuthRequestDTO authRequestDTO, boolean status, String partnerId, String token, 
			AuthResponseDTO authResponseDTO, BaseAuthResponseDTO baseAuthResponseDTO, Map<String, Object> metadata, boolean isKycAuthReq)
			throws IdAuthenticationBusinessException {
		if (token != null) {
			Boolean authTokenRequired = EnvUtil.getAuthTokenRequired();
			String authTokenId = authTokenRequired ? tokenIdManager.generateTokenId(token, partnerId) : null;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, authRequestDTO.getMetadata());

			if(baseAuthResponseDTO != null && authResponseDTO != null) {
				Object authTxnObj = metadata.get(AutnTxn.class.getSimpleName());
				if(authTxnObj instanceof AutnTxn) {
					AutnTxn autnTxn = (AutnTxn) authTxnObj;
					String authTypeCode = autnTxn.getAuthTypeCode();
					if (authTypeCode == null || !authTypeCode.contains(RequestType.EKYC_AUTH_REQUEST.getRequestType())) {
						String statusComment = autnTxn.getStatusComment();
						if (isKycAuthReq) {
							autnTxn.setAuthTypeCode(RequestType.KYC_AUTH_REQUEST.getRequestType()
								+ (authTypeCode == null ? "" : AuthTransactionBuilder.REQ_TYPE_DELIM + authTypeCode));
							autnTxn.setStatusComment(RequestType.KYC_AUTH_REQUEST.getMessage() + (statusComment == null ? ""
								: AuthTransactionBuilder.REQ_TYPE_MSG_DELIM + statusComment));
						} else {
							autnTxn.setAuthTypeCode(RequestType.EKYC_AUTH_REQUEST.getRequestType()
								+ (authTypeCode == null ? "" : AuthTransactionBuilder.REQ_TYPE_DELIM + authTypeCode));
							autnTxn.setStatusComment(RequestType.EKYC_AUTH_REQUEST.getMessage() + (statusComment == null ? ""
								: AuthTransactionBuilder.REQ_TYPE_MSG_DELIM + statusComment));
						}
					}
					metadata.put(AutnTxn.class.getSimpleName(), autnTxn);
				}
			} else {
				AutnTxn authTxn = AuthTransactionBuilder.newInstance().withRequest(authRequestDTO)
						.addRequestType(RequestType.EKYC_AUTH_REQUEST).withAuthToken(authTokenId).withStatus(status)
						.withInternal(false).withPartner(partner).withToken(token)
						.build(env, uinHashSaltRepo, securityManager);
				fraudEventManager.analyseEvent(authTxn);
				idService.saveAutnTxn(authTxn);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Entry<EKycAuthResponseDTO, Boolean> doProcessEKycAuth(EkycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId, Map<String, List<IdentityInfoDTO>> idInfo, String token)
			throws IdAuthenticationBusinessException, IDDataValidationException {

		EKycAuthResponseDTO kycAuthResponseDTO = new EKycAuthResponseDTO();
		
		if (kycAuthRequestDTO != null) {

			EKycResponseDTO response = new EKycResponseDTO();
			ResponseDTO authResponse = authResponseDTO.getResponse();
			Set<String> langCodes = mapper
					.convertValue(kycAuthRequestDTO.getMetadata().get(IdAuthCommonConstants.KYC_LANGUAGES), Set.class);
			if (Objects.nonNull(idInfo) && Objects.nonNull(authResponse) && authResponse.isAuthStatus()) {
				response = kycService.retrieveKycInfo(kycAuthRequestDTO.getAllowedKycAttributes(),
						langCodes
						, idInfo);
			}
			if (Objects.nonNull(authResponse) && Objects.nonNull(authResponseDTO)) {
				response.setKycStatus(authResponse.isAuthStatus());
				response.setAuthToken(authResponse.getAuthToken());
				
				if(Objects.nonNull(response.getIdentity())) {
					String partnerCertificate = (String) kycAuthRequestDTO.getMetadata().get(IdAuthCommonConstants.PARTNER_CERTIFICATE);
					Tuple3<String, String, String> encryptKycResponse = encryptKycResponse(response.getIdentity(),partnerCertificate);
					response.setSessionKey(encryptKycResponse.getT1());
					response.setIdentity(encryptKycResponse.getT2());
					response.setThumbprint(encryptKycResponse.getT3());
				}
				
				kycAuthResponseDTO.setResponse(response);
				kycAuthResponseDTO.setId(authResponseDTO.getId());
				kycAuthResponseDTO.setTransactionID(authResponseDTO.getTransactionID());
				kycAuthResponseDTO.setVersion(authResponseDTO.getVersion());
				kycAuthResponseDTO.setErrors(authResponseDTO.getErrors());
				String responseTime = authResponseDTO.getResponseTime();
				if(responseTime != null) {
					kycAuthResponseDTO.setResponseTime(responseTime);
				} else {
					String resTime = getAuthResponseTime(kycAuthRequestDTO);
					kycAuthResponseDTO.setResponseTime(resTime);
				}
			}

			return new SimpleEntry<>(kycAuthResponseDTO, response.isKycStatus());
		}
		return new SimpleEntry<>(kycAuthResponseDTO, false);
	}

	private String getAuthResponseTime(AuthRequestDTO kycAuthRequestDTO) {
		String dateTimePattern = EnvUtil.getDateTimePattern();
		return IdaRequestResponsConsumerUtil.getResponseTime(kycAuthRequestDTO.getRequestTime(), dateTimePattern);
	}

	private Tuple3<String, String, String> encryptKycResponse(String identity, String partnerCertificate) throws IdAuthenticationBusinessException {
		try {
			return securityManager.encryptData(identity.getBytes(), partnerCertificate);
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public KycAuthResponseDTO processKycAuth(@Nonnull AuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId, String oidcClientId, Map<String, Object>  metadata) throws IdAuthenticationBusinessException {
		boolean status;
		String token = null;
		KycAuthResponseDTO kycAuthResponseDTO = null;
		try {
			Map<String, Object> idResDTO = (Map<String, Object>) metadata.get(IdAuthCommonConstants.IDENTITY_DATA);
			token = idService.getToken(idResDTO);

			Entry<BaseAuthResponseDTO, Boolean> kycAuthResponse = doProcessKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId, 
							oidcClientId, idResDTO, false);
			kycAuthResponseDTO = (KycAuthResponseDTO) kycAuthResponse.getKey();
			status = kycAuthResponse.getValue();
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, token, authResponseDTO, kycAuthResponseDTO, metadata, true);
			auditHelper.audit(AuditModules.KYC_AUTH, AuditEvents.KYC_REQUEST_RESPONSE,
					kycAuthRequestDTO.getTransactionID(),	IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()),
					"kycAuthentication status : " + status);
			return kycAuthResponseDTO;
		} catch (IdAuthenticationBusinessException e) {
			status = false;
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, token, authResponseDTO, kycAuthResponseDTO, metadata, true);
			auditHelper.audit(AuditModules.KYC_AUTH, AuditEvents.KYC_REQUEST_RESPONSE,
							  kycAuthRequestDTO.getTransactionID(), IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()), e);
			throw e;
		}
	}

	private Entry<BaseAuthResponseDTO, Boolean> doProcessKycAuth(AuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId, String oidcClientId, Map<String, Object> idResDTO, boolean v2Resp) throws IdAuthenticationBusinessException, IDDataValidationException {

		BaseAuthResponseDTO baseAuthResponseDTO = !v2Resp ? new KycAuthResponseDTO() : new KycAuthResponseDTOV2();

		if (kycAuthRequestDTO != null) {

			String idHash = idService.getIdHash(idResDTO);
			ResponseDTO authResponse = authResponseDTO.getResponse();
			String responseTime = authResponseDTO.getResponseTime();
			if(Objects.isNull(responseTime)) {
				responseTime = getAuthResponseTime(kycAuthRequestDTO);
			}

			String requestTime = kycAuthRequestDTO.getRequestTime();
			String kycToken = null;
			if (Objects.nonNull(authResponse) && authResponse.isAuthStatus()) {
				kycToken = kycService.generateAndSaveKycToken(idHash, authResponse.getAuthToken(), oidcClientId, requestTime, responseTime, kycAuthRequestDTO.getTransactionID());
			}
			if (Objects.nonNull(authResponse) && Objects.nonNull(authResponseDTO)) {
				baseAuthResponseDTO.setId(authResponseDTO.getId());
				baseAuthResponseDTO.setTransactionID(authResponseDTO.getTransactionID());
				baseAuthResponseDTO.setVersion(authResponseDTO.getVersion());
				baseAuthResponseDTO.setErrors(authResponseDTO.getErrors());
				baseAuthResponseDTO.setResponseTime(responseTime);
				if (!v2Resp){
					KycAuthRespDTO response = new KycAuthRespDTO();
					response.setKycToken(kycToken);
					response.setKycStatus(authResponse.isAuthStatus());
					response.setAuthToken(authResponse.getAuthToken());
					((KycAuthResponseDTO)baseAuthResponseDTO).setResponse(response);
				} else {
					KycAuthRespDTOV2 response = new KycAuthRespDTOV2();
					Map<String, List<IdentityInfoDTO>> idInfo = IdInfoFetcher.getIdInfo(idResDTO, mapper);
					List<IdentityInfoDTO> idInfoList = idInfo.get(VERIFIED_CLAIMS_ATTRIBS);
					response.setKycToken(kycToken);
					response.setKycStatus(authResponse.isAuthStatus());
					response.setAuthToken(authResponse.getAuthToken());
					String verifiedClaimsMetadata = !CollectionUtils.isEmpty(idInfoList) ? idInfoList.get(0).getValue() : EMPTY;
					response.setVerifiedClaimsMetadata(kycService.buildVerifiedClaimsMetadata(verifiedClaimsMetadata, oidcClientId));
					((KycAuthResponseDTOV2)baseAuthResponseDTO).setResponse(response);
				}
			}

			return new SimpleEntry<>(baseAuthResponseDTO, authResponse.isAuthStatus());
		}
		return new SimpleEntry<>(baseAuthResponseDTO, false);
	}

	@Override
	public KycExchangeResponseDTO processKycExchange(KycExchangeRequestDTO kycExchangeRequestDTO, String partnerId, 
			String oidcClientId, Map<String, Object>  metadata, ObjectWithMetadata requestWithMetadata) throws IdAuthenticationBusinessException {
		
		try {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchange",
					"Processing Kyc Exchange request.");
			
			String kycAuthToken = kycExchangeRequestDTO.getKycToken();
			String idVid = kycExchangeRequestDTO.getIndividualId();
			String idvidHash = securityManager.hash(idVid);

			KycTokenData kycTokenData = tokenValidationHelper.findAndValidateIssuedToken(kycAuthToken, oidcClientId,
						kycExchangeRequestDTO.getTransactionID(), idvidHash);

			String idvIdType = kycExchangeRequestDTO.getIndividualIdType();
			Optional<PartnerPolicyResponseDTO> policyForPartner = partnerService.getPolicyForPartner(partnerId,	oidcClientId, metadata);
			Optional<PolicyDTO> policyDtoOpt = policyForPartner.map(PartnerPolicyResponseDTO::getPolicy);

			if (!policyDtoOpt.isPresent()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchange",
						"Partner Policy not found: " + partnerId + ", client id: " + oidcClientId);
				throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_FOUND.getErrorCode(),
							IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_FOUND.getErrorMessage());
			}

			List<String> consentAttributes = kycExchangeRequestDTO.getConsentObtained();
			List<String> allowedConsentAttributes = exchangeDataAttributesUtil.filterAllowedUserClaims(oidcClientId, consentAttributes);

			PolicyDTO policyDto = policyDtoOpt.get();
			List<String> policyAllowedKycAttribs = Optional.ofNullable(policyDto.getAllowedKycAttributes()).stream()
						.flatMap(Collection::stream).map(KYCAttributes::getAttributeName).collect(Collectors.toList());

			Set<String> filterAttributes = new HashSet<>();
			exchangeDataAttributesUtil.mapConsentedAttributesToIdSchemaAttributes(allowedConsentAttributes, filterAttributes, policyAllowedKycAttribs);
			Set<String> policyAllowedAttributes = exchangeDataAttributesUtil.filterByPolicyAllowedAttributes(filterAttributes, policyAllowedKycAttribs);

			boolean isBioRequired = false;
			if (filterAttributes.contains(CbeffDocType.FACE.getType().value().toLowerCase()) || 
						filterAttributes.contains(IdAuthCommonConstants.PHOTO.toLowerCase())) {
				policyAllowedAttributes.add(CbeffDocType.FACE.getType().value().toLowerCase());
				isBioRequired = true;
			}

			Map<String, Object> idResDTO = idService.processIdType(idvIdType, idVid, isBioRequired,
					IdAuthCommonConstants.KYC_EXCHANGE_CONSUME_VID_DEFAULT, policyAllowedAttributes);
			Map<String, List<IdentityInfoDTO>> idInfo = IdInfoFetcher.getIdInfo(idResDTO);
			
			String token = idService.getToken(idResDTO);
			String psuToken = kycTokenData.getPsuToken();
			List<String> locales = kycExchangeRequestDTO.getLocales();
			if (locales.size() == 0) {
				locales.add(EnvUtil.getKycExchangeDefaultLanguage());
			}

			String respJson = kycService.buildKycExchangeResponse(psuToken, idInfo, allowedConsentAttributes, locales, idVid, 
														kycExchangeRequestDTO);
			// update kyc token status 
			//KycTokenData kycTokenData = kycTokenDataOpt.get();
			kycTokenData.setKycTokenStatus(KycTokenStatusType.PROCESSED.getStatus());
			kycTokenDataRepo.saveAndFlush(kycTokenData);
			KycExchangeResponseDTO kycExchangeResponseDTO = new KycExchangeResponseDTO();
			kycExchangeResponseDTO.setId(kycExchangeRequestDTO.getId());
			kycExchangeResponseDTO.setTransactionID(kycExchangeRequestDTO.getTransactionID());
			kycExchangeResponseDTO.setVersion(kycExchangeRequestDTO.getVersion());
			kycExchangeResponseDTO.setResponseTime(exchangeDataAttributesUtil.getKycExchangeResponseTime(kycExchangeRequestDTO));

			EncryptedKycRespDTO encryptedKycRespDTO = new EncryptedKycRespDTO();
			encryptedKycRespDTO.setEncryptedKyc(respJson);
			kycExchangeResponseDTO.setResponse(encryptedKycRespDTO);
			saveToTxnTable(kycExchangeRequestDTO, false, true, partnerId, token, kycExchangeResponseDTO, requestWithMetadata);
			auditHelper.audit(AuditModules.KYC_EXCHANGE, AuditEvents.KYC_EXCHANGE_REQUEST_RESPONSE,
					kycExchangeRequestDTO.getTransactionID(),	IdType.getIDTypeOrDefault(kycExchangeRequestDTO.getIndividualIdType()),
					IdAuthCommonConstants.KYC_EXCHANGE_SUCCESS);
			return kycExchangeResponseDTO;
		} catch(IdAuthenticationBusinessException e) {
			auditHelper.audit(AuditModules.KYC_EXCHANGE, AuditEvents.KYC_EXCHANGE_REQUEST_RESPONSE,
							  kycExchangeRequestDTO.getTransactionID(), IdType.getIDTypeOrDefault(kycExchangeRequestDTO.getIndividualIdType()), e);
			throw e;
		}
	}

	// Need to move below duplicate code to common to be used by OTPService and KycExchange.
	private void saveToTxnTable(KycExchangeRequestDTO kycExchangeRequestDTO, boolean isInternal, boolean status, String partnerId, String token, 
				KycExchangeResponseDTO kycExchangeResponseDTO, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException {
		if (token != null) {
			boolean authTokenRequired = !isInternal
					&& EnvUtil.getAuthTokenRequired();
			String authTokenId = authTokenRequired ? tokenIdManager.generateTokenId(token, partnerId) : null;
			saveTxn(kycExchangeRequestDTO, token, authTokenId, status, partnerId, isInternal, kycExchangeResponseDTO, requestWithMetadata);
		}
	}

	private void saveTxn(KycExchangeRequestDTO kycExchangeRequestDTO, String token, String authTokenId,
			boolean status, String partnerId, boolean isInternal, KycExchangeResponseDTO kycExchangeResponseDTO, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException {
		Optional<PartnerDTO> partner = isInternal ? Optional.empty() : partnerService.getPartner(partnerId, kycExchangeRequestDTO.getMetadata());
		AutnTxn authTxn = AuthTransactionBuilder.newInstance()
				.withRequest(kycExchangeRequestDTO)
				.addRequestType(RequestType.KYC_EXCHANGE_REQUEST)
				.withAuthToken(authTokenId)
				.withStatus(status)
				.withToken(token)
				.withPartner(partner)
				.withInternal(isInternal)
				.build(env,uinHashSaltRepo,securityManager);
		fraudEventManager.analyseEvent(authTxn);
		if(requestWithMetadata != null) {
			requestWithMetadata.setMetadata(Map.of(AutnTxn.class.getSimpleName(), authTxn));	
		} else {
			idService.saveAutnTxn(authTxn);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public KycAuthResponseDTOV2 processKycAuthV2(@Nonnull AuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
				String partnerId, String oidcClientId, Map<String, Object>  metadata) throws IdAuthenticationBusinessException {
		boolean status;
		String token = null;
		KycAuthResponseDTOV2 kycAuthResponseDTOV2 = null;
		try {
			Map<String, Object> idResDTO = (Map<String, Object>) metadata.get(IdAuthCommonConstants.IDENTITY_DATA);
			token = idService.getToken(idResDTO);
			Entry<BaseAuthResponseDTO, Boolean> kycAuthResponse = doProcessKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId, 
							oidcClientId, idResDTO, true);
			kycAuthResponseDTOV2 = (KycAuthResponseDTOV2) kycAuthResponse.getKey();
			status = kycAuthResponse.getValue();
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, token, authResponseDTO, kycAuthResponseDTOV2, metadata, true);
			auditHelper.audit(AuditModules.KYC_AUTH_V2, AuditEvents.KYC_REQUEST_RESPONSE_V2,
					kycAuthRequestDTO.getTransactionID(),	IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()),
					"kycAuthentication status : " + status);
			return kycAuthResponseDTOV2;
		} catch (IdAuthenticationBusinessException e) {
			status = false;
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, token, authResponseDTO, kycAuthResponseDTOV2, metadata, true);
			auditHelper.audit(AuditModules.KYC_AUTH_V2, AuditEvents.KYC_REQUEST_RESPONSE_V2,
								kycAuthRequestDTO.getTransactionID(), IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()), e);
			throw e;
		}
	}

	@Override
	public KycExchangeResponseDTO processKycExchangeV2(KycExchangeRequestDTOV2 kycExchangeRequestDTOV2,
			String partnerId, String oidcClientId, Map<String, Object> metadata, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException {
		try {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchangeV2",
					"Processing Kyc Exchange V2 request.");

			String kycAuthToken = kycExchangeRequestDTOV2.getKycToken();
			String idVid = kycExchangeRequestDTOV2.getIndividualId();
			String idvidHash = securityManager.hash(idVid);

			KycTokenData kycTokenData = tokenValidationHelper.findAndValidateIssuedToken(kycAuthToken, oidcClientId,
				kycExchangeRequestDTOV2.getTransactionID(), idvidHash);

			String idvIdType = kycExchangeRequestDTOV2.getIndividualIdType();
			Optional<PartnerPolicyResponseDTO> policyForPartner = partnerService.getPolicyForPartner(partnerId,	oidcClientId, metadata);
			Optional<PolicyDTO> policyDtoOpt = policyForPartner.map(PartnerPolicyResponseDTO::getPolicy);

			if (!policyDtoOpt.isPresent()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchangeV2",
						"Partner Policy not found: " + partnerId + ", client id: " + oidcClientId);
				throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_FOUND.getErrorCode(),
							IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_FOUND.getErrorMessage());
			}

			List<String> unVerifiedConsentClaims = kycExchangeRequestDTOV2.getUnVerifiedConsentedClaims()
																	.keySet().stream().collect(Collectors.toList());
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchangeV2",
												"UnVerifiedConsentClaims List:" + unVerifiedConsentClaims);
			List<String> verifiedConsentClaims = exchangeDataAttributesUtil.getVerifiedClaimsList(
														kycExchangeRequestDTOV2.getVerifiedClaims());

			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchangeV2",
												"VerifiedConsentClaims List:" + verifiedConsentClaims);
			boolean duplicateExists = verifiedConsentClaims.stream().anyMatch(claim -> unVerifiedConsentClaims
								.stream().anyMatch(claim2 -> claim2.equalsIgnoreCase(claim)));
			if (duplicateExists){
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchangeV2",
						"Duplicate claims found in both verified & unverified claims.");
				throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.DUPLICATE_CLAIMS_FOUND.getErrorCode(),
							IdAuthenticationErrorConstants.DUPLICATE_CLAIMS_FOUND.getErrorMessage());
			}

			List<String> consentAttributes = new ArrayList<>(unVerifiedConsentClaims);
			consentAttributes.addAll(verifiedConsentClaims);

			List<String> allowedConsentAttributes = exchangeDataAttributesUtil.filterAllowedUserClaims(oidcClientId, consentAttributes);

			PolicyDTO policyDto = policyDtoOpt.get();

			List<String> policyAllowedKycAttribs = Optional.ofNullable(policyDto.getAllowedKycAttributes()).stream()
									.flatMap(Collection::stream).map(KYCAttributes::getAttributeName).collect(Collectors.toList());

			Set<String> filterAttributes = new HashSet<>();
			exchangeDataAttributesUtil.mapConsentedAttributesToIdSchemaAttributes(allowedConsentAttributes, filterAttributes, policyAllowedKycAttribs);
			Set<String> policyAllowedAttributes = exchangeDataAttributesUtil.filterByPolicyAllowedAttributes(filterAttributes, policyAllowedKycAttribs);

			boolean isBioRequired = false;
			if (filterAttributes.contains(CbeffDocType.FACE.getType().value().toLowerCase()) ||
						filterAttributes.contains(IdAuthCommonConstants.PHOTO.toLowerCase())) {
				policyAllowedAttributes.add(CbeffDocType.FACE.getType().value().toLowerCase());
				isBioRequired = true;
			}

			policyAllowedAttributes.add(IdaIdMapping.VERIFIEDATTRIBUTES.getIdname());
			Map<String, Object> idResDTO = idService.processIdType(idvIdType, idVid, isBioRequired,
								IdAuthCommonConstants.KYC_EXCHANGE_CONSUME_VID_DEFAULT, policyAllowedAttributes);
			Map<String, List<IdentityInfoDTO>> idInfo = IdInfoFetcher.getIdInfo(idResDTO, mapper);

			List<String> locales = kycExchangeRequestDTOV2.getLocales();
			if (locales.size() == 0) {
				locales.add(EnvUtil.getKycExchangeDefaultLanguage());
			}

			String token = idService.getToken(idResDTO);
			String psuToken = kycTokenData.getPsuToken();

			String respJson = kycService.buildExchangeVerifiedClaimsData(idvidHash, idInfo, unVerifiedConsentClaims,
						verifiedConsentClaims, locales, idVid, kycExchangeRequestDTOV2);
			// update kyc token status
			//KycTokenData kycTokenData = kycTokenDataOpt.get();
			kycTokenData.setKycTokenStatus(KycTokenStatusType.PROCESSED.getStatus());
			kycTokenDataRepo.saveAndFlush(kycTokenData);
			KycExchangeResponseDTO kycExchangeResponseDTO = new KycExchangeResponseDTO();
			kycExchangeResponseDTO.setId(kycExchangeRequestDTOV2.getId());
			kycExchangeResponseDTO.setTransactionID(kycExchangeRequestDTOV2.getTransactionID());
			kycExchangeResponseDTO.setVersion(kycExchangeRequestDTOV2.getVersion());
			kycExchangeResponseDTO.setResponseTime(exchangeDataAttributesUtil.getKycExchangeResponseTime(kycExchangeRequestDTOV2));

			EncryptedKycRespDTO encryptedKycRespDTO = new EncryptedKycRespDTO();
			encryptedKycRespDTO.setEncryptedKyc(respJson);
			kycExchangeResponseDTO.setResponse(encryptedKycRespDTO);
			/* saveToTxnTable(kycExchangeRequestDTOV2, false, true, partnerId, token, kycExchangeResponseDTO, requestWithMetadata);
			auditHelper.audit(AuditModules.KYC_EXCHANGE, AuditEvents.KYC_EXCHANGE_REQUEST_RESPONSE,
					kycExchangeRequestDTO.getTransactionID(),	IdType.getIDTypeOrDefault(kycExchangeRequestDTO.getIndividualIdType()),
					IdAuthCommonConstants.KYC_EXCHANGE_SUCCESS); */
			return kycExchangeResponseDTO;
		} catch(IdAuthenticationBusinessException e) {
			auditHelper.audit(AuditModules.KYC_EXCHANGE_V2, AuditEvents.KYC_EXCHANGE_REQUEST_RESPONSE_V2,
				kycExchangeRequestDTOV2.getTransactionID(),
				IdType.getIDTypeOrDefault(kycExchangeRequestDTOV2.getIndividualIdType()), e);
			throw e;
		}
	}
	
}
