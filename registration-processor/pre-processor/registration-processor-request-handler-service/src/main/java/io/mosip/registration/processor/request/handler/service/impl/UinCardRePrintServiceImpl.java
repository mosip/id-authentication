package io.mosip.registration.processor.request.handler.service.impl;

import java.io.IOException;
import java.math.BigInteger;
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

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.CardType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
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

	/** The env. */
	@Autowired
	private Environment env;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The packet creation service. */
	@Autowired
	private PacketCreationService packetCreationService;

	/** The sync upload encryption service. */
	@Autowired
	SyncUploadEncryptionService syncUploadEncryptionService;

	/** The validator. */
	@Autowired
	private RequestHandlerRequestValidator validator;

	/** The utilities. */
	@Autowired
	Utilities utilities;

	/** The vid type. */
	@Value("${registration.processor.id.repo.vidType}")
	private String vidType;

	/** The Constant VID_CREATE_ID. */
	public static final String VID_CREATE_ID = "registration.processor.id.repo.generate";

	/** The Constant REG_PROC_APPLICATION_VERSION. */
	public static final String REG_PROC_APPLICATION_VERSION = "registration.processor.id.repo.vidVersion";

	/** The Constant DATETIME_PATTERN. */
	public static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The Constant UIN. */
	public static final String UIN = "UIN";

	/** The Constant VID. */
	public static final String VID = "VID";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(UinCardRePrintServiceImpl.class);

	/**
	 * Creates the packet.
	 *
	 * @param uinCardRePrintRequestDto
	 *            the uin card re print request dto
	 * @return the packet generator res dto
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public PacketGeneratorResDto createPacket(UinCardRePrintRequestDto uinCardRePrintRequestDto)
			throws RegBaseCheckedException, IOException {

		String uin = null;
		String vid = null;
		byte[] packetZipBytes = null;
		PacketGeneratorResDto packetGeneratorResDto = new PacketGeneratorResDto();
		validator.validate(uinCardRePrintRequestDto.getRequesttime(), uinCardRePrintRequestDto.getId(),
				uinCardRePrintRequestDto.getVersion());
		if (validator.isValidCenter(uinCardRePrintRequestDto.getRequest().getCenterId())
				&& validator.isValidMachine(uinCardRePrintRequestDto.getRequest().getMachineId())
				&& validator.isValidRePrintRegistrationType(uinCardRePrintRequestDto.getRequest().getRegistrationType())
				&& validator.isValidIdType(uinCardRePrintRequestDto.getRequest().getIdType())
				&& validator.isValidCardType(uinCardRePrintRequestDto.getRequest().getCardType())
				&& isValidUinVID(uinCardRePrintRequestDto)) {

			try {
				String cardType = uinCardRePrintRequestDto.getRequest().getCardType();
				String regType = uinCardRePrintRequestDto.getRequest().getRegistrationType();

				if (uinCardRePrintRequestDto.getRequest().getIdType().equalsIgnoreCase(UIN))
					uin = uinCardRePrintRequestDto.getRequest().getId();
				else
					vid = uinCardRePrintRequestDto.getRequest().getId();

				if (cardType.equalsIgnoreCase(CardType.MASKED_UIN.toString()) && vid == null) {

					VidRequestDto vidRequestDto = new VidRequestDto();
					RequestWrapper<VidRequestDto> request = new RequestWrapper<>();
					VidResponseDTO response = new VidResponseDTO();
					vidRequestDto.setUIN(uin);
					vidRequestDto.setVidType("Temporary");
					request.setId(env.getProperty(VID_CREATE_ID));
					request.setRequest(vidRequestDto);
					DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
					LocalDateTime localdatetime = LocalDateTime
							.parse(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)), format);
					request.setRequesttime(localdatetime);
					request.setVersion(env.getProperty(REG_PROC_APPLICATION_VERSION));

					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "",
							"UinCardRePrintServiceImpl::createPacket():: post CREATEVID service call started with request data : "
									+ JsonUtil.objectMapperObjectToJson(vidRequestDto));

					response = (VidResponseDTO) restClientService.postApi(ApiName.CREATEVID, "", "", request,
							VidResponseDTO.class);

					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "",
							"UinCardRePrintServiceImpl::createPacket():: post CREATEVID service call ended successfully");

					if (!response.getErrors().isEmpty()) {
						throw new VidCreationException(PlatformErrorMessages.RPR_PGS_VID_EXCEPTION.getMessage(),
								"VID creation exception");

					} else {
						vid = response.getResponse().getVid();
					}

				}
				if (uin == null) {
					uin = utilities.getUinByVid(vid);
				}

				RegistrationDTO registrationDTO = createRegistrationDTOObject(uin,
						uinCardRePrintRequestDto.getRequest().getRegistrationType(),
						uinCardRePrintRequestDto.getRequest().getCenterId(),
						uinCardRePrintRequestDto.getRequest().getMachineId(), vid, cardType);
				packetZipBytes = packetCreationService.create(registrationDTO);
				String rid = registrationDTO.getRegistrationId();
				String packetCreatedDateTime = rid.substring(rid.length() - 14);
				String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
						+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);
				LocalDateTime ldt = LocalDateTime.parse(formattedDate,
						DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
				String creationTime = ldt.toString() + ".000Z";

				if (utilities.linkRegIdWrtUin(rid, uin))
					packetGeneratorResDto = syncUploadEncryptionService.uploadUinPacket(
							registrationDTO.getRegistrationId(), creationTime, regType, packetZipBytes);
				else
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), rid,
							"UinCardRePrintServiceImpl::createPacket():: RID link to UIN failed");
			} catch (Exception e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(),
						PlatformErrorMessages.RPR_RHS_REG_BASE_EXCEPTION.getMessage(), ExceptionUtils.getStackTrace(e));
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_RHS_REG_BASE_EXCEPTION,
						ExceptionUtils.getStackTrace(e), e);

			}

		}
		return packetGeneratorResDto;
	}

	/**
	 * Creates the registration DTO object.
	 *
	 * @param uin
	 *            the uin
	 * @param registrationType
	 *            the registration type
	 * @param centerId
	 *            the center id
	 * @param machineId
	 *            the machine id
	 * @param vid
	 *            the vid
	 * @param cardType
	 *            the card type
	 * @return the registration DTO
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
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

	/**
	 * Gets the registration meta data DTO.
	 *
	 * @param uin
	 *            the uin
	 * @param registrationType
	 *            the registration type
	 * @param centerId
	 *            the center id
	 * @param machineId
	 *            the machine id
	 * @param vid
	 *            the vid
	 * @param cardType
	 *            the card type
	 * @return the registration meta data DTO
	 */
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
	 * Generate registration id.
	 *
	 * @param centerId
	 *            the center id
	 * @param machineId
	 *            the machine id
	 * @return the string
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
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
					"", "UinCardRePrintServiceImpl::generateRegistrationId():: RIDgeneration Api call started");
			responseWrapper = (ResponseWrapper<?>) restClientService.getApi(ApiName.RIDGENERATION, pathsegments, "", "",
					ResponseWrapper.class);
			if (responseWrapper.getErrors() == null) {
				ridJson = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()), JSONObject.class);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"\"UinCardRePrintServiceImpl::generateRegistrationId():: RIDgeneration Api call  ended with response data : "
								+ JsonUtil.objectMapperObjectToJson(ridJson));
				rid = (String) ridJson.get("rid");

			} else {
				List<ErrorDTO> error = responseWrapper.getErrors();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"\"UinCardRePrintServiceImpl::generateRegistrationId():: RIDgeneration Api call  ended with response data : "
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

	/**
	 * Checks if is valid uin VID.
	 *
	 * @param uinCardRePrintRequestDto
	 *            the uin card re print request dto
	 * @return true, if is valid uin VID
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public boolean isValidUinVID(UinCardRePrintRequestDto uinCardRePrintRequestDto) throws RegBaseCheckedException {
		boolean isValid = false;
		if (uinCardRePrintRequestDto.getRequest().getIdType().equalsIgnoreCase(UIN)) {
			isValid = validator.isValidUin(uinCardRePrintRequestDto.getRequest().getId());
		} else if (uinCardRePrintRequestDto.getRequest().getIdType().equalsIgnoreCase(VID)) {
			isValid = validator.isValidVid(uinCardRePrintRequestDto.getRequest().getId());
		}
		return isValid;
	}
}
