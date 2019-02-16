package io.mosip.registration.processor.status.api.controller;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.RegStatusValidationException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.sync.response.dto.RegSyncResponseDTO;
import io.mosip.registration.processor.status.utilities.RegStatusValidationUtil;
import io.mosip.registration.processor.status.validator.RegistrationStatusRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;
import io.mosip.registration.processor.status.dto.RegistrationSyncRequestDTO;
/**
 * The Class RegistrationStatusController.
 */
@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/registration-status")
@Api(tags = "Registration Status")
public class RegistrationSyncController {

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The sync registration service. */
	@Autowired
	SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;

	/** The validator. */
	@Autowired
	private RegistrationStatusRequestValidator validator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}

	private static final String REG_SYNC_SERVICE_ID = "mosip.registration.sync";
	private static final String REG_SYNC_APPLICATION_VERSION = "1.0";
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	
	/**
	 * Sync registration ids.
	 *
	 * @param syncRegistrationList
	 *            the sync registration list
	 * @return the response entity
	 * @throws RegStatusAppException 
	 */
	@PostMapping(path = "/sync", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the synchronizing registration entity", response = RegistrationStatusCode.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Synchronizing Registration Entity successfully fetched") })
	public ResponseEntity<Object> syncRegistrationController(@Validated @RequestBody(required = true) RegistrationSyncRequestDTO registrationSyncRequestDTO,@ApiIgnore Errors errors) throws RegStatusAppException {

		try {
			RegStatusValidationUtil.validate(errors);
			List<SyncResponseDto> syncResponseDtoList = syncRegistrationService.sync(registrationSyncRequestDTO.getRequest());
			return ResponseEntity.ok().body(buildRegistrationSyncResponse(syncResponseDtoList));
		}catch (RegStatusValidationException e) {
			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);
		}
	}


	public RegSyncResponseDTO buildRegistrationSyncResponse(List<SyncResponseDto> syncResponseDtoList) {

		RegSyncResponseDTO response = new RegSyncResponseDTO();
		if (Objects.isNull(response.getId())) {
			response.setId(REG_SYNC_SERVICE_ID);
		}
		response.setError(null);
		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(REG_SYNC_APPLICATION_VERSION);
		response.setResponse(syncResponseDtoList);
		response.setError(null);
		return response;
	}

}
