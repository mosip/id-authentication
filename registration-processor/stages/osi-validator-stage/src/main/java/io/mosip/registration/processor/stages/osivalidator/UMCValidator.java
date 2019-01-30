package io.mosip.registration.processor.stages.osivalidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gson.Gson;

import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.DeviceHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.DeviceHistoryResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.ErrorDTO;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.MachineHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.MachineHistoryResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterDeviceHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterDeviceHistoryResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class UMCValidator.
 * 
 * @author Jyothi
 */
@Service
public class UMCValidator {

	/** The packet info manager. */
	@Autowired
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The umc client. */

	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	/** The registration status dto. */
	private InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	/** The adapter. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The primary languagecode. */
	@Value("${primary.language}")
	private String primaryLanguagecode;

	/** The identity iterator util. */
	IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();

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
	 * @param latitude
	 *            the latitude
	 * @param longitude
	 *            the longitude
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidRegistrationCenter(String registrationCenterId, String langCode, String effectiveDate,
			String latitude, String longitude) throws ApisResourceAccessException {
		boolean activeRegCenter = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(registrationCenterId);
		pathsegments.add(langCode);
		pathsegments.add(effectiveDate);

		RegistrationCenterResponseDto rcpdto = (RegistrationCenterResponseDto) registrationProcessorRestService
				.getApi(ApiName.CENTERHISTORY, pathsegments, "", "", RegistrationCenterResponseDto.class);

		List<RegistrationCenterDto> dtos = new ArrayList<>();
		if (rcpdto != null)
			dtos = rcpdto.getRegistrationCentersHistory();

		if (dtos == null || dtos.isEmpty()) {
			this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_ID_NOT_FOUND);
		} else {
			for (RegistrationCenterDto dto : dtos) {

				if (dto.getLatitude() != null && dto.getLongitude() != null && dto.getLatitude().matches(latitude)
						&& dto.getLongitude().matches(longitude)) {

					activeRegCenter = dto.getIsActive();
					if (!activeRegCenter) {
						this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_NOT_ACTIVE);
					}
					break;
				} else {
					this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
				}

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
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidMachine(String machineId, String langCode, String effdatetimes)
			throws ApisResourceAccessException {

		boolean isActiveMachine = false;

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(machineId);
		pathsegments.add(langCode);
		pathsegments.add(effdatetimes);
		MachineHistoryResponseDto mhrdto = (MachineHistoryResponseDto) registrationProcessorRestService
				.getApi(ApiName.MACHINEHISTORY, pathsegments, "", "", MachineHistoryResponseDto.class);

		List<MachineHistoryDto> dtos = new ArrayList<>();
		if (mhrdto != null)
			dtos = mhrdto.getMachineHistoryDetails();
		if (dtos == null || dtos.isEmpty()) {
			this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_ID_NOT_FOUND);
		} else {
			for (MachineHistoryDto dto : dtos) {
				if (dto.getId() != null && dto.getId().matches(machineId)) {
					isActiveMachine = dto.getIsActive();
					if (!isActiveMachine) {
						this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_NOT_ACTIVE);
					}
					break;
				} else {
					this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_ID_NOT_FOUND);
				}
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
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidUMCmapping(String effectiveTimestamp, String registrationCenterId, String machineId,
			String superviserId, String officerId) throws ApisResourceAccessException {

		boolean supervisorActive = false;
		boolean officerActive = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(effectiveTimestamp);
		pathsegments.add(registrationCenterId);
		pathsegments.add(machineId);
		pathsegments.add(superviserId);

		RegistrationCenterUserMachineMappingHistoryResponseDto supervisordto = (RegistrationCenterUserMachineMappingHistoryResponseDto) registrationProcessorRestService
				.getApi(ApiName.CENTERUSERMACHINEHISTORY, pathsegments, "", "",
						RegistrationCenterUserMachineMappingHistoryResponseDto.class);
		List<String> officerpathsegments = new ArrayList<>();
		officerpathsegments.add(effectiveTimestamp);
		officerpathsegments.add(registrationCenterId);
		officerpathsegments.add(machineId);
		officerpathsegments.add(officerId);
		RegistrationCenterUserMachineMappingHistoryResponseDto officerdto = (RegistrationCenterUserMachineMappingHistoryResponseDto) registrationProcessorRestService
				.getApi(ApiName.CENTERUSERMACHINEHISTORY, officerpathsegments, "", "",
						RegistrationCenterUserMachineMappingHistoryResponseDto.class);

		List<RegistrationCenterUserMachineMappingHistoryDto> supervisordtos = new ArrayList<>();
		if (supervisordto != null)
			supervisordtos = supervisordto.getRegistrationCenters();
		List<RegistrationCenterUserMachineMappingHistoryDto> officerdtos = new ArrayList<>();
		if (officerdto != null)
			officerdtos = officerdto.getRegistrationCenters();

		if (checkNotNull(supervisordtos)) {

			supervisorActive = supervisordtos.get(0).getIsActive();

		}
		if (checkNotNull(officerdtos)) {
			officerActive = officerdtos.get(0).getIsActive();
		}
		if (checkNull(supervisordtos) && checkNull(officerdtos)) {
			this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_MACHINE_USER_MAPPING_NOT_FOUND);
		}

		return supervisorActive || officerActive;
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
	 * @return true, if is valid UMC
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public boolean isValidUMC(String registrationId) throws ApisResourceAccessException, JsonParseException,
			JsonMappingException, io.mosip.kernel.core.exception.IOException, IOException {
		RegistrationCenterMachineDto rcmDto = getCenterMachineDto(registrationId);

		RegOsiDto regOsi = packetInfoManager.getOsi(registrationId);
		boolean umc = false;

		if (rcmDto.getLatitude() == null || rcmDto.getLongitude() == null || rcmDto.getLatitude().trim().isEmpty()
				|| rcmDto.getLongitude().trim().isEmpty()) {
			this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
		}

		else if (isValidRegistrationCenter(rcmDto.getRegcntrId(), primaryLanguagecode, rcmDto.getPacketCreationDate(),
				rcmDto.getLatitude(), rcmDto.getLongitude())
				&& isValidMachine(rcmDto.getMachineId(), primaryLanguagecode, rcmDto.getPacketCreationDate())
				&& isValidUMCmapping(rcmDto.getPacketCreationDate(), rcmDto.getRegcntrId(), rcmDto.getMachineId(),
						regOsi.getSupervisorId(), regOsi.getOfficerId())) {
			umc = true;
		}

		if (umc) {
			// MOS-12831
			umc = isValidDevice(rcmDto);
		}

		return umc;
	}

	/**
	 * Gets the registration status dto.
	 *
	 * @return the registration status dto
	 */
	public InternalRegistrationStatusDto getRegistrationStatusDto() {
		return this.registrationStatusDto;
	}

	/**
	 * Sets the registration status dto.
	 *
	 * @param registrationStatusDto
	 *            the new registration status dto
	 */
	public void setRegistrationStatusDto(InternalRegistrationStatusDto registrationStatusDto) {
		this.registrationStatusDto = registrationStatusDto;
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
	 */
	private RegistrationCenterMachineDto getCenterMachineDto(String registrationId)
			throws JsonParseException, JsonMappingException, io.mosip.kernel.core.exception.IOException, IOException {

		identity = getIdentity(registrationId);

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
	 * Gets the identity.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the identity
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private Identity getIdentity(String registrationId)
			throws IOException, JsonParseException, JsonMappingException, io.mosip.kernel.core.exception.IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("packet_meta_info.json").getFile());
		InputStream packetMetaInfoStream = new FileInputStream(idJsonFile);
		// InputStream packetMetaInfoStream = adapter.getFile(registrationId,
		// PacketFiles.PACKET_META_INFO.name());

		// String packetMetaInfoString = IOUtils.toString(packetMetaInfoStream);

		// PacketMetaInfo packetMetaInfo = (PacketMetaInfo)
		// JsonUtils.jsonStringToJavaObject(PacketMetaInfo.class,
		// packetMetaInfoString);

		PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
				PacketMetaInfo.class);

		return packetMetaInfo.getIdentity();

	}

	/**
	 * Checks if is valid device.
	 *
	 * @param rcmDto
	 *            the rcm dto
	 * @return true, if is valid device
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidDevice(RegistrationCenterMachineDto rcmDto) throws ApisResourceAccessException {
		boolean isValidDevice = false;
		if (isDeviceActive(rcmDto) && isDeviceMappedWithCenter(rcmDto)) {
			isValidDevice = true;
		}
		return isValidDevice;
	}

	/**
	 * Checks if is device mapped with center.
	 *
	 * @param rcmDto
	 *            the rcm dto
	 * @return true, if is device mapped with center
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isDeviceMappedWithCenter(RegistrationCenterMachineDto rcmDto) throws ApisResourceAccessException {
		boolean isDeviceMappedWithCenter = false;
		List<FieldValue> registreredDeviceIds = identity.getCapturedRegisteredDevices();

		for (FieldValue fieldValue : registreredDeviceIds) {
			String deviceId = null;
			deviceId = fieldValue.getValue();
			RegistrationCenterDeviceHistoryResponseDto registrationCenterDeviceHistoryResponseDto;
			try {
				List<String> pathsegments = new ArrayList<>();
				pathsegments.add(rcmDto.getRegcntrId());
				pathsegments.add(deviceId);
				pathsegments.add(rcmDto.getPacketCreationDate());

				registrationCenterDeviceHistoryResponseDto = (RegistrationCenterDeviceHistoryResponseDto) registrationProcessorRestService
						.getApi(ApiName.REGISTRATIONCENTERDEVICEHISTORY, pathsegments, "", "",
								RegistrationCenterDeviceHistoryResponseDto.class);
				isDeviceMappedWithCenter = validateDeviceMappedWithCenterResponse(
						registrationCenterDeviceHistoryResponseDto, deviceId, rcmDto.getRegcntrId(), rcmDto.getRegId());

			} catch (ApisResourceAccessException e) {
				if (e.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
					String result = httpClientException.getResponseBodyAsString();
					Gson gsonObj = new Gson();
					registrationCenterDeviceHistoryResponseDto = gsonObj.fromJson(result,
							RegistrationCenterDeviceHistoryResponseDto.class);
					ErrorDTO error = registrationCenterDeviceHistoryResponseDto.getErrors().get(0);
					isDeviceMappedWithCenter = false;
					if (error.getErrorCode().equalsIgnoreCase("KER-MSD-133")) {
						this.registrationStatusDto.setStatusComment(StatusMessage.DEVICE_ID + " " + deviceId
								+ StatusMessage.CENTER_ID + " " + rcmDto.getRegcntrId() + StatusMessage.DEVICE_NOT_FOUND
								+ " " + rcmDto.getRegId());
					} else {
						this.registrationStatusDto.setStatusComment(error.getErrorMessage());
					}
				}
				break;
			}
		}

		return isDeviceMappedWithCenter;
	}

	/**
	 * Validate device mapped with center response.
	 *
	 * @param registrationCenterDeviceHistoryResponseDto
	 *            the registration center device history response dto
	 * @return true, if successful
	 */
	private boolean validateDeviceMappedWithCenterResponse(
			RegistrationCenterDeviceHistoryResponseDto registrationCenterDeviceHistoryResponseDto, String deviceId,
			String centerId, String regId) {
		boolean isDeviceMappedWithCenter = false;
		RegistrationCenterDeviceHistoryDto registrationCenterDeviceHistoryDto = registrationCenterDeviceHistoryResponseDto
				.getRegistrationCenterDeviceHistoryDetails();

		if (registrationCenterDeviceHistoryDto.getIsActive()) {
			isDeviceMappedWithCenter = true;
		} else {
			this.registrationStatusDto.setStatusComment(StatusMessage.DEVICE_ID + " " + deviceId
					+ StatusMessage.CENTER_ID + " " + centerId + StatusMessage.DEVICE_WAS_IN_ACTIVE + " " + regId);

		}

		return isDeviceMappedWithCenter;

	}

	/**
	 * Checks if is device active.
	 *
	 * @param rcmDto
	 *            the rcm dto
	 * @return true, if is device active
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isDeviceActive(RegistrationCenterMachineDto rcmDto) {
		boolean isDeviceActive = false;

		List<FieldValue> registreredDeviceIds = identity.getCapturedRegisteredDevices();
		for (FieldValue fieldValue : registreredDeviceIds) {
			String deviceId = null;
			deviceId = fieldValue.getValue();
			DeviceHistoryResponseDto deviceHistoryResponsedto;
			try {
				List<String> pathsegments = new ArrayList<>();

				pathsegments.add(deviceId);
				pathsegments.add(primaryLanguagecode);
				pathsegments.add(rcmDto.getPacketCreationDate());

				deviceHistoryResponsedto = (DeviceHistoryResponseDto) registrationProcessorRestService
						.getApi(ApiName.DEVICESHISTORIES, pathsegments, "", "", DeviceHistoryResponseDto.class);

				isDeviceActive = validateDeviceResponse(deviceHistoryResponsedto, deviceId, rcmDto.getRegId());
			} catch (ApisResourceAccessException e) {
				if (e.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
					String result = httpClientException.getResponseBodyAsString();
					Gson gsonObj = new Gson();
					deviceHistoryResponsedto = gsonObj.fromJson(result, DeviceHistoryResponseDto.class);
					ErrorDTO error = deviceHistoryResponsedto.getErrors().get(0);
					isDeviceActive = false;
					if (error.getErrorCode().equalsIgnoreCase("KER-MSD-129")) {
						this.registrationStatusDto.setStatusComment(StatusMessage.DEVICE_ID + " " + deviceId
								+ StatusMessage.DEVICE_NOT_FOUND + " " + rcmDto.getRegId());
					} else {
						this.registrationStatusDto.setStatusComment(error.getErrorMessage());
					}

				}
				break;
			}

		}
		return isDeviceActive;
	}

	/**
	 * Validate device response.
	 *
	 * @param deviceHistoryResponsedto
	 *            the device history responsedto
	 * @return true, if successful
	 */
	private boolean validateDeviceResponse(DeviceHistoryResponseDto deviceHistoryResponsedto, String deviceId,
			String regId) {

		boolean isDeviceActive = false;

		List<DeviceHistoryDto> dtos = deviceHistoryResponsedto.getDeviceHistoryDetails();
		if (dtos != null && !dtos.isEmpty()) {
			DeviceHistoryDto deviceHistoryDto = dtos.get(0);
			if (deviceHistoryDto.getIsActive()) {
				isDeviceActive = true;
			} else {
				this.registrationStatusDto.setStatusComment(
						StatusMessage.DEVICE_ID + " " + deviceId + StatusMessage.DEVICE_WAS_IN_ACTIVE + " " + regId);

			}

		}

		return isDeviceActive;
	}
}
