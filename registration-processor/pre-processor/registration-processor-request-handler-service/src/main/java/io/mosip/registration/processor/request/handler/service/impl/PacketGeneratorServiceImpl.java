package io.mosip.registration.processor.request.handler.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.request.handler.service.PacketCreationService;
import io.mosip.registration.processor.request.handler.service.PacketGeneratorService;
import io.mosip.registration.processor.request.handler.service.dto.PackerGeneratorFailureDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationDTO;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationMetaDataDTO;
import io.mosip.registration.processor.request.handler.service.dto.demographic.DemographicDTO;
import io.mosip.registration.processor.request.handler.service.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.processor.request.handler.service.dto.demographic.MoroccoIdentity;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.upload.SyncUploadEncryptionService;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;

/**
 * @author Sowmya The Class PacketGeneratorServiceImpl.
 */
@Service
public class PacketGeneratorServiceImpl implements PacketGeneratorService {

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
	@Value("${mosip.primary-language}")
	private String primaryLanguagecode;

	@Autowired
	private UinValidator<String> uinValidatorImpl;

	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketCreationServiceImpl.class);

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The filemanager. */
	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	@Autowired
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private Utilities utilities;

	@Autowired
	RequestHandlerRequestValidator validator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.packet.service.PacketGeneratorService#
	 * createPacket(io.mosip.registration.processor.packet.service.dto.
	 * PacketGeneratorDto)
	 */
	@Override
	public PacketGeneratorResDto createPacket(PacketGeneratorDto request) throws RegBaseCheckedException, IOException {

		PacketGeneratorResDto packerGeneratorResDto = null;
		PackerGeneratorFailureDto dto = new PackerGeneratorFailureDto();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"PacketGeneratorServiceImpl ::createPacket()::entry");
		byte[] packetZipBytes = null;
		if (validator.isValidCenter(request.getCenterId()) && validator.isValidMachine(request.getMachineId())
				&& validator.isValidRegistrationTypeAndUin(request.getRegistrationType(), request.getUin())) {
			try {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", "Packet Generator Validation successfull");
				RegistrationDTO registrationDTO = createRegistrationDTOObject(request.getUin(),
						request.getRegistrationType(), request.getCenterId(), request.getMachineId());
				packetZipBytes = packetCreationService.create(registrationDTO);
				String rid = registrationDTO.getRegistrationId();
				String packetCreatedDateTime = rid.substring(rid.length() - 14);
				String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
						+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);
				LocalDateTime ldt = LocalDateTime.parse(formattedDate,
						DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
				String creationTime = ldt.toString() + ".000Z";

				packerGeneratorResDto = syncUploadEncryptionService.uploadUinPacket(registrationDTO.getRegistrationId(),
						creationTime, request.getRegistrationType(), packetZipBytes);

				return packerGeneratorResDto;
			} catch (Exception e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(),
						PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION.getMessage(), ExceptionUtils.getStackTrace(e));
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						ExceptionUtils.getStackTrace(e), e);

			}
		} else {
			return dto;
		}
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
	 * @throws RegBaseCheckedException
	 */
	private RegistrationDTO createRegistrationDTOObject(String uin, String registrationType, String centerId,
			String machineId) throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setDemographicDTO(getDemographicDTO(uin));
		RegistrationMetaDataDTO registrationMetaDataDTO = getRegistrationMetaDataDTO(registrationType, uin, centerId,
				machineId);
		String registrationId = generateRegistrationId(registrationMetaDataDTO.getCenterId(),
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

	private String generateRegistrationId(String centerId, String machineId) throws RegBaseCheckedException {

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(centerId);
		pathsegments.add(machineId);
		String rid = null;
		ResponseWrapper<?> responseWrapper;
		JSONObject ridJson;
		ObjectMapper mapper = new ObjectMapper();
		try {

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "PacketGeneratorServiceImpl::generateRegistrationId():: RIDgeneration Api call started");
			responseWrapper = (ResponseWrapper<?>) restClientService.getApi(ApiName.RIDGENERATION, pathsegments, "", "",
					ResponseWrapper.class);
			if (responseWrapper.getErrors() == null) {
				ridJson = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()), JSONObject.class);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"\"PacketGeneratorServiceImpl::generateRegistrationId():: RIDgeneration Api call  ended with response data : "
								+ JsonUtil.objectMapperObjectToJson(ridJson));
				rid = (String) ridJson.get("rid");

			} else {
				List<ErrorDTO> error = responseWrapper.getErrors();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"\"PacketGeneratorServiceImpl::generateRegistrationId():: RIDgeneration Api call  ended with response data : "
								+ error.get(0).getMessage());
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.get(0).getMessage(), new Throwable());
			}

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getMessage(), e);
			}
		} catch (IOException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getMessage(), e);
		}
		return rid;
	}
}
