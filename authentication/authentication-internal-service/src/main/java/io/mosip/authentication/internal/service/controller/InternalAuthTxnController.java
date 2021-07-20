package io.mosip.authentication.internal.service.controller;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.autntxn.dto.AutnTxnDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnResponseDto;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtxn.service.AuthTxnService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
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
	
	@Autowired
	private AuditHelper auditHelper;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(authTxnValidator);
	}

	@Autowired
	Environment environment;
	
	@Autowired
	private IdTypeUtil idTypeUtil;

	/**
	 * To fetch Auth Transactions details based on Individual's details
	 *
	 * @param otpRequestDto as request body
	 * @param errors        associate error
	 * @param partnerId     the partner id
	 * @param mispLK        the misp LK
	 * @return otpResponseDTO
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IdAuthenticationBusinessException 
	 */
	@PreAuthorize("hasAnyRole('RESIDENT')")
	@ApiOperation(value = "Auth Transaction Request", response = IdAuthenticationAppException.class)
	@GetMapping(path = "/authTransactions/individualId/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "No Records Found") })
	public ResponseEntity<AutnTxnResponseDto> getAuthTxnDetails(
			@RequestParam(name = "IDType", required = false) String individualIdType,
			@PathVariable("ID") String individualId,
			@RequestParam(name = "pageStart", required = false) Integer pageStart,
			@RequestParam(name = "pageFetch", required = false) Integer pageFetch)
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		AutnTxnResponseDto autnTxnResponseDto = new AutnTxnResponseDto();
		AutnTxnRequestDto authtxnrequestdto = new AutnTxnRequestDto();
		authtxnrequestdto.setIndividualId(individualId);
		authtxnrequestdto.setIndividualIdType(
				Objects.isNull(individualIdType) ? idTypeUtil.getIdType(individualId).getType() : individualIdType);
		authtxnrequestdto.setPageStart(pageStart);
		authtxnrequestdto.setPageFetch(pageFetch);
		
		try {
			Errors errors = new BindException(authtxnrequestdto, "authtxnrequestdto");
			authTxnValidator.validate(authtxnrequestdto, errors);
			DataValidationUtil.validate(errors);
			List<AutnTxnDto> authTxnList = authTxnService.fetchAuthTxnDetails(authtxnrequestdto);
			Map<String, List<AutnTxnDto>> authTxnMap = new HashMap<>();
			authTxnMap.put("authTransactions", authTxnList);
			autnTxnResponseDto.setResponse(authTxnMap);
			autnTxnResponseDto.setResponseTime(getResponseTime());
			
			boolean status = true;
			auditHelper.audit(AuditModules.AUTH_TRANSACTION_HISTORY, AuditEvents.RETRIEVE_AUTH_TRANSACTION_HISTORY_REQUEST_RESPONSE, authtxnrequestdto.getIndividualId(),
					IdType.getIDTypeOrDefault(authtxnrequestdto.getIndividualIdType()), "auth transaction history status : " + status );
			return new ResponseEntity<>(autnTxnResponseDto, HttpStatus.OK);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTH_TXN_DETAILS,
					e.getErrorText());
			
			auditHelper.audit(AuditModules.AUTH_TRANSACTION_HISTORY, AuditEvents.RETRIEVE_AUTH_TRANSACTION_HISTORY_REQUEST_RESPONSE, authtxnrequestdto.getIndividualId(),
					IdType.getIDTypeOrDefault(authtxnrequestdto.getIndividualIdType()), e );
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			
			auditHelper.audit(AuditModules.AUTH_TRANSACTION_HISTORY, AuditEvents.RETRIEVE_AUTH_TRANSACTION_HISTORY_REQUEST_RESPONSE, authtxnrequestdto.getIndividualId(),
					IdType.getIDTypeOrDefault(authtxnrequestdto.getIndividualIdType()), e );
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}

	}

	private String getResponseTime() {
		return DateUtils.formatDate(
				DateUtils.parseToDate(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()),
						environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
						TimeZone.getTimeZone(ZoneOffset.UTC)),
				environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
				TimeZone.getTimeZone(ZoneOffset.UTC));
	}

}
