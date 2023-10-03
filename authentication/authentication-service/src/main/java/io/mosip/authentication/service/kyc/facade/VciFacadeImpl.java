package io.mosip.authentication.service.kyc.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.KycTokenData;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.TokenValidationHelper;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.KycTokenDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.KycTokenStatusType;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.VCResponseDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.KYCAttributes;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.VciFacade;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.service.kyc.impl.VciServiceImpl;
import io.mosip.authentication.service.kyc.util.ExchangeDataAttributesUtil;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 *
 * Facade to Verifiable Credential details
 * 
 * @author Dinesh Karuppiah.T
 */
@Component
public class VciFacadeImpl implements VciFacade {

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(VciFacadeImpl.class);
	
	/** The env. */
	@Autowired
	private EnvUtil env;

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
	private VciServiceImpl vciServiceImpl;

	@Autowired
	private TokenValidationHelper tokenValidationHelper;

	@Autowired
	private KycTokenDataRepository kycTokenDataRepo;

	@Autowired
	private ExchangeDataAttributesUtil exchangeDataAttributesUtil;

	@Override
	public VciExchangeResponseDTO processVciExchange(VciExchangeRequestDTO vciExchangeRequestDTO, String partnerId, 
			String oidcClientId, Map<String, Object>  metadata, ObjectWithMetadata requestWithMetadata) throws IdAuthenticationBusinessException {
		String idvidHash = null;
		try {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processVciExchange",
					"Processing VCI Exchange request.");
			
			String vciAuthToken = vciExchangeRequestDTO.getVcAuthToken();
			String idVid = vciExchangeRequestDTO.getIndividualId();
			idvidHash = securityManager.hash(idVid);

			KycTokenData kycTokenData = tokenValidationHelper.findAndValidateIssuedToken(vciAuthToken, oidcClientId, 
						vciExchangeRequestDTO.getTransactionID(), idvidHash);
			
			String idvIdType = vciExchangeRequestDTO.getIndividualIdType();
			Optional<PartnerPolicyResponseDTO> policyForPartner = partnerService.getPolicyForPartner(partnerId,	oidcClientId, metadata);
			Optional<PolicyDTO> policyDtoOpt = policyForPartner.map(PartnerPolicyResponseDTO::getPolicy);

			if (!policyDtoOpt.isPresent()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchange",
						"Partner Policy not found: " + partnerId + ", client id: " + oidcClientId);
				throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_FOUND.getErrorCode(),
							IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_FOUND.getErrorMessage());
			}

			// Will implement later the consent claims based on credential definition input
			List<String> consentAttributes = Collections.emptyList();  
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
					IdAuthCommonConstants.VCI_EXCHANGE_CONSUME_VID_DEFAULT, policyAllowedAttributes);

			String token = idService.getToken(idResDTO);
			
			vciServiceImpl.addCredSubjectId(vciExchangeRequestDTO.getCredSubjectId(), idvidHash, token, oidcClientId);
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processVciExchange",
					"Added Credential Subject Id complete.");
					
			Map<String, List<IdentityInfoDTO>> idInfo = IdInfoFetcher.getIdInfo(idResDTO);
			
			String psuToken = kycTokenData.getPsuToken();
			List<String> locales = vciExchangeRequestDTO.getLocales();
			if (Objects.isNull(locales) || locales.size() == 0) {
				locales = new ArrayList<>(); // throws NullPointer if locales is null
				locales.add(EnvUtil.getKycExchangeDefaultLanguage());
			}

			VCResponseDTO<?> vcResponseDTO = vciServiceImpl.buildVerifiableCredentials(vciExchangeRequestDTO.getCredSubjectId(), vciExchangeRequestDTO.getVcFormat(), 
						idInfo, locales, policyAllowedAttributes, vciExchangeRequestDTO, psuToken);
			
			// update kyc token status 
			kycTokenData.setKycTokenStatus(KycTokenStatusType.PROCESSED.getStatus());
			kycTokenDataRepo.saveAndFlush(kycTokenData);
			VciExchangeResponseDTO vciExchangeResponseDTO = new VciExchangeResponseDTO();
			vciExchangeResponseDTO.setId(vciExchangeRequestDTO.getId());
			vciExchangeResponseDTO.setTransactionID(vciExchangeRequestDTO.getTransactionID());
			vciExchangeResponseDTO.setVersion(vciExchangeRequestDTO.getVersion());
			vciExchangeResponseDTO.setResponseTime(exchangeDataAttributesUtil.getKycExchangeResponseTime(vciExchangeRequestDTO));
			vciExchangeResponseDTO.setResponse(vcResponseDTO);
			saveToTxnTable(vciExchangeRequestDTO, false, true, partnerId, token, vciExchangeResponseDTO, requestWithMetadata);
			auditHelper.audit(AuditModules.VCI_EXCHANGE, AuditEvents.VCI_EXCHANGE_REQUEST_RESPONSE,
					idvidHash,	IdType.getIDTypeOrDefault(vciExchangeRequestDTO.getIndividualIdType()),
					IdAuthCommonConstants.VCI_EXCHANGE_SUCCESS);
			return vciExchangeResponseDTO; 
		} catch(IdAuthenticationBusinessException e) {
			auditHelper.audit(AuditModules.VCI_EXCHANGE, AuditEvents.VCI_EXCHANGE_REQUEST_RESPONSE,
							  idvidHash, IdType.getIDTypeOrDefault(vciExchangeRequestDTO.getIndividualIdType()), e); 
			throw e;
		}
	}

	// Need to move below duplicate code to common to be used by OTPService and KycExchange.
	private void saveToTxnTable(VciExchangeRequestDTO vciExchangeRequestDTO, boolean isInternal, boolean status, String partnerId, String token, 
				VciExchangeResponseDTO vciExchangeResponseDTO, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException {
		if (token != null) {
			boolean authTokenRequired = !isInternal
					&& EnvUtil.getAuthTokenRequired();
			String authTokenId = authTokenRequired ? tokenIdManager.generateTokenId(token, partnerId) : null;
			saveTxn(vciExchangeRequestDTO, token, authTokenId, status, partnerId, isInternal, vciExchangeResponseDTO, requestWithMetadata);
		}
	}

	private void saveTxn(VciExchangeRequestDTO vciExchangeRequestDTO, String token, String authTokenId,
			boolean status, String partnerId, boolean isInternal, VciExchangeResponseDTO vciExchangeResponseDTO, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException {
		Optional<PartnerDTO> partner = isInternal ? Optional.empty() : partnerService.getPartner(partnerId, vciExchangeRequestDTO.getMetadata());
		AutnTxn authTxn = AuthTransactionBuilder.newInstance()
				.withRequest(vciExchangeRequestDTO)
				.addRequestType(RequestType.VCI_EXCHANGE_REQUEST)
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
}
