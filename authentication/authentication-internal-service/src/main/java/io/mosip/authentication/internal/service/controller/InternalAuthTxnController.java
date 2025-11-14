package io.mosip.authentication.internal.service.controller;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.autntxn.dto.AutnTxnDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnResponseDto;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * The {@code InternalAuthTxnController} use to fetch Auth Transaction
 * 
 * @author Dinesh Karuppiah.T
 */
@RestController
@Tag(name = "internal-auth-txn-controller", description = "Internal Auth Txn Controller")
public class InternalAuthTxnController {

	private static Logger logger = IdaLogger.getLogger(InternalAuthTxnController.class);

	private static final String AUTH_TXN_DETAILS = "getAuthTransactionDetails";

	@Autowired
	@Qualifier("authTxnValidator")
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
	EnvUtil environment;
	
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
	//@PreAuthorize("hasAnyRole('RESIDENT')")
	@PreAuthorize("hasAnyRole(@authorizedRoles.getGetauthtransactionsindividualid())")
	@GetMapping(path = "/authTransactions/individualId/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Auth Transaction Request", description = "Auth Transaction Request", tags = { "internal-auth-txn-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "400", description = "No Records Found" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
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
		// Removed Storing the idvid hash value in audit entries. 
		// For this type of request storing a UUID instead of idvid hash.
		String randomId = UUID.randomUUID().toString();
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
			auditHelper.audit(AuditModules.AUTH_TRANSACTION_HISTORY, AuditEvents.RETRIEVE_AUTH_TRANSACTION_HISTORY_REQUEST_RESPONSE, randomId,
					IdType.getIDTypeOrDefault(authtxnrequestdto.getIndividualIdType()), "auth transaction history status : " + status );
			return new ResponseEntity<>(autnTxnResponseDto, HttpStatus.OK);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTH_TXN_DETAILS,
					e.getErrorText());
			
			auditHelper.audit(AuditModules.AUTH_TRANSACTION_HISTORY, AuditEvents.RETRIEVE_AUTH_TRANSACTION_HISTORY_REQUEST_RESPONSE, randomId,
					IdType.getIDTypeOrDefault(authtxnrequestdto.getIndividualIdType()), e );
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			
			auditHelper.audit(AuditModules.AUTH_TRANSACTION_HISTORY, AuditEvents.RETRIEVE_AUTH_TRANSACTION_HISTORY_REQUEST_RESPONSE, randomId,
					IdType.getIDTypeOrDefault(authtxnrequestdto.getIndividualIdType()), e );
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}

	}

	private String getResponseTime() {
		return DateUtils.formatDate(
				DateUtils.parseToDate(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()),
						EnvUtil.getDateTimePattern(),
						TimeZone.getTimeZone(ZoneOffset.UTC)),
				EnvUtil.getDateTimePattern(),
				TimeZone.getTimeZone(ZoneOffset.UTC));
	}

}
