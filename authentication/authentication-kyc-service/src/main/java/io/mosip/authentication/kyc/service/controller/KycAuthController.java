package io.mosip.authentication.kyc.service.controller;

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

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
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
	@PostMapping(path = "/{eKYC-Partner-ID}/{MISP-LK}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "eKyc Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public KycAuthResponseDTO processKyc(@Validated @RequestBody KycAuthRequestDTO kycAuthRequestDTO,
			@ApiIgnore Errors errors, @PathVariable("eKYC-Partner-ID") String partnerId,
			@PathVariable("MISP-LK") String mispLK)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		AuthResponseDTO authResponseDTO = null;
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		try {
			DataValidationUtil.validate(errors);
			authResponseDTO = kycFacade.authenticateIndividual(kycAuthRequestDTO, true, partnerId);
			if (authResponseDTO != null) {
				kycAuthResponseDTO = kycFacade.processKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId);
			}
		} catch (IDDataValidationException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKyc",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKyc",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
		
		return kycAuthResponseDTO;
	}

}
