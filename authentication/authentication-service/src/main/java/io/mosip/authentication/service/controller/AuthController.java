package io.mosip.authentication.service.controller;

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
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.IdTypeUtil;
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
public class AuthController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(AuthController.class);

	/** The auth request validator. */
	@Autowired
	private AuthRequestValidator authRequestValidator;

	/** The auth facade. */
	@Autowired
	private AuthFacade authFacade;
	
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
	@InitBinder("authRequestDTO")
	private void initAuthRequestBinder(WebDataBinder binder) {
		binder.setValidator(authRequestValidator);
	}

	/**
	 * authenticateRequest - method to authenticate request.
	 *
	 * @param authrequestdto - Authenticate Request
	 * @param errors         the errors
	 * @return authResponsedto AuthResponseDTO
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 * @throws IdAuthenticationBusinessException
	 */
	@PostMapping(path = "/{MISP-LK}/{Auth-Partner-ID}/{API-Key}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticate Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public AuthResponseDTO authenticateIndividual(@Validated @RequestBody AuthRequestDTO authrequestdto,
			@ApiIgnore Errors errors, @PathVariable("MISP-LK") String mispLK, @PathVariable("Auth-Partner-ID") String partnerId,
			@PathVariable("API-Key") String partnerApiKey)
			throws IdAuthenticationAppException, IdAuthenticationDaoException, IdAuthenticationBusinessException {
		
		boolean isAuth = true;
		Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, authrequestdto.getMetadata());
		AuthTransactionBuilder authTxnBuilder = authTransactionHelper
				.createAndSetAuthTxnBuilderMetadataToRequest(authrequestdto, !isAuth, partner);
		
		try {
			String idType = Objects.nonNull(authrequestdto.getIndividualIdType()) ? authrequestdto.getIndividualIdType()
					: idTypeUtil.getIdType(authrequestdto.getIndividualId()).getType();
			authrequestdto.setIndividualIdType(idType);
			authRequestValidator.validateIdvId(authrequestdto.getIndividualId(), idType, errors);
			if(!errors.hasErrors() && Optional.of(authrequestdto)
					.map(AuthRequestDTO::getRequestedAuth)
					.filter(AuthTypeDTO::isBio)
					.isPresent()) {
				authRequestValidator.validateDeviceDetails(authrequestdto, errors);
			}
			DataValidationUtil.validate(errors);
			AuthResponseDTO authResponsedto = authFacade.authenticateIndividual(authrequestdto, true, partnerId, partnerApiKey, IdAuthCommonConstants.CONSUME_VID_DEFAULT);
			// Note: Auditing of success or failure status of each authentication (but not
			// the exception) is handled in respective authentication invocations in the facade
			return authResponsedto;
		} catch (IDDataValidationException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					"authenticateApplication", e.getErrorCode() + " : " + e.getErrorText());
			
			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authrequestdto, e);
			
			throw authTransactionHelper.createDataValidationException(authTxnBuilder, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					"authenticateApplication",  e.getErrorCode() + " : " + e.getErrorText());
			
			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authrequestdto, e);
			
			throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e);
		} 
	}

	
}
