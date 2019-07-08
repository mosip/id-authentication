package io.mosip.registration.processor.stages.osivalidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.DeviceHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.DeviceHistoryResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.MachineHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.MachineHistoryResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistartionCenterTimestampResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterDeviceHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterDeviceHistoryResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.stages.osivalidator.utils.OSIUtils;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class UMCValidator.
 *
 * @author Jyothi
 * @author Ranjitha Siddegowda
 */
@Service
public class UMCValidator {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(UMCValidator.class);

	/** The response from masterdata validate api. */
	private static final String VALID = "Valid";

	/** The umc client. */

	@Value("${mosip.workinghour.validation.required}")
	private Boolean isWorkingHourValidationRequired;

	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	@Autowired
	private OSIUtils osiUtils;

	/** The primary languagecode. */
	@Value("${mosip.primary-language}")
	private String primaryLanguagecode;

	/** The identity iterator util. */
	IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();

	private static final String NO_DEVICE_HISTORY_FOUND = "no device history found for device : ";

	private static final String IS_DEVICE_MAPPED_WITH_CENTER = "no center found for device : ";

	ObjectMapper mapper = new ObjectMapper();

	/** The identity. */
	Identity identity;

	/**
	 * Validate registration center.
	 *
	 * @param registrationCenterId
	 *            the registration center id
	 * @param langCode
	 *            the lang code
	 * @param effectiveDate
	 *            the effective date
	 * @param registrationStatusDto
	 * @param latitude
	 *            the latitude
	 * @param longitude
	 *            the longitude
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @throws com.fasterxml.jackson.databind.JsonMappingException
	 * @throws com.fasterxml.jackson.core.JsonParseException
	 */
	@SuppressWarnings("unchecked")
	private boolean isValidRegistrationCenter(String registrationCenterId, String langCode, String effectiveDate,
			InternalRegistrationStatusDto registrationStatusDto) throws ApisResourceAccessException, IOException {
		boolean activeRegCenter = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(registrationCenterId);
		pathsegments.add(langCode);
		pathsegments.add(effectiveDate);
		RegistrationCenterResponseDto rcpdto = null;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();

		try {

			responseWrapper = (ResponseWrapper<?>) registrationProcessorRestService.getApi(ApiName.CENTERHISTORY,
					pathsegments, "", "", ResponseWrapper.class);
			rcpdto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
					RegistrationCenterResponseDto.class);

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"UMCValidator::isValidRegistrationCenter()::CenterHistory service ended with response data : "
							+ JsonUtil.objectMapperObjectToJson(rcpdto));

			if (responseWrapper.getErrors() == null) {
				activeRegCenter = rcpdto.getRegistrationCentersHistory().get(0).getIsActive();
				if (!activeRegCenter) {
					registrationStatusDto.setStatusComment(StatusMessage.CENTER_NOT_ACTIVE);
				}
			} else {
				List<ErrorDTO> error = responseWrapper.getErrors();
				registrationStatusDto.setStatusComment(error.get(0).getMessage());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						"UMCValidator::isValidRegistrationCenter()::CenterHistory service ended with response data : "
								+ error.get(0).getMessage());

			}

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				String result = httpClientException.getResponseBodyAsString();

				activeRegCenter = false;

				registrationStatusDto.setStatusComment(result);

			} else {
				throw e;
			}

		}
		return activeRegCenter;

	}

	/**
	 * Validate machine.
	 *
	 * @param machineId
	 *            the machine id
	 * @param langCode
	 *            the lang code
	 * @param effdatetimes
	 *            the effdatetimes
	 * @param registrationStatusDto
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @throws com.fasterxml.jackson.databind.JsonMappingException
	 * @throws com.fasterxml.jackson.core.JsonParseException
	 */
	@SuppressWarnings("unchecked")
	private boolean isValidMachine(String machineId, String langCode, String effdatetimes,
			InternalRegistrationStatusDto registrationStatusDto) throws ApisResourceAccessException, IOException {

		boolean isActiveMachine = false;

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(machineId);
		pathsegments.add(langCode);
		pathsegments.add(effdatetimes);
		MachineHistoryResponseDto mhrdto;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();

		try {

			responseWrapper = (ResponseWrapper<?>) registrationProcessorRestService.getApi(ApiName.MACHINEHISTORY,
					pathsegments, "", "", ResponseWrapper.class);
			mhrdto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
					MachineHistoryResponseDto.class);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"UMCValidator::isValidMachine()::MachineHistory service ended with response data : "
							+ JsonUtil.objectMapperObjectToJson(mhrdto));
			if (responseWrapper.getErrors() == null) {
				MachineHistoryDto dto = mhrdto.getMachineHistoryDetails().get(0);

				if (dto.getId() != null && dto.getId().matches(machineId)) {
					isActiveMachine = dto.getIsActive();
					if (!isActiveMachine) {
						registrationStatusDto.setStatusComment(StatusMessage.MACHINE_NOT_ACTIVE);
					}

				} else {
					registrationStatusDto.setStatusComment(StatusMessage.MACHINE_ID_NOT_FOUND);
				}
			} else {
				List<ErrorDTO> error = responseWrapper.getErrors();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						"UMCValidator::isValidMachine()::MachineHistory service ended with response data : "
								+ error.get(0).getMessage());
				registrationStatusDto.setStatusComment(error.get(0).getMessage());
			}
		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				String result = httpClientException.getResponseBodyAsString();

				registrationStatusDto.setStatusComment(result);

				isActiveMachine = false;
			}
		}
		return isActiveMachine;

	}

	/**
	 * Validate UM cmapping.
	 *
	 * @param effectiveTimestamp
	 *            the effective timestamp
	 * @param registrationCenterId
	 *            the registration center id
	 * @param machineId
	 *            the machine id
	 * @param superviserId
	 *            the superviser id
	 * @param officerId
	 *            the officer id
	 * @param registrationStatusDto
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 */
	private boolean isValidUMCmapping(String effectiveTimestamp, String registrationCenterId, String machineId,
			String superviserId, String officerId, InternalRegistrationStatusDto registrationStatusDto)
			throws ApisResourceAccessException, IOException {

		boolean supervisorActive = false;
		boolean officerActive = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(effectiveTimestamp);
		pathsegments.add(registrationCenterId);
		pathsegments.add(machineId);
		pathsegments.add(superviserId);
		RegistrationCenterUserMachineMappingHistoryResponseDto supervisordto;
		if (superviserId != null)
			supervisorActive = validateMapping(pathsegments, registrationStatusDto);

		if (!supervisorActive) {
			List<String> officerpathsegments = new ArrayList<>();
			officerpathsegments.add(effectiveTimestamp);
			officerpathsegments.add(registrationCenterId);
			officerpathsegments.add(machineId);
			officerpathsegments.add(officerId);
			if (officerId != null)
				officerActive = validateMapping(officerpathsegments, registrationStatusDto);

		}
		if (!supervisorActive && !officerActive) {
			registrationStatusDto.setStatusComment("Registration Center, Machine, User not found");
		}
		return supervisorActive || officerActive;
	}

	private boolean validateMapping(List<String> pathsegments, InternalRegistrationStatusDto registrationStatusDto)
			throws IOException, ApisResourceAccessException {
		boolean isValidUser = false;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
		RegistrationCenterUserMachineMappingHistoryResponseDto userDto = null;

		try {

			responseWrapper = (ResponseWrapper<?>) registrationProcessorRestService
					.getApi(ApiName.CENTERUSERMACHINEHISTORY, pathsegments, "", "", ResponseWrapper.class);
			userDto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
					RegistrationCenterUserMachineMappingHistoryResponseDto.class);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"UMCValidator::validateMapping()::CenterUserMachineHistory service ended with response data : "
							+ JsonUtil.objectMapperObjectToJson(userDto));
			if (userDto != null) {
				if (responseWrapper.getErrors() == null) {
					isValidUser = userDto.getRegistrationCenters().get(0).getIsActive();
				} else {
					List<ErrorDTO> error = responseWrapper.getErrors();
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
							"UMCValidator::validateMapping()::CenterUserMachineHistory service ended with response data : "
									+ error.get(0).getMessage());
					registrationStatusDto.setStatusComment(error.get(0).getMessage());
				}
			}
		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				isValidUser = false;

				registrationStatusDto.setStatusComment(httpClientException.getResponseBodyAsString());

			}
		}
		return isValidUser;
	}

	/**
	 * Check not null.
	 *
	 * @param validatorDtos
	 *            the validator dtos
	 * @return true, if successful
	 */
	boolean checkNotNull(List<RegistrationCenterUserMachineMappingHistoryDto> validatorDtos) {
		return (validatorDtos != null && !validatorDtos.isEmpty());
	}

	/**
	 * Check null.
	 *
	 * @param validatorDtos
	 *            the validator dtos
	 * @return true, if successful
	 */
	boolean checkNull(List<RegistrationCenterUserMachineMappingHistoryDto> validatorDtos) {
		return (validatorDtos == null || validatorDtos.isEmpty());
	}

	/**
	 * Checks if is valid UMC.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param registrationStatusDto2
	 * @return true, if is valid UMC
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws PacketDecryptionFailureException
	 */
	public boolean isValidUMC(String registrationId, InternalRegistrationStatusDto registrationStatusDto)
			throws ApisResourceAccessException, JsonParseException, JsonMappingException,
			io.mosip.kernel.core.exception.IOException, IOException, PacketDecryptionFailureException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "UMCValidator::isValidUMC()::entry");
		RegistrationCenterMachineDto rcmDto = getCenterMachineDto(registrationId);

		identity = osiUtils.getIdentity(registrationId);
		RegOsiDto regOsi = osiUtils.getOSIDetailsFromMetaInfo(registrationId, identity);
		boolean umc = false;

		if (rcmDto.getLatitude() == null || rcmDto.getLongitude() == null || rcmDto.getLatitude().trim().isEmpty()
				|| rcmDto.getLongitude().trim().isEmpty()) {
			registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
		}

		else if (isWorkingHourValidationRequired
				&& isValidRegistrationCenter(rcmDto.getRegcntrId(), primaryLanguagecode, rcmDto.getPacketCreationDate(),
						registrationStatusDto)
				&& isValidMachine(rcmDto.getMachineId(), primaryLanguagecode, rcmDto.getPacketCreationDate(),
						registrationStatusDto)
				&& isValidUMCmapping(rcmDto.getPacketCreationDate(), rcmDto.getRegcntrId(), rcmDto.getMachineId(),
						regOsi.getSupervisorId(), regOsi.getOfficerId(), registrationStatusDto)
				&& validateCenterIdAndTimestamp(rcmDto, registrationStatusDto)
				&& isValidDevice(rcmDto, registrationStatusDto))
			umc = true;
		else if (isValidRegistrationCenter(rcmDto.getRegcntrId(), primaryLanguagecode, rcmDto.getPacketCreationDate(),
				registrationStatusDto)
				&& isValidMachine(rcmDto.getMachineId(), primaryLanguagecode, rcmDto.getPacketCreationDate(),
						registrationStatusDto)
				&& isValidUMCmapping(rcmDto.getPacketCreationDate(), rcmDto.getRegcntrId(), rcmDto.getMachineId(),
						regOsi.getSupervisorId(), regOsi.getOfficerId(), registrationStatusDto)
				&& isValidDevice(rcmDto, registrationStatusDto))
			umc = true;

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "UMCValidator::isValidUMC()::exit");
		return umc;
	}

	/**
	 * Gets the center machine dto.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the center machine dto
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ApisResourceAccessException
	 * @throws PacketDecryptionFailureException
	 */
	private RegistrationCenterMachineDto getCenterMachineDto(String registrationId)
			throws JsonParseException, JsonMappingException, io.mosip.kernel.core.exception.IOException, IOException,
			PacketDecryptionFailureException, ApisResourceAccessException {

		identity = osiUtils.getIdentity(registrationId);

		List<FieldValue> metaData = identity.getMetaData();
		return mapMetaDataToDto(metaData);

	}

	/**
	 * Map meta data to dto.
	 *
	 * @param metaData
	 *            the meta data
	 * @return the registration center machine dto
	 */
	private RegistrationCenterMachineDto mapMetaDataToDto(List<FieldValue> metaData) {
		RegistrationCenterMachineDto dto = new RegistrationCenterMachineDto();
		dto.setRegId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.REGISTRATIONID));
		dto.setRegcntrId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.CENTERID));
		dto.setMachineId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.MACHINEID));
		dto.setLatitude(identityIteratorUtil.getFieldValue(metaData, JsonConstant.GEOLOCLATITUDE));
		dto.setLongitude(identityIteratorUtil.getFieldValue(metaData, JsonConstant.GEOLOCLONGITUDE));
		dto.setPacketCreationDate(identityIteratorUtil.getFieldValue(metaData, JsonConstant.CREATIONDATE));
		return dto;
	}

	/**
	 * Checks if is valid device.
	 *
	 * @param rcmDto
	 *            the rcm dto
	 * @param registrationStatusDto
	 * @return true, if is valid device
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 */
	private boolean isValidDevice(RegistrationCenterMachineDto rcmDto,
			InternalRegistrationStatusDto registrationStatusDto) throws ApisResourceAccessException, IOException {
		boolean isValidDevice = false;
		if (isDeviceActive(rcmDto, registrationStatusDto) && isDeviceMappedWithCenter(rcmDto, registrationStatusDto)) {
			isValidDevice = true;
		}
		return isValidDevice;
	}

	/**
	 * Checks if is device mapped with center.
	 *
	 * @param rcmDto
	 *            the rcm dto
	 * @param registrationStatusDto
	 * @return true, if is device mapped with center
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @throws com.fasterxml.jackson.databind.JsonMappingException
	 * @throws com.fasterxml.jackson.core.JsonParseException
	 */

	private boolean isDeviceMappedWithCenter(RegistrationCenterMachineDto rcmDto,
			InternalRegistrationStatusDto registrationStatusDto) throws ApisResourceAccessException, IOException {
		boolean isDeviceMappedWithCenter = false;
		List<FieldValue> registreredDeviceIds = identity.getCapturedRegisteredDevices();
		if (registreredDeviceIds != null && !registreredDeviceIds.isEmpty()) {
			for (FieldValue fieldValue : registreredDeviceIds) {
				String deviceId = null;
				deviceId = fieldValue.getValue();
				RegistrationCenterDeviceHistoryResponseDto registrationCenterDeviceHistoryResponseDto;
				try {

					List<String> pathsegments = new ArrayList<>();
					pathsegments.add(rcmDto.getRegcntrId());
					pathsegments.add(deviceId);
					pathsegments.add(rcmDto.getPacketCreationDate());

					ResponseWrapper<?> responseWrapper = (ResponseWrapper<?>) registrationProcessorRestService.getApi(
							ApiName.REGISTRATIONCENTERDEVICEHISTORY, pathsegments, "", "", ResponseWrapper.class);
					registrationCenterDeviceHistoryResponseDto = mapper.readValue(
							mapper.writeValueAsString(responseWrapper.getResponse()),
							RegistrationCenterDeviceHistoryResponseDto.class);
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
							"UMCValidator::isDeviceMappedWithCenter()::CenterUserMachineHistory service ended with response data : "
									+ JsonUtil.objectMapperObjectToJson(registrationCenterDeviceHistoryResponseDto));
					if (responseWrapper.getErrors() == null) {
						isDeviceMappedWithCenter = validateDeviceMappedWithCenterResponse(
								registrationCenterDeviceHistoryResponseDto, deviceId, rcmDto.getRegcntrId(),
								rcmDto.getRegId(), registrationStatusDto);
						if (!isDeviceMappedWithCenter) {
							registrationStatusDto.setStatusComment(
									StatusMessage.OSI_VALIDATION_FAILURE + IS_DEVICE_MAPPED_WITH_CENTER + deviceId);
							break;
						}
					} else {
						isDeviceMappedWithCenter = false;
						List<ErrorDTO> error = responseWrapper.getErrors();
						regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
								"UMCValidator::isDeviceMappedWithCenter()::CenterUserMachineHistory service ended with response data : "
										+ error.get(0).getMessage());
						registrationStatusDto.setStatusComment(error.get(0).getMessage());
						break;
					}
				} catch (ApisResourceAccessException e) {
					if (e.getCause() instanceof HttpClientErrorException) {
						HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
						String result = httpClientException.getResponseBodyAsString();

						isDeviceMappedWithCenter = false;
						registrationStatusDto.setStatusComment(result);
					}
					break;
				}
			}
		} else {
			isDeviceMappedWithCenter = true;
		}
		return isDeviceMappedWithCenter;
	}

	/**
	 * Validate device mapped with center response.
	 *
	 * @param registrationCenterDeviceHistoryResponseDto
	 *            the registration center device history response dto
	 * @param registrationStatusDto
	 * @return true, if successful
	 */
	private boolean validateDeviceMappedWithCenterResponse(
			RegistrationCenterDeviceHistoryResponseDto registrationCenterDeviceHistoryResponseDto, String deviceId,
			String centerId, String regId, InternalRegistrationStatusDto registrationStatusDto) {
		boolean isDeviceMappedWithCenter = false;
		RegistrationCenterDeviceHistoryDto registrationCenterDeviceHistoryDto = registrationCenterDeviceHistoryResponseDto
				.getRegistrationCenterDeviceHistoryDetails();

		if (registrationCenterDeviceHistoryDto.getIsActive()) {
			isDeviceMappedWithCenter = true;
		} else {
			registrationStatusDto.setStatusComment(StatusMessage.DEVICE_ID + " " + deviceId + StatusMessage.CENTER_ID
					+ " " + centerId + StatusMessage.DEVICE_WAS_IN_ACTIVE + " " + regId);

		}

		return isDeviceMappedWithCenter;

	}

	/**
	 * Checks if is device active.
	 *
	 * @param rcmDto
	 *            the rcm dto
	 * @param registrationStatusDto
	 * @return true, if is device active
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @throws com.fasterxml.jackson.databind.JsonMappingException
	 * @throws com.fasterxml.jackson.core.JsonParseException
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */

	private boolean isDeviceActive(RegistrationCenterMachineDto rcmDto,
			InternalRegistrationStatusDto registrationStatusDto)
			throws JsonProcessingException, IOException, ApisResourceAccessException {
		boolean isDeviceActive = false;

		List<FieldValue> registreredDeviceIds = identity.getCapturedRegisteredDevices();
		if (registreredDeviceIds != null && !registreredDeviceIds.isEmpty()) {
			for (FieldValue fieldValue : registreredDeviceIds) {
				String deviceId = null;
				deviceId = fieldValue.getValue();
				DeviceHistoryResponseDto deviceHistoryResponsedto;
				try {
					List<String> pathsegments = new ArrayList<>();

					pathsegments.add(deviceId);
					pathsegments.add(primaryLanguagecode);
					pathsegments.add(rcmDto.getPacketCreationDate());

					ResponseWrapper<?> responseWrapper = (ResponseWrapper<?>) registrationProcessorRestService
							.getApi(ApiName.DEVICESHISTORIES, pathsegments, "", "", ResponseWrapper.class);
					deviceHistoryResponsedto = mapper.readValue(
							mapper.writeValueAsString(responseWrapper.getResponse()), DeviceHistoryResponseDto.class);
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
							"UMCValidator::isDeviceActive()::CenterUserMachineHistory service ended with response data : "
									+ JsonUtil.objectMapperObjectToJson(deviceHistoryResponsedto));
					if (deviceHistoryResponsedto.getErrors() == null) {
						isDeviceActive = validateDeviceResponse(deviceHistoryResponsedto, deviceId, rcmDto.getRegId(),
								registrationStatusDto);
						if (!isDeviceActive) {
							registrationStatusDto.setStatusComment(
									StatusMessage.OSI_VALIDATION_FAILURE + NO_DEVICE_HISTORY_FOUND + deviceId);
							break;

						}
					} else {
						isDeviceActive = false;
						List<ErrorDTO> error = responseWrapper.getErrors();
						registrationStatusDto.setStatusComment(error.get(0).getMessage());
						regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
								"UMCValidator::isDeviceActive()::CenterUserMachineHistory service ended with response data : "
										+ error.get(0).getMessage());
						break;
					}

				} catch (ApisResourceAccessException e) {
					if (e.getCause() instanceof HttpClientErrorException) {
						HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
						String result = httpClientException.getResponseBodyAsString();

						isDeviceActive = false;
						registrationStatusDto.setStatusComment(result);

					}
					break;
				}

			}

		} else {
			isDeviceActive = true;
		}
		return isDeviceActive;
	}

	/**
	 * Validate device response.
	 *
	 * @param deviceHistoryResponsedto
	 *            the device history responsedto
	 * @param registrationStatusDto
	 * @return true, if successful
	 */
	private boolean validateDeviceResponse(DeviceHistoryResponseDto deviceHistoryResponsedto, String deviceId,
			String regId, InternalRegistrationStatusDto registrationStatusDto) {

		boolean isDeviceActive = false;

		List<DeviceHistoryDto> dtos = deviceHistoryResponsedto.getDeviceHistoryDetails();
		if (dtos != null && !dtos.isEmpty()) {
			DeviceHistoryDto deviceHistoryDto = dtos.get(0);
			if (deviceHistoryDto.getIsActive()) {
				isDeviceActive = true;
			} else {
				registrationStatusDto.setStatusComment(
						StatusMessage.DEVICE_ID + " " + deviceId + StatusMessage.DEVICE_WAS_IN_ACTIVE + " " + regId);

			}

		}

		return isDeviceActive;
	}

	/**
	 * Checks if is valid center id timestamp.
	 * 
	 * @param registrationStatusDto
	 *
	 * @param centerId
	 *            the center id
	 * @param timestamp
	 *            the timestamp
	 * @return true, if is valid center id timestamp
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @throws com.fasterxml.jackson.databind.JsonMappingException
	 * @throws com.fasterxml.jackson.core.JsonParseException
	 * @throws UMCValidationException
	 *
	 */

	private boolean validateCenterIdAndTimestamp(RegistrationCenterMachineDto rcmDto,
			InternalRegistrationStatusDto registrationStatusDto) throws ApisResourceAccessException, IOException {
		boolean isValid = false;
		try {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rcmDto.getRegId(), "UMCValidator::validateCenterIdAndTimestamp()::entry");
			List<String> pathsegments = new ArrayList<>();
			pathsegments.add(rcmDto.getRegcntrId());
			pathsegments.add(primaryLanguagecode);
			pathsegments.add(rcmDto.getPacketCreationDate());
			ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
			RegistartionCenterTimestampResponseDto result;

			responseWrapper = (ResponseWrapper<?>) registrationProcessorRestService
					.getApi(ApiName.REGISTRATIONCENTERTIMESTAMP, pathsegments, "", "", ResponseWrapper.class);

			result = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
					RegistartionCenterTimestampResponseDto.class);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"UMCValidator::isDeviceActive()::CenterUserMachineHistory service ended with response data : "
							+ JsonUtil.objectMapperObjectToJson(result));
			if (responseWrapper.getErrors() == null) {
				if (result.getStatus().equals(VALID)) {
					isValid = true;
				} else {
					registrationStatusDto.setStatusComment(StatusMessage.TIMESTAMP_VALIDATION1 + " " + rcmDto.getRegId()
							+ StatusMessage.TIMESTAMP_VALIDATION2 + " " + rcmDto.getRegcntrId());
				}
			} else {
				List<ErrorDTO> error = responseWrapper.getErrors();
				registrationStatusDto.setStatusComment(error.get(0).getMessage());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						"UMCValidator::isDeviceActive()::CenterUserMachineHistory service ended with response data : "
								+ error.get(0).getMessage());
			}

		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rcmDto.getRegId(), e.getMessage() + ExceptionUtils.getStackTrace(e));
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();

				String result = httpClientException.getResponseBodyAsString();
				registrationStatusDto.setStatusComment(result);

			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				rcmDto.getRegId(), "UMCValidator::validateCenterIdAndTimestamp()::exit");
		return isValid;
	}
}