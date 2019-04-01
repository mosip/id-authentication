package io.mosip.registration.processor.packet.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gson.Gson;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.service.PacketCreationService;
import io.mosip.registration.processor.packet.service.PacketGeneratorService;
import io.mosip.registration.processor.packet.service.dto.ErrorDTO;
import io.mosip.registration.processor.packet.service.dto.MachineResponseDto;
import io.mosip.registration.processor.packet.service.dto.PackerGeneratorFailureDto;
import io.mosip.registration.processor.packet.service.dto.PacketGeneratorDto;
import io.mosip.registration.processor.packet.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.packet.service.dto.RegistrationCenterResponseDto;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.RegistrationMetaDataDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.DemographicDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.MoroccoIdentity;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.upload.service.SyncUploadEncryptionService;
import io.mosip.registration.processor.status.code.RegistrationType;

/**
 * @author Sowmya The Class PacketGeneratorServiceImpl.
 */
@Service
public class PacketGeneratorServiceImpl implements PacketGeneratorService {

	/** The rid generator impl. */
	@Autowired
	private RidGenerator<String> ridGeneratorImpl;

	/** The packet creation service. */
	@Autowired
	private PacketCreationService packetCreationService;

	/** The sync upload encryption service. */
	@Autowired
	SyncUploadEncryptionService syncUploadEncryptionService;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The primary languagecode. */
	@Value("${primary.language}")
	private String primaryLanguagecode;

	@Autowired
	private UinValidator<String> uinValidatorImpl;

	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketCreationServiceImpl.class);

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The filemanager. */
	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.packet.service.PacketGeneratorService#
	 * createPacket(io.mosip.registration.processor.packet.service.dto.
	 * PacketGeneratorDto)
	 */
	@Override
	public PacketGeneratorResDto createPacket(PacketGeneratorDto request) throws RegBaseCheckedException {

		PacketGeneratorResDto packerGeneratorResDto = null;
		PackerGeneratorFailureDto dto = new PackerGeneratorFailureDto();

		byte[] packetZipBytes = null;
		if (isValidCenter(request.getCenterId(), dto) && isValidMachine(request.getMachineId(), dto)
				&& isValidUin(request.getUin(), dto) && isValidRegistrationType(request.getRegistrationType(), dto)) {
			try {
				RegistrationDTO registrationDTO = createRegistrationDTOObject(request.getUin(),
						request.getRegistrationType(), request.getCenterId(), request.getMachineId());
				packetZipBytes = packetCreationService.create(registrationDTO);
				String creationTime = packetCreationService.getCreationTime();

				filemanager.put(registrationDTO.getRegistrationId(), new ByteArrayInputStream(packetZipBytes),
						DirectoryPathDto.PACKET_GENERATED_DECRYPTED);

				packerGeneratorResDto = syncUploadEncryptionService.uploadUinPacket(registrationDTO.getRegistrationId(),
						creationTime, request.getRegistrationType());
				return packerGeneratorResDto;
			} catch (Exception e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(),
						PlatformErrorMessages.RPR_PGS_JSON_PROCESSING_EXCEPTION.getMessage(),
						ExceptionUtils.getStackTrace(e));
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						ExceptionUtils.getStackTrace(e), e);

			}
		} else {
			return dto;
		}
	}

	private boolean isValidRegistrationType(String registrationType, PackerGeneratorFailureDto dto)
			throws RegBaseCheckedException {
		if (registrationType.equals(RegistrationType.ACTIVATED.toString())
				|| registrationType.equals(RegistrationType.DEACTIVATED.toString())) {
			return true;
		} else {

			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
					"Invalid RegistrationType:Enter ACTIVATED or DEACTIVATED", new Throwable());

		}

	}

	private boolean isValidUin(String uin, PackerGeneratorFailureDto dto) throws RegBaseCheckedException {
		boolean isValidUIN = false;
		try {
			isValidUIN = uinValidatorImpl.validateId(uin);
			List<String> regIdList = packetInfoManager.getRegIdByUIN(uin);
			if (isValidUIN && ((regIdList != null) && !regIdList.isEmpty())) {
				isValidUIN = true;
			} else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, "UIN is not valid",
						new Throwable());

			}
		} catch (InvalidIDException ex) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, ex.getErrorText(), ex);

		}
		return isValidUIN;
	}

	/**
	 * Creates the registration DTO object.
	 *
	 * @param uin
	 *            the uin
	 * @param registrationType
	 *            the registration type
	 * @param applicantType
	 *            the applicant type
	 * @param centerId
	 *            the center id
	 * @param machineId
	 *            the machine id
	 * @return the registration DTO
	 */
	private RegistrationDTO createRegistrationDTOObject(String uin, String registrationType, String centerId,
			String machineId) {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setDemographicDTO(getDemographicDTO(uin));
		RegistrationMetaDataDTO registrationMetaDataDTO = getRegistrationMetaDataDTO(registrationType, uin, centerId,
				machineId);
		String registrationId = ridGeneratorImpl.generateId(registrationMetaDataDTO.getCenterId(),
				registrationMetaDataDTO.getMachineId());
		registrationDTO.setRegistrationId(registrationId);
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);
		return registrationDTO;

	}

	/**
	 * Gets the demographic DTO.
	 *
	 * @param uin
	 *            the uin
	 * @return the demographic DTO
	 */
	private DemographicDTO getDemographicDTO(String uin) {
		DemographicDTO demographicDTO = new DemographicDTO();
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		MoroccoIdentity identity = new MoroccoIdentity();
		identity.setIdSchemaVersion(1.0);
		identity.setUin(new BigInteger(uin));
		demographicInfoDTO.setIdentity(identity);
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);
		return demographicDTO;
	}

	/**
	 * Gets the registration meta data DTO.
	 *
	 * @param registrationType
	 *            the registration type
	 * @param applicantType
	 *            the applicant type
	 * @param uin
	 *            the uin
	 * @param centerId
	 *            the center id
	 * @param machineId
	 *            the machine id
	 * @return the registration meta data DTO
	 */
	private RegistrationMetaDataDTO getRegistrationMetaDataDTO(String registrationType, String uin, String centerId,
			String machineId) {
		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();

		registrationMetaDataDTO.setCenterId(centerId);
		registrationMetaDataDTO.setMachineId(machineId);
		registrationMetaDataDTO.setRegistrationCategory(registrationType);
		registrationMetaDataDTO.setUin(uin);
		return registrationMetaDataDTO;

	}

	/**
	 * Checks if is valid center.
	 *
	 * @param centerId
	 *            the center id
	 * @param dto
	 *            the dto
	 * @return true, if is valid center
	 * @throws RegBaseCheckedException
	 */
	private boolean isValidCenter(String centerId, PackerGeneratorFailureDto dto) throws RegBaseCheckedException {
		boolean isValidCenter = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(centerId);
		pathsegments.add(primaryLanguagecode);
		RegistrationCenterResponseDto rcpdto;
		try {
			rcpdto = (RegistrationCenterResponseDto) restClientService.getApi(ApiName.CENTERDETAILS, pathsegments, "",
					"", RegistrationCenterResponseDto.class);

			if (rcpdto.getErrors() == null && !rcpdto.getRegistrationCenters().isEmpty()) {
				isValidCenter = true;
			} else {
				ErrorDTO error = rcpdto.getErrors().get(0);

				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.getErrorMessage(), new Throwable());
			}

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				String result = httpClientException.getResponseBodyAsString();
				Gson gsonObj = new Gson();
				rcpdto = gsonObj.fromJson(result, RegistrationCenterResponseDto.class);
				ErrorDTO error = rcpdto.getErrors().get(0);
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.getErrorMessage(), e);

			}

		}
		return isValidCenter;

	}

	/**
	 * Checks if is valid machine.
	 *
	 * @param machine
	 *            the machine
	 * @param dto
	 *            the dto
	 * @return true, if is valid machine
	 * @throws RegBaseCheckedException
	 */
	private boolean isValidMachine(String machine, PackerGeneratorFailureDto dto) throws RegBaseCheckedException {
		boolean isValidMachine = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(machine);
		pathsegments.add(primaryLanguagecode);
		MachineResponseDto machinedto;
		try {
			machinedto = (MachineResponseDto) restClientService.getApi(ApiName.MACHINEDETAILS, pathsegments, "", "",
					MachineResponseDto.class);

			if (machinedto.getErrors() == null && !machinedto.getMachines().isEmpty()) {
				isValidMachine = true;
			} else {
				ErrorDTO error = machinedto.getErrors().get(0);
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.getErrorMessage(), new Throwable());
			}

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				String result = httpClientException.getResponseBodyAsString();
				Gson gsonObj = new Gson();
				machinedto = gsonObj.fromJson(result, MachineResponseDto.class);
				ErrorDTO error = machinedto.getErrors().get(0);
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.getErrorMessage(), e);

			}

		}
		return isValidMachine;

	}
}
