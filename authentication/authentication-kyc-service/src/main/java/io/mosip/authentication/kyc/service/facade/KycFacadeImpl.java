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

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
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
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.facade.KycFacade;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.kernel.core.util.DateUtils;

/**
 * @author Dinesh Karuppiah.T
 *
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
	private IdService<AutnTxn> idInfoService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	@Autowired
	private IdService<AutnTxn> idAuthService;

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
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		Map<String, Object> idResDTO = null;
		String resTime = null;
		IdType idType = null;
		if (kycAuthRequestDTO != null) {
			String idvId = null;
			Optional<String> idvIdOptional = idInfoFetcher.getUinOrVid(kycAuthRequestDTO);
			if (idvIdOptional.isPresent()) {
				idvId = idvIdOptional.get();
			}
			String idvIdtype = idInfoFetcher.getUinOrVidType(kycAuthRequestDTO).getType();
			idResDTO = idAuthService.processIdType(idvIdtype, idvId, true);

			if (idvIdtype.equals(IdType.UIN.getType())) {
				idType = IdType.UIN;
			} else {
				idType = IdType.VID;
			}
			String dateTimePattern = env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN);

			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(kycAuthRequestDTO.getRequestTime(), isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					kycAuthRequestDTO.getIndividualId(), idType, AuditModules.EKYC_AUTH.getDesc());
		}
		Map<String, List<IdentityInfoDTO>> idInfo = idInfoService.getIdInfo(idResDTO);
		KycResponseDTO response = new KycResponseDTO();
		ResponseDTO authResponse = authResponseDTO.getResponse();
		if (Objects.nonNull(idResDTO) && Objects.nonNull(authResponse) && authResponse.isAuthStatus()) {
			response = kycService.retrieveKycInfo(String.valueOf(idResDTO.get("uin")),
					kycAuthRequestDTO.getAllowedKycAttributes(), kycAuthRequestDTO.getSecondaryLangCode(), idInfo);
			response.setTtl(env.getProperty(IdAuthConfigKeyConstants.EKYC_TTL_HOURS));
		}
		if (Objects.nonNull(authResponse) && Objects.nonNull(authResponseDTO)) {
			response.setKycStatus(authResponse.isAuthStatus());
			response.setStaticToken(authResponse.getStaticToken());
			kycAuthResponseDTO.setResponse(response);
			kycAuthResponseDTO.setId(authResponseDTO.getId());
			kycAuthResponseDTO.setTransactionID(authResponseDTO.getTransactionID());
			kycAuthResponseDTO.setVersion(authResponseDTO.getVersion());
			kycAuthResponseDTO.setErrors(authResponseDTO.getErrors());
			kycAuthResponseDTO.setResponseTime(resTime);
		}
		return kycAuthResponseDTO;
	}

}
