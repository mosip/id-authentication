package io.mosip.registration.processor.packet.service.impl;

import java.io.ByteArrayInputStream;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
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
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.service.PacketCreationService;
import io.mosip.registration.processor.packet.service.PacketGeneratorService;
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
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.packet.upload.service.SyncUploadEncryptionService;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
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
	private ObjectMapper mapper=new ObjectMapper();

	@Autowired
	private Utilities utilities;


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

		byte[] packetZipBytes = null;
		if (isValidCenter(request.getCenterId(), dto) && isValidMachine(request.getMachineId(), dto)
				&& isValidUin(request.getUin(), dto) && isValidRegistrationType(request.getRegistrationType(), dto)) {
			try {
				RegistrationDTO registrationDTO = createRegistrationDTOObject(request.getUin(),
						request.getRegistrationType(), request.getCenterId(), request.getMachineId());
				packetZipBytes = packetCreationService.create(registrationDTO);
				String rid = registrationDTO.getRegistrationId();
				String packetCreatedDateTime = rid.substring(rid.length() - 14);
				String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);
				LocalDateTime ldt = LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")); 
				String creationTime = ldt.toString()+".000Z";

				packerGeneratorResDto = syncUploadEncryptionService.uploadUinPacket(registrationDTO.getRegistrationId(),
						creationTime, request.getRegistrationType(),packetZipBytes);
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
		if (registrationType!=null && (registrationType.equalsIgnoreCase(RegistrationType.ACTIVATED.toString())
				|| registrationType.equalsIgnoreCase(RegistrationType.DEACTIVATED.toString()))) {
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
			JSONObject jsonObject =  utilities.retrieveIdrepoJson(Long.parseLong(uin));
			if (isValidUIN && jsonObject!=null) {
				isValidUIN = true;
			} else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, "UIN is not valid",
						new Throwable());

			}
		} catch (InvalidIDException ex) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, ex.getErrorText(), ex);

		} catch (IdRepoAppException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getErrorText(), e);
		} catch (NumberFormatException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e);
		} catch (ApisResourceAccessException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getErrorText(), e);
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
	 * @throws RegBaseCheckedException 
	 */
	private RegistrationDTO createRegistrationDTOObject(String uin, String registrationType, String centerId,
			String machineId) throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setDemographicDTO(getDemographicDTO(uin));
		RegistrationMetaDataDTO registrationMetaDataDTO = getRegistrationMetaDataDTO(registrationType, uin, centerId,
				machineId);
		String registrationId =generateRegistrationId(registrationMetaDataDTO.getCenterId(),registrationMetaDataDTO.getMachineId());
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
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	private boolean isValidCenter(String centerId, PackerGeneratorFailureDto dto) throws RegBaseCheckedException, IOException {
		boolean isValidCenter = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(centerId);
		pathsegments.add(primaryLanguagecode);
		RegistrationCenterResponseDto rcpdto;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
		try {
			if(centerId!=null && !centerId.isEmpty()) {
			responseWrapper = (ResponseWrapper<?>) restClientService.getApi(ApiName.CENTERDETAILS, pathsegments, "",
					"", ResponseWrapper.class);
			rcpdto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()), RegistrationCenterResponseDto.class);

			if (responseWrapper.getErrors() == null && !rcpdto.getRegistrationCenters().isEmpty()) {
				isValidCenter = true;
			} else {
				List<ErrorDTO> error = responseWrapper.getErrors();


				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.get(0).getMessage(), new Throwable());
			  }
			}else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						 "Center id is mandatory", new Throwable());
			}
		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				List<ErrorDTO> error = responseWrapper.getErrors();
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.get(0).getMessage(), e);

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
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	private boolean isValidMachine(String machine, PackerGeneratorFailureDto dto) throws RegBaseCheckedException, IOException {
		boolean isValidMachine = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(machine);
		pathsegments.add(primaryLanguagecode);
		MachineResponseDto machinedto;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
		try {

			if(machine!=null && !machine.isEmpty())  {
			responseWrapper = (ResponseWrapper<?>) restClientService.getApi(ApiName.MACHINEDETAILS, pathsegments, "", "",
					ResponseWrapper.class);
			machinedto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()), MachineResponseDto.class);

			if (responseWrapper.getErrors() == null && !machinedto.getMachines().isEmpty()) {
				isValidMachine = true;
			} else {
				List<ErrorDTO> error = responseWrapper.getErrors();
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.get(0).getMessage(), new Throwable());
			}
			}else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						 "Machine id is mandatory", new Throwable());
			}

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				List<ErrorDTO> error = responseWrapper.getErrors();
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.get(0).getMessage(), e);

			}

		}
		return isValidMachine;

	}
	
	
	private String generateRegistrationId(String centerId,String machineId) throws RegBaseCheckedException {

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(centerId);
		pathsegments.add(machineId);
		String rid=null;
		ResponseWrapper<?> responseWrapper;
		JSONObject ridJson;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
		JSONObject ridJson=new JSONObject();
		ObjectMapper mapper=new ObjectMapper();
		try {
			responseWrapper = (ResponseWrapper<?>) restClientService.getApi(ApiName.RIDGENERATION, pathsegments, "",
					"", ResponseWrapper.class);
			if (responseWrapper.getErrors() == null) {
				ridJson = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()), JSONObject.class);
				rid=(String) ridJson.get("rid");

			} else {
				List<ErrorDTO> error = responseWrapper.getErrors();
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.get(0).getMessage(), new Throwable());
			}

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						e.getMessage(), e);
			}
		}catch (IOException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
					e.getMessage(), e);
		}
		return rid;
	}
}
