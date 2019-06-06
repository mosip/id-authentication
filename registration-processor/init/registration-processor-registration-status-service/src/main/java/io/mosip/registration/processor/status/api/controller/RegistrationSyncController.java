package io.mosip.registration.processor.status.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.constant.ResponseStatusCode;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationSyncRequestDTO;
import io.mosip.registration.processor.status.dto.SyncErrDTO;
import io.mosip.registration.processor.status.dto.SyncErrorDTO;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.dto.SyncResponseFailDto;
import io.mosip.registration.processor.status.dto.SyncResponseFailureDto;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.sync.response.dto.RegSyncResponseDTO;
import io.mosip.registration.processor.status.validator.RegistrationSyncRequestValidator;
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

	@Autowired
	private Environment env;

	/** Token validator class */
	@Autowired
	TokenValidator tokenValidator;

	@Value("${registration.processor.signature.isEnabled}")
	private Boolean isEnabled;

	@Autowired
	private DigitalSignatureUtility digitalSignatureUtility;


	private static final String REG_SYNC_SERVICE_ID = "mosip.registration.processor.registration.sync.id";
	private static final String REG_SYNC_APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String RESPONSE_SIGNATURE = "Response-Signature";

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
	public ResponseEntity<Object> syncRegistrationController(
			@RequestHeader(name = "Center-Machine-RefId", required = true) String referenceId,
			@RequestHeader(name = "timestamp", required = true) String timeStamp,
			@RequestBody(required = true) Object encryptedSyncMetaInfo,
			@CookieValue(value = "Authorization", required = true) String token) throws RegStatusAppException {
		tokenValidator.validate("Authorization=" + token, "sync");

		try {
			List<SyncResponseDto> syncResponseList = new ArrayList<>();
			RegistrationSyncRequestDTO registrationSyncRequestDTO = syncRegistrationService
					.decryptAndGetSyncRequest(encryptedSyncMetaInfo, referenceId, timeStamp, syncResponseList);

			if (registrationSyncRequestDTO != null && validator.validate(registrationSyncRequestDTO,
					env.getProperty(REG_SYNC_SERVICE_ID), syncResponseList)) {
				syncResponseList = syncRegistrationService.sync(registrationSyncRequestDTO.getRequest());
			}
			if(isEnabled) {
				HttpHeaders headers = new HttpHeaders();
				headers.add(RESPONSE_SIGNATURE,digitalSignatureUtility.getDigitalSignature(buildRegistrationSyncResponse(syncResponseList)));
				return ResponseEntity.ok().headers(headers).body(buildRegistrationSyncResponse(syncResponseList));
			}

			return ResponseEntity.ok().body(buildRegistrationSyncResponse(syncResponseList));

		} catch (JsonProcessingException e) {
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
					SyncErrDTO errors = new SyncErrDTO(((SyncResponseFailureDto) syncResponseDto).getErrorCode(),
							((SyncResponseFailureDto) syncResponseDto).getMessage());
					errors.setRegistrationId(((SyncResponseFailureDto) syncResponseDto).getRegistrationId());
					errors.setStatus(syncResponseDto.getStatus());

					syncErrorDTOList.add(errors);
				} else if (syncResponseDto instanceof SyncResponseFailDto) {
					SyncErrorDTO errors = new SyncErrorDTO(((SyncResponseFailDto) syncResponseDto).getErrorCode(),
							((SyncResponseFailDto) syncResponseDto).getMessage());
					errors.setStatus(syncResponseDto.getStatus());
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