package io.mosip.registration.processor.status.api.controller;

import java.util.List;
import java.util.Objects;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusRequestDTO;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.sync.response.dto.RegStatusResponseDTO;
import io.mosip.registration.processor.status.validator.RegistrationStatusRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class RegistrationStatusController.
 */
@RefreshScope
@RestController
@Api(tags = "Registration Status")
public class RegistrationStatusController {

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The sync registration service. */
	@Autowired
	SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;

	/** The validator. */
	@Autowired
	RegistrationStatusRequestValidator registrationStatusRequestValidator;

	/** Token validator class */
	@Autowired
	TokenValidator tokenValidator;

	private static final String REG_STATUS_SERVICE_ID = "mosip.registration.processor.registration.status.id";
	private static final String REG_STATUS_APPLICATION_VERSION = "mosip.registration.processor.registration.status.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String RESPONSE_SIGNATURE = "Response-Signature";

	@Autowired
	private Environment env;


	@Value("${registration.processor.signature.isEnabled}")
	private Boolean isEnabled;

	@Autowired
	private DigitalSignatureUtility digitalSignatureUtility;

	/**
	 * Search.
	 *
	 * @param registrationIds
	 *            the registration ids
	 * @return the response entity
	 * @throws RegStatusAppException
	 */
	@PostMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the registration entity", response = RegistrationExternalStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Registration Entity successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the Registration Entity") })
	public ResponseEntity<Object> search(
			@RequestBody(required = true) RegistrationStatusRequestDTO registrationStatusRequestDTO,
			@CookieValue(value = "Authorization") String token) throws RegStatusAppException {
		tokenValidator.validate("Authorization=" + token, "registrationstatus");
		try {
			registrationStatusRequestValidator.validate(registrationStatusRequestDTO,
					env.getProperty(REG_STATUS_SERVICE_ID));
			List<RegistrationStatusDto> registrations = registrationStatusService
					.getByIds(registrationStatusRequestDTO.getRequest());
			if (isEnabled) {
				HttpHeaders headers = new HttpHeaders();
				headers.add(RESPONSE_SIGNATURE,
						digitalSignatureUtility.getDigitalSignature(buildRegistrationStatusResponse(registrations)));
				return ResponseEntity.status(HttpStatus.OK).headers(headers)
						.body(buildRegistrationStatusResponse(registrations));
			}
			return ResponseEntity.status(HttpStatus.OK).body(buildRegistrationStatusResponse(registrations));
		} catch (RegStatusAppException e) {
			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);
		} catch (Exception e) {
			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_UNKNOWN_EXCEPTION, e);
		}
	}

	public String buildRegistrationStatusResponse(List<RegistrationStatusDto> registrations) {

		RegStatusResponseDTO response = new RegStatusResponseDTO();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_STATUS_SERVICE_ID));
		}
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(REG_STATUS_APPLICATION_VERSION));
		response.setResponse(registrations);
		response.setErrors(null);
		Gson gson = new GsonBuilder().create();
		return gson.toJson(response);
	}

}