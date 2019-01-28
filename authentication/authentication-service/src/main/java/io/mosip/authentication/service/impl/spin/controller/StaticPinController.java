package io.mosip.authentication.service.impl.spin.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.spin.facade.StaticPinFacade;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.service.impl.spin.validator.StaticPinRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 *  This Class will provide to Store the Static Pin value
 *  
 * @author Prem Kumar
 *
 */
@RestController
public class StaticPinController {
	
	/** The  logger */
	private static Logger logger = IdaLogger.getLogger(StaticPinController.class);
	
	/** The Constant DEAFULT_SESSION_ID */
	private static final String DEAFULT_SESSION_ID = "sessionId";
	
	/** The Static Pin Facade */
	@Autowired
	private StaticPinFacade staticPinFacade;
	
	/** The Static Pin Request Validator  */
	@Autowired
	private StaticPinRequestValidator staticPinRequestValidator;
	
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(staticPinRequestValidator);
	}
	/**
	 * 
	 * @param staticPinRequestDTO
	 * @param errors
	 * @return staticPinResponseDTO
	 * @throws IdAuthenticationAppException
	 */
	@PostMapping(path = "/v1.0/static-pin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticate Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "Request authenticated failed") })
	public StaticPinResponseDTO storeSpin(@Valid @RequestBody StaticPinRequestDTO staticPinRequestDTO,@ApiIgnore Errors errors)
			throws IdAuthenticationAppException  {
		try {
			DataValidationUtil.validate(errors);
			StaticPinResponseDTO staticPinResponseDTO =staticPinFacade.storeSpin(staticPinRequestDTO);
			return staticPinResponseDTO;
		} catch (IDDataValidationException e) {
			logger.error(DEAFULT_SESSION_ID, null, null, e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(DEAFULT_SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}
		
	}
	
}
