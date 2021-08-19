package io.mosip.authentication.kyc.service.controller;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.kyc.service.facade.KycFacadeImpl;
import io.mosip.authentication.kyc.service.validator.KycAuthRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the authentication requests.
 *
 * @author Arun Bose
 * @author Prem Kumar
 * @author Nagarjuna K
 */
@RestController
public class KycAuthController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(KycAuthController.class);

	/** The KycAuthRequestValidator */
	@Autowired
	private KycAuthRequestValidator kycReqValidator;

	/** The auth facade. */
	@Autowired
	private KycFacadeImpl kycFacade;
	
	@Autowired
	private AuditHelper auditHelper;
	
	@Autowired
	private IdTypeUtil idTypeUtil;
	
	@Autowired
	private AuthTransactionHelper authTransactionHelper;
	
	@Autowired
	private PartnerService partnerService;

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("kycAuthRequestDTO")
	private void initKycBinder(WebDataBinder binder) {
		binder.addValidators(kycReqValidator);
	}

	/**
	 * Controller Method to auhtentication for eKyc-Details.
	 *
	 * @param kycAuthRequestDTO the kyc auth request DTO
	 * @param errors            the errors
	 * @return kycAuthResponseDTO the kyc auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PostMapping(path = "/{MISP-LK}/{eKYC-Partner-ID}/{API-Key}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "eKyc Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public KycAuthResponseDTO processKyc(@Validated @RequestBody KycAuthRequestDTO kycAuthRequestDTO,
			@ApiIgnore Errors errors, @PathVariable("MISP-LK") String mispLK,@PathVariable("eKYC-Partner-ID") String partnerId,
			@PathVariable("API-Key") String partnerApiKey)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		boolean isAuth = true;
		Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, kycAuthRequestDTO.getMetadata());
		AuthTransactionBuilder authTxnBuilder = authTransactionHelper
				.createAndSetAuthTxnBuilderMetadataToRequest(kycAuthRequestDTO, !isAuth, partner);
		
		try {
			String idType = Objects.nonNull(kycAuthRequestDTO.getIndividualIdType()) ? kycAuthRequestDTO.getIndividualIdType()
					: idTypeUtil.getIdType(kycAuthRequestDTO.getIndividualId()).getType();
			kycAuthRequestDTO.setIndividualIdType(idType);
			kycReqValidator.validateIdvId(kycAuthRequestDTO.getIndividualId(), idType, errors);
			if(AuthTypeUtil.isBio(kycAuthRequestDTO)) {
				kycReqValidator.validateDeviceDetails(kycAuthRequestDTO, errors);
			}
			DataValidationUtil.validate(errors);
			
			AuthResponseDTO authResponseDTO = kycFacade.authenticateIndividual(kycAuthRequestDTO, true, partnerId, partnerApiKey);
			KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
			if (authResponseDTO != null && 
					authResponseDTO.getMetadata() != null && 
					authResponseDTO.getMetadata().get(IdAuthCommonConstants.IDENTITY_DATA) != null) {
				kycAuthResponseDTO = kycFacade.processKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId);
			}
			return kycAuthResponseDTO;
		} catch (IDDataValidationException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKyc",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			
			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.EKYC_REQUEST_RESPONSE, kycAuthRequestDTO, e);
			
			throw authTransactionHelper.createDataValidationException(authTxnBuilder, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKyc",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			
			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.EKYC_REQUEST_RESPONSE, kycAuthRequestDTO, e);
			
			throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e);
		}
	}

}
