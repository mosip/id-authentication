package io.mosip.registration.processor.request.handler.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.RequestWrapper;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.vid.VidRequestDto;
import io.mosip.registration.processor.core.packet.dto.vid.VidResponseDTO;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.request.handler.service.PacketCreationService;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationDTO;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationMetaDataDTO;
import io.mosip.registration.processor.request.handler.service.dto.UinCardRePrintRequestDto;
import io.mosip.registration.processor.request.handler.service.dto.demographic.DemographicDTO;
import io.mosip.registration.processor.request.handler.service.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.processor.request.handler.service.dto.demographic.MoroccoIdentity;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.service.exception.VidCreationException;
import io.mosip.registration.processor.request.handler.upload.SyncUploadEncryptionService;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;

/**
 * The Class ResidentServiceRePrintServiceImpl.
 */
@Service
public class UinCardRePrintServiceImpl {

	@Autowired
	private Environment env;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private PacketCreationService packetCreationService;

	@Autowired
	SyncUploadEncryptionService syncUploadEncryptionService;
	
	/** The validator. */
	@Autowired
	private RequestHandlerRequestValidator validator;

	@Autowired
	Utilities utilities;

	@Value("${registration.processor.id.repo.vidType}")
	private String vidType;

	public static final String VID_CREATE_ID = "registration.processor.id.repo.generate";

	public static final String REG_PROC_APPLICATION_VERSION = "registration.processor.id.repo.vidVersion";

	public static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	private static Logger regProcLogger = RegProcessorLogger.getLogger(UinCardRePrintServiceImpl.class);

	@SuppressWarnings("unchecked")
	private PacketGeneratorResDto check(String requestUin, String requestVid, String cardType, String registrationId)
			throws ApisResourceAccessException, IOException, RegBaseCheckedException, PacketDecryptionFailureException,
			io.mosip.kernel.core.exception.IOException,
			io.mosip.registration.processor.packet.storage.exception.VidCreationException,
			IdObjectValidationFailedException, IdObjectIOException, ParseException, VidCreationException {

		PacketGeneratorResDto packerGeneratorResDto = null;
		String uin = requestUin;
		String vid = requestVid;
		byte[] packetZipBytes = null;

		if (cardType == "vid" && vid == null) {

			VidRequestDto vidRequestDto = new VidRequestDto();
			RequestWrapper<VidRequestDto> request = new RequestWrapper<>();
			ResponseWrapper<VidResponseDTO> response = new ResponseWrapper<>();
			vidRequestDto.setUIN(requestUin);
			vidRequestDto.setVidType(vidType);
			request.setId(env.getProperty(VID_CREATE_ID));
			request.setRequest(vidRequestDto);
			DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
			LocalDateTime localdatetime = LocalDateTime
					.parse(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)), format);
			request.setRequesttime(localdatetime);
			request.setVersion(env.getProperty(REG_PROC_APPLICATION_VERSION));

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Stage::methodName():: post CREATEVID service call started with request data : "
							+ JsonUtil.objectMapperObjectToJson(vidRequestDto));

			response = (ResponseWrapper<VidResponseDTO>) restClientService.postApi(ApiName.CREATEVID, "", "", request,
					ResponseWrapper.class);

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Stage::methodName():: post CREATEVID service call ended successfully");

			if (!response.getErrors().isEmpty()) {
				throw new VidCreationException(PlatformErrorMessages.RPR_PGS_VID_EXCEPTION.getMessage(),
						"VID creation exception");

			} else {
				vid = response.getResponse().getVid().toString();
			}

		} else if (cardType == "uin" && uin == null) {
			utilities.getUinByVid(vid);
		}

		RegistrationDTO registrationDTO = createRegistrationDTOObject(uin, "", "", "", vid, cardType);
		packetZipBytes = packetCreationService.create(registrationDTO);
		String rid = registrationDTO.getRegistrationId();
		String packetCreatedDateTime = rid.substring(rid.length() - 14);
		String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
				+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);
		LocalDateTime ldt = LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
		String creationTime = ldt.toString() + ".000Z";

		packerGeneratorResDto = syncUploadEncryptionService.uploadUinPacket(registrationDTO.getRegistrationId(),
				creationTime, "---regtype---", packetZipBytes);

		return packerGeneratorResDto;
	}

	private RegistrationDTO createRegistrationDTOObject(String uin, String registrationType, String centerId,
			String machineId, String vid, String cardType) throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setDemographicDTO(getDemographicDTO(uin));
		RegistrationMetaDataDTO registrationMetaDataDTO = getRegistrationMetaDataDTO(uin, registrationType, centerId,
				machineId, vid, cardType);
		String registrationId = generateRegistrationId(registrationMetaDataDTO.getCenterId(),
				registrationMetaDataDTO.getMachineId());
		registrationDTO.setRegistrationId(registrationId);
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);
		return registrationDTO;

	}

	private RegistrationMetaDataDTO getRegistrationMetaDataDTO(String uin, String registrationType, String centerId,
			String machineId, String vid, String cardType) {
		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();

		registrationMetaDataDTO.setCenterId(centerId);
		registrationMetaDataDTO.setMachineId(machineId);
		registrationMetaDataDTO.setRegistrationCategory(registrationType);
		registrationMetaDataDTO.setUin(uin);
		registrationMetaDataDTO.setVid(vid);
		registrationMetaDataDTO.setCardType(cardType);
		return registrationMetaDataDTO;

	}

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

	private String generateRegistrationId(String centerId, String machineId) throws RegBaseCheckedException {

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(centerId);
		pathsegments.add(machineId);
		String rid = null;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
		JSONObject ridJson = new JSONObject();
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
	
	public PacketGeneratorResDto methodToCall(UinCardRePrintRequestDto uinCardRePrintRequestDto)
			throws RegBaseCheckedException, IOException {
		PacketGeneratorResDto packetGeneratorResDto = new PacketGeneratorResDto();
		validator.validate(uinCardRePrintRequestDto.getRequesttime(), uinCardRePrintRequestDto.getId(),
				uinCardRePrintRequestDto.getVersion());
		myMethod(uinCardRePrintRequestDto);
		return packetGeneratorResDto;
	}
	
	public void myMethod(UinCardRePrintRequestDto uinCardRePrintRequestDto)
			throws RegBaseCheckedException, IOException {
		if (validator.isValidCenter(uinCardRePrintRequestDto.getRequest().getCenterId())
				&& validator.isValidMachine(uinCardRePrintRequestDto.getRequest().getMachineId())
				&& validator.isValidRegistrationType(uinCardRePrintRequestDto.getRequest().getRegistrationType())
				&& validator.isValidIdType(uinCardRePrintRequestDto.getRequest().getIdType())
				&& validator.isValidCardType(uinCardRePrintRequestDto.getRequest().getCardType())
				&& isValidUinVID(uinCardRePrintRequestDto)) {			
		}
	}

	public boolean isValidUinVID(UinCardRePrintRequestDto uinCardRePrintRequestDto) throws RegBaseCheckedException {
		boolean isValid = false;
		if (uinCardRePrintRequestDto.getRequest().getIdType().equalsIgnoreCase("UIN")) {
			isValid = validator.isValidUin(uinCardRePrintRequestDto.getRequest().getId());
		}else if(uinCardRePrintRequestDto.getRequest().getIdType().equalsIgnoreCase("VID")) {
			isValid = validator.isValidVid(uinCardRePrintRequestDto.getRequest().getId());
		}
		return isValid;
	}
}
