package io.mosip.registration.processor.status.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.core.token.validation.exception.AccessDeniedException;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.status.dto.RegistrationTransactionDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.TransactionService;
import io.mosip.registration.processor.status.sync.response.dto.RegTransactionResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RefreshScope
@RestController
@Api(tags = "Registration Status")
public class RegistrationTransactionController {
	
	@Autowired
	TransactionService<TransactionDto> transactionService;
	
	@Autowired
	private Environment env;
	
	@Autowired
	TokenValidator tokenValidator;
	
	@Value("${registration.processor.signature.isEnabled}")
	private Boolean isEnabled;
	
	@Autowired
	private DigitalSignatureUtility digitalSignatureUtility;
	
	private static final String REG_TRANSACTION_SERVICE_ID = "mosip.registration.processor.registration.transaction.id";
	private static final String REG_TRANSACTION_APPLICATION_VERSION = "mosip.registration.processor.transaction.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String RESPONSE_SIGNATURE = "Response-Signature";
	
	@GetMapping(path = "/rid/{rid}")
	@ApiOperation(value = "Get the transaction entity/entities")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Transaction Entity/Entities successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch Transaction Entity/Entities") })
	public ResponseEntity<RegTransactionResponseDTO> getTransactionsbyRid(@PathVariable String rid,HttpServletRequest request)
			throws RegStatusAppException {
		List<RegistrationTransactionDto> dtoList=new ArrayList<>();
		List<ErrorDTO> errors=new ArrayList<>();
		HttpHeaders headers = new HttpHeaders();
		Cookie token=WebUtils.getCookie( request,"Authorization");
		tokenValidator.validate("Authorization=" + token.getValue(), "transaction");
		try {	
			
			dtoList = transactionService.getTransactionByRegId(rid);	
			if(dtoList.isEmpty()) {
				ErrorDTO errorDTO=new ErrorDTO();
				errorDTO.setErrorCode("RPR-RPN-001");
				errorDTO.setMessage("RID Not Found");
				errors.add(errorDTO);
			}
			if (isEnabled) {		 
				headers.add(RESPONSE_SIGNATURE,
						digitalSignatureUtility.getDigitalSignature(buildSignatureRegistrationTransactionResponse(
								buildRegistrationTransactionResponse(dtoList,errors))));	
				return ResponseEntity.status(HttpStatus.OK).headers(headers)
						.body(buildRegistrationTransactionResponse(dtoList,errors));
			}
				return ResponseEntity.status(HttpStatus.OK)
						.body(buildRegistrationTransactionResponse(dtoList,errors));
		}catch (Exception e) {
			if(e instanceof TablenotAccessibleException | e instanceof InvalidTokenException |
					e instanceof AccessDeniedException) {
				throw e;
			}
			else {
				throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_UNKNOWN_EXCEPTION, e);
			}
		}
	}

	private RegTransactionResponseDTO buildRegistrationTransactionResponse(List<RegistrationTransactionDto> dtoList,
			List<ErrorDTO> errors) {
		RegTransactionResponseDTO regTransactionResponseDTO= new RegTransactionResponseDTO();
		if (Objects.isNull(regTransactionResponseDTO.getId())) {
			regTransactionResponseDTO.setId(env.getProperty(REG_TRANSACTION_SERVICE_ID));
		}
		regTransactionResponseDTO.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		regTransactionResponseDTO.setVersion(env.getProperty(REG_TRANSACTION_APPLICATION_VERSION));
		regTransactionResponseDTO.setErrors(errors);
		regTransactionResponseDTO.setResponse(dtoList);
		return regTransactionResponseDTO;
	}

	private String buildSignatureRegistrationTransactionResponse(RegTransactionResponseDTO dto) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(dto);
	}
}
