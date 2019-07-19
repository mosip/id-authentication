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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.autntxn.dto.AutnTxnDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnResponseDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtxn.service.AuthTxnService;
import io.mosip.authentication.internal.service.validator.AuthTxnValidator;
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
public class InternalAuthTxnController {

	private static Logger logger = IdaLogger.getLogger(InternalAuthTxnController.class);

	private static final String AUTH_TXN_DETAILS = "getAuthTransactionDetails";

	@Autowired
	private AuthTxnValidator authTxnValidator;

	@Autowired
	private AuthTxnService authTxnService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(authTxnValidator);
	}

	@Autowired
	Environment environment;

	/**
	 * To fetch Auth Transactions details based on Individual's details
	 *
	 * @param otpRequestDto as request body
	 * @param errors        associate error
	 * @param partnerId     the partner id
	 * @param mispLK        the misp LK
	 * @return otpResponseDTO
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException    the ID data validation exception
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','ID_AUTHENTICATION')")
	@ApiOperation(value = "Auth Transaction Request", response = IdAuthenticationAppException.class)
	@GetMapping(path = "/authTransactions/individualIdType/{IDType}/individualId/{ID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "No Records Found") })
	public ResponseEntity<AutnTxnResponseDto> getAuthTxnDetails(@PathVariable("IDType") String individualIdType,
			@PathVariable("ID") String individualId,
			@RequestParam(name = "pageStart", required = false) Integer pageStart,
			@RequestParam(name = "pageFetch", required = false) Integer pageFetch)
			throws IdAuthenticationAppException, IDDataValidationException {
		try {
			AutnTxnResponseDto autnTxnResponseDto = new AutnTxnResponseDto();
			AutnTxnRequestDto authtxnrequestdto = new AutnTxnRequestDto();
			authtxnrequestdto.setIndividualId(individualId);
			authtxnrequestdto.setIndividualIdType(individualIdType);
			authtxnrequestdto.setPageStart(pageStart);
			authtxnrequestdto.setPageFetch(pageFetch);
			Errors errors = new BindException(authtxnrequestdto, "authtxnrequestdto");
			authTxnValidator.validate(authtxnrequestdto, errors);
			DataValidationUtil.validate(errors);
			List<AutnTxnDto> authTxnList = authTxnService.fetchAuthTxnDetails(authtxnrequestdto);
			Map<String, List<AutnTxnDto>> authTxnMap = new HashMap<>();
			authTxnMap.put("authTransactions", authTxnList);
			autnTxnResponseDto.setResponse(authTxnMap);
			autnTxnResponseDto.setResponseTime(getResponseTime());
			return new ResponseEntity<>(autnTxnResponseDto, HttpStatus.OK);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTH_TXN_DETAILS,
					e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}

	}

	private String getResponseTime() {
		return DateUtils.formatDate(
				DateUtils.parseToDate(DateUtils.getUTCCurrentDateTimeString(),
						environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
						TimeZone.getTimeZone(ZoneOffset.UTC)),
				environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
				TimeZone.getTimeZone(ZoneOffset.UTC));
	}

}
