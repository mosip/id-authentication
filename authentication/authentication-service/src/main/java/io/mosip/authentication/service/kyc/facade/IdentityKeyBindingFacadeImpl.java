package io.mosip.authentication.service.kyc.facade;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.BaseAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRespDto;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingResponseDto;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.facade.IdentityKeyBindingFacade;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.IdentityKeyBindingService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.kernel.core.logger.spi.Logger;

import javax.validation.constraints.NotNull;

/**
 *
 * Facade for Identity Key Binding
 * 
 * @author Mahammed Taheer
 */
@Component
public class IdentityKeyBindingFacadeImpl implements IdentityKeyBindingFacade {
    
    /** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdentityKeyBindingFacadeImpl.class);

    /** The Id Info Service */
	@Autowired
	private IdService<AutnTxn> idService;

    @Autowired
	private AuthFacade authFacade;

    @Autowired
	private IdentityKeyBindingService keyBindingService;

    @Autowired
	private IdaUinHashSaltRepo uinHashSaltRepo;

	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;

    /** The env. */
	@Autowired
	private EnvUtil env;

	@Autowired
	private IdAuthSecurityManager securityManager;

    @Autowired
	private PartnerService partnerService;
	
	@Autowired
	private IdAuthFraudAnalysisEventManager fraudEventManager;	

    @Autowired
	private AuditHelper auditHelper;

    /*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.KycFacade#
	 * authenticateIndividual(io.mosip.authentication.core.indauth.dto.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, String partnerId, 
					String partnerApiKey, ObjectWithMetadata requestWithMetadata) 
                    throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		String idvid = authRequest.getIndividualId();
		String idvIdType = IdType.getIDTypeStrOrDefault(authRequest.getIndividualIdType());
		// First check whether Id is Perpetual VID or UIN. 
		// For VIDs with transaction limit key binding will not be allowed.
		idService.checkIdKeyBindingPermitted(idvid, idvIdType);

        boolean keyBinded = keyBindingService.isPublicKeyBinded(idvid, 
                            ((IdentityKeyBindingRequestDTO) authRequest).getIdentityKeyBinding().getPublicKeyJWK());
        if (keyBinded) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
						"Public key already binded to an VID.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PUBLIC_KEY_BINDING_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.PUBLIC_KEY_BINDING_NOT_ALLOWED.getErrorMessage()));
        }
		
		return authFacade.authenticateIndividual(authRequest, true, partnerId, partnerApiKey, 
				IdAuthCommonConstants.KEY_BINDING_CONSUME_VID_DEFAULT, requestWithMetadata);
	}

    @SuppressWarnings("unchecked")
	@Override
	public IdentityKeyBindingResponseDto processIdentityKeyBinding(@NotNull IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO,
					AuthResponseDTO authResponseDTO, String partnerId, String oidcClientId, Map<String, Object>  metadata) 
					throws IdAuthenticationBusinessException {
		boolean status;
		String token = null;
		String idHash = null;
		IdentityKeyBindingResponseDto keyBindingResponseDto = null;
		try {
			Map<String, Object> idResDTO = (Map<String, Object>) metadata.get(IdAuthCommonConstants.IDENTITY_DATA);
			token = idService.getToken(idResDTO);
			idHash = idService.getIdHash(idResDTO);
            Map<String, List<IdentityInfoDTO>> idInfo = IdInfoFetcher.getIdInfo(idResDTO);

			Entry<IdentityKeyBindingResponseDto, Boolean> keyBindingResponseEntry = doProcessIdKeyBinding(identityKeyBindingRequestDTO, 
                            authResponseDTO, partnerId, oidcClientId, token, idHash, idInfo);
            keyBindingResponseDto = keyBindingResponseEntry.getKey();
			status = keyBindingResponseEntry.getValue();
			saveToTxnTable(identityKeyBindingRequestDTO, status, partnerId, token, authResponseDTO, keyBindingResponseDto, metadata);
			auditHelper.audit(AuditModules.IDENTITY_KEY_BINDING, AuditEvents.KEY_BINDIN_REQUEST_RESPONSE,
					idHash,	IdType.getIDTypeOrDefault(identityKeyBindingRequestDTO.getIndividualIdType()),
					"Identity Key Binding status : " + status);
			return keyBindingResponseDto;
		} catch (IdAuthenticationBusinessException e) {
			status = false;
			saveToTxnTable(identityKeyBindingRequestDTO, status, partnerId, token, authResponseDTO, keyBindingResponseDto, metadata);
			auditHelper.audit(AuditModules.IDENTITY_KEY_BINDING, AuditEvents.KEY_BINDIN_REQUEST_RESPONSE,
								idHash, IdType.getIDTypeOrDefault(identityKeyBindingRequestDTO.getIndividualIdType()), e);
			throw e;
		}
	}

	private Entry<IdentityKeyBindingResponseDto, Boolean> doProcessIdKeyBinding(IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO, 
                AuthResponseDTO authResponseDTO, String partnerId, String oidcClientId, String token, 
                String idHash, Map<String, List<IdentityInfoDTO>> idInfo) throws IdAuthenticationBusinessException, IDDataValidationException {

		IdentityKeyBindingResponseDto keyBindingResponseDTO = new IdentityKeyBindingResponseDto();

		if (identityKeyBindingRequestDTO != null) {

			IdentityKeyBindingRespDto response = new IdentityKeyBindingRespDto();
			ResponseDTO authResponse = authResponseDTO.getResponse();
			String responseTime = authResponseDTO.getResponseTime();
			if(Objects.isNull(responseTime)) {
				responseTime = getAuthResponseTime(identityKeyBindingRequestDTO);
			}

			String certificateData = null;
			if (Objects.nonNull(authResponse) && authResponse.isAuthStatus()) {
				certificateData = keyBindingService.createAndSaveKeyBindingCertificate(identityKeyBindingRequestDTO, idInfo, token, partnerId);
				response.setIdentityCertificate(certificateData);
			}
			if (Objects.nonNull(authResponse) && Objects.nonNull(authResponseDTO)) {
				response.setBindingAuthStatus(authResponse.isAuthStatus());
				response.setAuthToken(authResponse.getAuthToken());
				keyBindingResponseDTO.setResponse(response);
				keyBindingResponseDTO.setId(authResponseDTO.getId());
				keyBindingResponseDTO.setTransactionID(authResponseDTO.getTransactionID());
				keyBindingResponseDTO.setVersion(authResponseDTO.getVersion());
				keyBindingResponseDTO.setErrors(authResponseDTO.getErrors());
				keyBindingResponseDTO.setResponseTime(responseTime);
			}

			return new SimpleEntry<>(keyBindingResponseDTO, response.isBindingAuthStatus());
		}
		return new SimpleEntry<>(keyBindingResponseDTO, false);
	}

    // Duplicate Code..
    private String getAuthResponseTime(AuthRequestDTO kycAuthRequestDTO) {
		String dateTimePattern = EnvUtil.getDateTimePattern();
		return IdaRequestResponsConsumerUtil.getResponseTime(kycAuthRequestDTO.getRequestTime(), dateTimePattern);
	}

    private void saveToTxnTable(AuthRequestDTO authRequestDTO, boolean status, String partnerId, String token, 
			AuthResponseDTO authResponseDTO, BaseAuthResponseDTO baseAuthResponseDTO, Map<String, Object> metadata)
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
							autnTxn.setAuthTypeCode(RequestType.IDENTITY_KEY_BINDING.getRequestType()
								+ (authTypeCode == null ? "" : AuthTransactionBuilder.REQ_TYPE_DELIM + authTypeCode));
							autnTxn.setStatusComment(RequestType.IDENTITY_KEY_BINDING.getMessage() + (statusComment == null ? ""
								: AuthTransactionBuilder.REQ_TYPE_MSG_DELIM + statusComment));
					}
					metadata.put(AutnTxn.class.getSimpleName(), autnTxn);
				}
			} else {
				AutnTxn authTxn = AuthTransactionBuilder.newInstance().withRequest(authRequestDTO)
						.addRequestType(RequestType.IDENTITY_KEY_BINDING).withAuthToken(authTokenId).withStatus(status)
						.withInternal(false).withPartner(partner).withToken(token)
						.build(env, uinHashSaltRepo, securityManager);
				fraudEventManager.analyseEvent(authTxn);
				idService.saveAutnTxn(authTxn);
			}
		}
	}
}
