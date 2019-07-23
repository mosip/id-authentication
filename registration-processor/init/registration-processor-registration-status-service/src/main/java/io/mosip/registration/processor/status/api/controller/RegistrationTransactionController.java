package io.mosip.registration.processor.status.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusRequestDTO;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.service.TransactionService;
import io.mosip.registration.processor.status.validator.RegistrationTransactionRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RefreshScope
@RestController
@Api(tags = "Registration Transaction")
public class RegistrationTransactionController {
	/*
	@Autowired
	TransactionService<TransactionDto> transactionService;
	
	@Autowired
	private Environment env;
	
	@Autowired
	TokenValidator tokenValidator;
	
	@Autowired
	RegistrationTransactionRequestValidator registrationTransactionRequestValidator;
	
	@Value("${registration.processor.signature.isEnabled}")
	private Boolean isEnabled;
	
	@Autowired
	private DigitalSignatureUtility digitalSignatureUtility;
	
	private static final String REG_TRANSACTION_SERVICE_ID = "mosip.registration.processor.registration.transaction.id";
	private static final String REG_TRANSACTION_APPLICATION_VERSION = "mosip.registration.processor.transaction.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String RESPONSE_SIGNATURE = "Response-Signature";
	
	@PostMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the transaction entity", response = RegistrationExternalStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Transaction Entity/Entities successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch Transaction Entity/Entities") })
	public ResponseEntity<Object> search(
			@RequestBody(required = true) RegistrationStatusRequestDTO registrationStatusRequestDTO,
			@CookieValue(value = "Authorization") String token)
			throws RegStatusAppException {
		tokenValidator.validate("Authorization=" + token, "transaction");
		try {
			registrationTransactionRequestValidator.validate(registrationStatusRequestDTO,
					env.getProperty(REG_TRANSACTION_SERVICE_ID));
			List<RegistrationStatusDto> registrations = registrationStatusService
					.getByIds(registrationStatusRequestDTO.getRequest());
			if (isEnabled) {
				HttpHeaders headers = new HttpHeaders();
				headers.add(RESPONSE_SIGNATURE,
						digitalSignatureUtility.getDigitalSignature(buildSignatureRegistrationTransactionResponse(registrations)));
				return ResponseEntity.status(HttpStatus.OK).headers(headers)
						.body(buildSignatureRegistrationTransactionResponse(registrations));
			}
			return ResponseEntity.status(HttpStatus.OK).body(buildRegistrationStatusResponse(registrations));
		} catch (RegStatusAppException e) {
			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);
		} catch (Exception e) {
			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_UNKNOWN_EXCEPTION, e);
		}
	}

	private Object buildRegistrationStatusResponse(List<RegistrationStatusDto> registrations) {
		// TODO Auto-generated method stub
		return null;
	}

	private String buildSignatureRegistrationTransactionResponse(List<RegistrationStatusDto> registrations) {
		// TODO Auto-generated method stub
		return null;
	}*/
}
