package io.mosip.authentication.internal.service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.core.autntxn.dto.AutnTxnDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.internal.service.validator.AuthTxnValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The {@code OTPAuthController} use to send request to generate otp.
 * 
 * @author Rakesh Roshan
 */
@RestController
public class InternalAuthTxnController {

	private static final int DEFAULT_COUNT = 10;

	private static final int DEFAULT_PAGE_SIZE = 1;

	private static final String UIN_KEY = "uin";

	private static Logger logger = IdaLogger.getLogger(InternalAuthTxnController.class);

	private static final String AUTH_TXN_DETAILS = "getAuthTransactionDetails";

	@Autowired
	private AutnTxnRepository authtxnRepo;

	@Autowired
	private AuthTxnValidator authTxnValidator;

	@Autowired
	private IdService<AutnTxn> idService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(authTxnValidator);
	}

	/**
	 * send OtpRequestDTO request to generate OTP and received OtpResponseDTO as
	 * output.
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
	@PostMapping(path = "/auth-transactions/individualIdType/{IDType}/individualId/{ID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "No Records Found") })
	public ResponseEntity<List<AutnTxn>> getAuthTxnDetails(@PathVariable("IDType") String individualIdType,
			@PathVariable("ID") String individualId,
			@RequestParam(name = "pageStart", required = false) Integer pageStart,
			@RequestParam(name = "pageFetch", required = false) Integer pageFetch)
			throws IdAuthenticationAppException, IDDataValidationException {
		try {
			List<AutnTxn> autnTxnList = null;
			AutnTxnDto authTxnDto = new AutnTxnDto();
			authTxnDto.setIndividualId(individualId);
			authTxnDto.setIndividualIdType(individualIdType);
			Errors errors = new BindException(authTxnDto, "authTxnDto");
			authTxnValidator.validate(authTxnDto, errors);
			DataValidationUtil.validate(errors);
			Map<String, Object> idResDTO = idService.processIdType(individualIdType, individualId, false);
			if (idResDTO != null && !idResDTO.isEmpty() && idResDTO.containsKey(UIN_KEY)) {
				String uin = String.valueOf(idResDTO.get(UIN_KEY));
				String hashedUin = HMACUtils.digestAsPlainText(HMACUtils.generateHash(uin.getBytes()));
				autnTxnList = authtxnRepo.findByPagableUinorVid(hashedUin,
						PageRequest.of(pageStart == null || pageStart == 0 ? DEFAULT_PAGE_SIZE : pageStart,
								pageFetch == null || pageFetch == 0 ? DEFAULT_COUNT : pageFetch));
				logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTH_TXN_DETAILS,
						"pageStart >>" + pageStart + "pageFetch >>" + pageFetch);
			}
			return new ResponseEntity<>(autnTxnList, HttpStatus.OK);

		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTH_TXN_DETAILS,
					e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}

}
