package io.mosip.registration.processor.status.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.constant.ResponseStatusCode;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.dto.SyncResponseFailureDto;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.RegStatusValidationException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.sync.response.dto.RegSyncResponseDTO;
import io.mosip.registration.processor.status.utilities.RegStatusValidationUtil;
import io.mosip.registration.processor.status.validator.RegistrationSyncRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;
import io.mosip.registration.processor.status.dto.RegistrationSyncRequestDTO;
import io.mosip.registration.processor.status.dto.SyncErrorDTO;

/**
 * The Class RegistrationStatusController.
 */
@RefreshScope
@RestController
@RequestMapping("/registration-processor")
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
	private RegistrationSyncRequestValidator validator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}

	@Autowired
	private Environment env;

	/** Token validator class */
	@Autowired
	TokenValidator tokenValidator;
	
	private static final String REG_SYNC_SERVICE_ID = "mosip.registration.processor.registration.sync.id";
	private static final String REG_SYNC_APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/**
	 * Sync registration ids.
	 *
	 * @param syncRegistrationList the sync registration list
	 * @return the response entity
	 * @throws RegStatusAppException
	 */
	@PostMapping(path = "/sync/v1.0", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the synchronizing registration entity", response = RegistrationStatusCode.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Synchronizing Registration Entity successfully fetched") })
	public ResponseEntity<Object> syncRegistrationController(
			@Validated @RequestBody(required = true) RegistrationSyncRequestDTO registrationSyncRequestDTO,
			@CookieValue(value = "Authorization", required = true) String token, @ApiIgnore Errors errors)
			throws RegStatusAppException {
		tokenValidator.validate(token, "registrationstatus");
		try {
			RegStatusValidationUtil.validate(errors);
			List<SyncResponseDto> syncResponseDtoList = syncRegistrationService
					.sync(registrationSyncRequestDTO.getRequest());
			return ResponseEntity.ok().body(buildRegistrationSyncResponse(syncResponseDtoList));
		} catch (RegStatusValidationException | JsonProcessingException e) {
			throw new RegStatusAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);
		}
	}

	public String buildRegistrationSyncResponse(List<SyncResponseDto> syncResponseDtoList)
			throws JsonProcessingException {

		RegSyncResponseDTO response = new RegSyncResponseDTO();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_SYNC_SERVICE_ID));
		}
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(REG_SYNC_APPLICATION_VERSION));
		List<SyncErrorDTO> syncErrorDTOList = new ArrayList<>();
		List<SyncResponseDto> syncResponseList = new ArrayList<SyncResponseDto>();
		for (SyncResponseDto syncResponseDto : syncResponseDtoList) {
			if (syncResponseDto.getStatus().equals(ResponseStatusCode.SUCCESS.toString())) {
				syncResponseList.add(syncResponseDto);
			} else {
				if (syncResponseDto instanceof SyncResponseFailureDto) {
					SyncErrorDTO errors = new SyncErrorDTO(((SyncResponseFailureDto) syncResponseDto).getErrorCode(),
							syncResponseDto.getMessage());
					errors.setRegistrationId(syncResponseDto.getRegistrationId());
					errors.setStatus(syncResponseDto.getStatus());
					errors.setParentRegistrationId(syncResponseDto.getParentRegistrationId());
					syncErrorDTOList.add(errors);
				}
			}
		}
		if (!syncErrorDTOList.isEmpty()) {
			response.setErrors(syncErrorDTOList);
		}
		if (!syncResponseList.isEmpty()) {
			response.setResponse(syncResponseList);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String objectMapperResponse = null;
		objectMapperResponse = objectMapper.writeValueAsString(response);
		return objectMapperResponse;
	}

}
