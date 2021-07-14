/**
 * 
 */
package io.mosip.authentication.kyc.service.facade;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.facade.KycFacade;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.kernel.core.util.DateUtils;
import reactor.util.function.Tuple2;

/**
 * 
 *
 * Facade to authentication KYC details
 * 
 * @author Dinesh Karuppiah.T
 */
@Component
public class KycFacadeImpl implements KycFacade {

	/** The env. */
	@Autowired
	Environment env;

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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.KycFacade#
	 * authenticateIndividual(io.mosip.authentication.core.indauth.dto.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request, String partnerId, String partnerApiKey)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		return authFacade.authenticateIndividual(authRequest, request, partnerId, partnerApiKey, IdAuthCommonConstants.CONSUME_VID_DEFAULT);

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
	public KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId) throws IdAuthenticationBusinessException {
		boolean status;
		String token = null;
		KycAuthResponseDTO kycAuthResponseDTO = null;
		try {
			Map<String, Object> idResDTO = (Map<String, Object>) authResponseDTO.getMetadata().get(IdAuthCommonConstants.IDENTITY_DATA);
			token = idService.getToken(idResDTO);

			Entry<KycAuthResponseDTO, Boolean> kycAuthResponse = doProcessKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId,
					idResDTO, token);
			kycAuthResponseDTO = kycAuthResponse.getKey();
			status = kycAuthResponse.getValue();
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, token, authResponseDTO, kycAuthResponseDTO);
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.EKYC_REQUEST_RESPONSE,
					kycAuthRequestDTO.getIndividualId(),
					IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()),
					"kycAuthentication status : " + status);
			return kycAuthResponseDTO;
		} catch (IdAuthenticationBusinessException e) {
			status = false;
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, token, authResponseDTO, kycAuthResponseDTO);
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.EKYC_REQUEST_RESPONSE,
					kycAuthRequestDTO.getIndividualId(),
					IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()), e);
			throw e;
		}
	}

	private void saveToTxnTable(KycAuthRequestDTO kycAuthRequestDTO, boolean status, String partnerId, String token, AuthResponseDTO authResponseDTO, KycAuthResponseDTO kycAuthResponseDTO)
			throws IdAuthenticationBusinessException {
		if (token != null) {
			Boolean authTokenRequired = env.getProperty(IdAuthConfigKeyConstants.RESPONSE_TOKEN_ENABLE, Boolean.class);
			String authTokenId = authTokenRequired ? tokenIdManager.generateTokenId(token, partnerId) : null;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, kycAuthRequestDTO.getMetadata());

			if(kycAuthResponseDTO != null && authResponseDTO != null) {
				Object authTxnObj = authResponseDTO.getMetadata().get(AutnTxn.class.getSimpleName());
				if(authTxnObj instanceof AutnTxn) {
					AutnTxn autnTxn = (AutnTxn) authTxnObj;
					autnTxn.setAuthTypeCode(RequestType.KYC_AUTH_REQUEST.getRequestType() +
							AuthTransactionBuilder.REQ_TYPE_DELIM +  autnTxn.getAuthTypeCode());
					autnTxn.setStatusComment(RequestType.KYC_AUTH_REQUEST.getMessage() +
							AuthTransactionBuilder.REQ_TYPE_MSG_DELIM +  autnTxn.getStatusComment());

					kycAuthResponseDTO.setMetadata(Map.of(AutnTxn.class.getSimpleName(), autnTxn));
				}
			} else {
				AutnTxn authTxn = AuthTransactionBuilder.newInstance().withRequest(kycAuthRequestDTO)
						.addRequestType(RequestType.KYC_AUTH_REQUEST).withAuthToken(authTokenId).withStatus(status)
						.withInternal(false).withPartner(partner).withToken(token)
						.build(env, uinHashSaltRepo, securityManager);
				fraudEventManager.analyseEvent(authTxn);
				idService.saveAutnTxn(authTxn);
			}
		}
	}

	private Entry<KycAuthResponseDTO, Boolean> doProcessKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId, Map<String, Object> idResDTO, String token)
			throws IdAuthenticationBusinessException, IDDataValidationException {
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();

		String resTime = null;
		if (kycAuthRequestDTO != null) {

			String dateTimePattern = env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN);

			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(kycAuthRequestDTO.getRequestTime(), isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));

			Map<String, List<IdentityInfoDTO>> idInfo = IdInfoFetcher.getIdInfo(idResDTO);
			KycResponseDTO response = new KycResponseDTO();
			ResponseDTO authResponse = authResponseDTO.getResponse();	
			Set<String> langCodes = new HashSet<>(
					Arrays.asList(kycAuthRequestDTO.getMetadata().get(IdAuthCommonConstants.KYC_LANGUAGES).toString()));			
			if (Objects.nonNull(idResDTO) && Objects.nonNull(authResponse) && authResponse.isAuthStatus()) {
				response = kycService.retrieveKycInfo(kycAuthRequestDTO.getAllowedKycAttributes(),
						langCodes, idInfo);
			}
			if (Objects.nonNull(authResponse) && Objects.nonNull(authResponseDTO)) {
				response.setKycStatus(authResponse.isAuthStatus());
				response.setAuthToken(authResponse.getAuthToken());
				
				if(Objects.nonNull(response.getIdentity())) {
					String partnerCertificate = (String) kycAuthRequestDTO.getMetadata().get(IdAuthCommonConstants.PARTNER_CERTIFICATE);
					Tuple2<String, String> encryptKycResponse = encryptKycResponse(response.getIdentity(),partnerCertificate);
					response.setIdentity(encryptKycResponse.getT1());
					response.setThumbprint(encryptKycResponse.getT2());
				}
				
				kycAuthResponseDTO.setResponse(response);
				kycAuthResponseDTO.setId(authResponseDTO.getId());
				kycAuthResponseDTO.setTransactionID(authResponseDTO.getTransactionID());
				kycAuthResponseDTO.setVersion(authResponseDTO.getVersion());
				kycAuthResponseDTO.setErrors(authResponseDTO.getErrors());
				kycAuthResponseDTO.setResponseTime(resTime);
			}

			return new SimpleEntry<>(kycAuthResponseDTO, response.isKycStatus());
		}
		return new SimpleEntry<>(kycAuthResponseDTO, false);
	}

	private Tuple2<String, String> encryptKycResponse(String identity, String partnerCertificate) throws IdAuthenticationBusinessException {
		try {
			return securityManager.encryptData(identity.getBytes(), partnerCertificate);
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

}
