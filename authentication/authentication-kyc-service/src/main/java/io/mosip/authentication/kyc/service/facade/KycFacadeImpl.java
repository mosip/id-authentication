/**
 * 
 */
package io.mosip.authentication.kyc.service.facade;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
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
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.kernel.core.util.DateUtils;

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
	private UinEncryptSaltRepo uinEncryptSaltRepo;

	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;

	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private PartnerService partnerService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.KycFacade#
	 * authenticateIndividual(io.mosip.authentication.core.indauth.dto.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request, String partnerId)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		return authFacade.authenticateIndividual(authRequest, request, partnerId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.facade.KycFacade#processKycAuth(io.
	 * mosip.authentication.core.indauth.dto.KycAuthRequestDTO,
	 * io.mosip.authentication.core.indauth.dto.AuthResponseDTO, java.lang.String)
	 */
	@Override
	public KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId) throws IdAuthenticationBusinessException {
		boolean status;
		String uin = null;
		try {
			String idvId = kycAuthRequestDTO.getIndividualId();
			String idvIdtype = IdType.getIDTypeStrOrDefault(kycAuthRequestDTO.getIndividualIdType());

			Map<String, Object> idResDTO = idService.processIdType(idvIdtype, idvId, true);
			uin = idService.getUin(idResDTO);

			KycAuthResponseDTO kycAuthResponseDTO = doProcessKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId,
					idResDTO, uin);
			status = kycAuthResponseDTO.getResponse().isKycStatus();
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, uin);
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.EKYC_REQUEST_RESPONSE,
					kycAuthRequestDTO.getIndividualId(),
					IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()),
					"kycAuthentication status : " + status);
			return kycAuthResponseDTO;
		} catch (IdAuthenticationBusinessException e) {
			status = false;
			saveToTxnTable(kycAuthRequestDTO, status, partnerId, uin);
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.EKYC_REQUEST_RESPONSE,
					kycAuthRequestDTO.getIndividualId(),
					IdType.getIDTypeOrDefault(kycAuthRequestDTO.getIndividualIdType()), e);
			throw e;
		}
	}

	private void saveToTxnTable(KycAuthRequestDTO kycAuthRequestDTO, boolean status, String partnerId, String uin)
			throws IdAuthenticationBusinessException {
		if (uin != null) {
			Boolean staticTokenRequired = env.getProperty(IdAuthConfigKeyConstants.RESPONSE_TOKEN_ENABLE, Boolean.class);
			String staticTokenId = staticTokenRequired ? tokenIdManager.generateTokenId(uin, partnerId) : null;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId);

			AutnTxn authTxn = AuthTransactionBuilder.newInstance().withAuthRequest(kycAuthRequestDTO)
					.withRequestType(RequestType.KYC_AUTH_REQUEST).withResponseToken(staticTokenId).withStatus(status)
					.withInternal(false).withPartner(partner).withUin(uin)
					.build(env, uinEncryptSaltRepo, uinHashSaltRepo, securityManager);
			idService.saveAutnTxn(authTxn);
		}
	}

	private KycAuthResponseDTO doProcessKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId, Map<String, Object> idResDTO, String uin)
			throws IdAuthenticationBusinessException, IDDataValidationException {
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();

		String resTime = null;
		if (kycAuthRequestDTO != null) {

			String dateTimePattern = env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN);

			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(kycAuthRequestDTO.getRequestTime(), isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));

			Map<String, List<IdentityInfoDTO>> idInfo = idService.getIdInfo(idResDTO);
			KycResponseDTO response = new KycResponseDTO();
			ResponseDTO authResponse = authResponseDTO.getResponse();

			if (Objects.nonNull(idResDTO) && Objects.nonNull(authResponse) && authResponse.isAuthStatus()) {
				response = kycService.retrieveKycInfo(String.valueOf(uin), kycAuthRequestDTO.getAllowedKycAttributes(),
						kycAuthRequestDTO.getSecondaryLangCode(), idInfo);
			}
			if (Objects.nonNull(authResponse) && Objects.nonNull(authResponseDTO)) {
				response.setKycStatus(authResponse.isAuthStatus());
				response.setResponseToken(authResponse.getResponseToken());
				kycAuthResponseDTO.setResponse(response);
				kycAuthResponseDTO.setId(authResponseDTO.getId());
				kycAuthResponseDTO.setTransactionID(authResponseDTO.getTransactionID());
				kycAuthResponseDTO.setVersion(authResponseDTO.getVersion());
				kycAuthResponseDTO.setErrors(authResponseDTO.getErrors());
				kycAuthResponseDTO.setResponseTime(resTime);
			}

		}
		return kycAuthResponseDTO;
	}

}
