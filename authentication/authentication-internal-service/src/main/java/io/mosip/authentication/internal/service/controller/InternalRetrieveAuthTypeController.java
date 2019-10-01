package io.mosip.authentication.internal.service.controller;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.authtype.dto.AuthtypeResponseDto;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.AuthtypeStatusService;
import io.mosip.authentication.internal.service.validator.AuthtypeStatusValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The {@code InternalAuthTxnController} use to fetch Auth Transaction
 * 
 * @author Dinesh Karuppiah.T
 */
@RestController
public class InternalRetrieveAuthTypeController {

	private static Logger logger = IdaLogger.getLogger(InternalRetrieveAuthTypeController.class);

	private static final String AUTH_TYPE_STATUS = "getAuthTypeStatus";

	@Autowired
	private AuthtypeStatusValidator authtypeStatusValidator;

	@Autowired
	private AuthtypeStatusService authtypeStatusService;

	@Autowired
	Environment environment;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(authtypeStatusValidator);
	}

	/**
	 * To fetch Auth Type status based on Individual's details
	 *
	 * @param authtypeResponseDto as request body
	 * @param errors              associate error
	 * @param partnerId           the partner id
	 * @param mispLK              the misp LK
	 * @return authtypeResponseDto
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException    the ID data validation exception
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','ID_AUTHENTICATION')")
	@ApiOperation(value = "Authtype Status Request", response = IdAuthenticationAppException.class)
	@GetMapping(path = "/authtypes/status/individualIdType/{IDType}/individualId/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "No Records Found") })
	public ResponseEntity<AuthtypeResponseDto> getAuthTypeStatus(@PathVariable("IDType") String individualIdType,
			@PathVariable("ID") String individualId) throws IdAuthenticationAppException, IDDataValidationException {
		try {
			AuthtypeResponseDto authtypeResponseDto = new AuthtypeResponseDto();
			AuthtypeRequestDto authtypeRequestDto = new AuthtypeRequestDto();
			authtypeRequestDto.setIndividualId(individualId);
			authtypeRequestDto.setIndividualIdType(individualIdType);
			Errors errors = new BindException(authtypeRequestDto, "authtypeRequestDto");
			authtypeStatusValidator.validate(authtypeRequestDto, errors);
			DataValidationUtil.validate(errors);
			List<AuthtypeStatus> authtypeStatusList = authtypeStatusService.fetchAuthtypeStatus(authtypeRequestDto);
			Map<String, List<AuthtypeStatus>> authtypestatusmap = new HashMap<>();
			authtypestatusmap.put("authTypes", authtypeStatusList);
			authtypeResponseDto.setResponse(authtypestatusmap);
			authtypeResponseDto.setResponseTime(getResponseTime());
			return new ResponseEntity<>(authtypeResponseDto, HttpStatus.OK);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTH_TYPE_STATUS,
					e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}

	}

	/**
	 * To get Response Time
	 * 
	 * @return
	 */
	private String getResponseTime() {
		return DateUtils.formatDate(
				DateUtils.parseToDate(DateUtils.getUTCCurrentDateTimeString(),
						environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
						TimeZone.getTimeZone(ZoneOffset.UTC)),
				environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
				TimeZone.getTimeZone(ZoneOffset.UTC));
	}

}
