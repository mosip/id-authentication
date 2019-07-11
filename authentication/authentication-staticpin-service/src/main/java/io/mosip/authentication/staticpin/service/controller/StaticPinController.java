package io.mosip.authentication.staticpin.service.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.staticpin.service.StaticPinService;
import io.mosip.authentication.core.staticpin.dto.StaticPinRequestDTO;
import io.mosip.authentication.core.staticpin.dto.StaticPinResponseDTO;
import io.mosip.authentication.staticpin.service.validator.StaticPinRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * This Class will provide to Store the Static Pin value
 * 
 * @author Prem Kumar
 *
 */
@RestController
public class StaticPinController {

	/** The logger */
	private static Logger logger = IdaLogger.getLogger(StaticPinController.class);

	/** The Static Pin Facade */
	@Autowired
	private StaticPinService staticPinService;

	@Autowired
	private Environment env;

	/** The Static Pin Request Validator */
	@Autowired
	private StaticPinRequestValidator staticPinRequestValidator;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/**
	 * This method will bind StaticPinRequestValidator
	 * 
	 * @param binder
	 */
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(staticPinRequestValidator);
	}

	/** The Constant AUTH_FACADE. */
	private static final String STATIC_PIN = "StaticPin";

	/**
	 * This class provides store Request of Static Pin and sends proper Response
	 * 
	 * @param staticPinRequestDTO
	 * @param errors
	 * @return staticPinResponseDTO
	 * @throws IdAuthenticationAppException
	 * @throws IDDataValidationException
	 */
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Static Pin Store Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Stored successfully") })
	public StaticPinResponseDTO storeSpin(@Valid @RequestBody StaticPinRequestDTO staticPinRequestDTO,
			@ApiIgnore Errors errors) throws IdAuthenticationAppException, IDDataValidationException {
		try {
			DataValidationUtil.validate(errors);
			return staticPinService.storeSpin(staticPinRequestDTO);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getClass().getName(),
					e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		} finally {
			logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
					STATIC_PIN, "Staticpin Authentication status : " + "");
			String idType = staticPinRequestDTO.getIndividualIdType();
			IdType actualidType =IdType.getIDTypeOrDefault(idType);
			auditHelper.audit(AuditModules.STATIC_PIN_STORAGE, AuditEvents.AUTH_REQUEST_RESPONSE,
					staticPinRequestDTO.getId() != null && !staticPinRequestDTO.getId().isEmpty()
							? staticPinRequestDTO.getId()
							: "",
					actualidType, AuditModules.STATIC_PIN_STORAGE.getDesc());
		}

	}

}
