package io.mosip.registration.processor.request.handler.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.request.handler.service.PacketCreationService;
import io.mosip.registration.processor.request.handler.service.PacketGeneratorService;
import io.mosip.registration.processor.request.handler.service.dto.PackerGeneratorFailureDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationDTO;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationMetaDataDTO;
import io.mosip.registration.processor.request.handler.service.dto.ResidentUpdateDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.upload.SyncUploadEncryptionService;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;

@Service
@Qualifier("residentUpdateService")
public class ResidentUpdateServiceImpl implements PacketGeneratorService<ResidentUpdateDto> {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketCreationServiceImpl.class);
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	RequestHandlerRequestValidator validator;

	@Autowired
	private PacketCreationService packetCreationService;

	@Autowired
	SyncUploadEncryptionService syncUploadEncryptionService;

	@Override
	public PacketGeneratorResDto createPacket(ResidentUpdateDto request) throws RegBaseCheckedException, IOException {
		byte[] packetZipBytes = null;
		PackerGeneratorFailureDto dto = new PackerGeneratorFailureDto();
		if (validator.isValidCenter(request.getCenterId()) && validator.isValidMachine(request.getMachineId())
				&& validator.isValidRegistrationTypeAndUin(request.getIndividualIdType().name(),
						request.getIndividualId())) {

			RegistrationDTO registrationDTO = createRegistrationDTOObject(request.getIndividualId(),
					request.getIndividualIdType().toString(), request.getCenterId(), request.getMachineId());
			try {
				packetZipBytes = packetCreationService.create(registrationDTO, request.getDemographics());
			} catch (IdObjectValidationFailedException | IdObjectIOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String rid = registrationDTO.getRegistrationId();
			String packetCreatedDateTime = rid.substring(rid.length() - 14);
			String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
					+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);
			LocalDateTime ldt = LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
			String creationTime = ldt.toString() + ".000Z";

			PacketGeneratorResDto packerGeneratorResDto = syncUploadEncryptionService.uploadUinPacket(
					registrationDTO.getRegistrationId(), creationTime, request.getIndividualIdType().toString(),
					packetZipBytes);

			return packerGeneratorResDto;

		} else {
			return dto;

		}
	}

	private RegistrationDTO createRegistrationDTOObject(String uin, String registrationType, String centerId,
			String machineId) throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		// registrationDTO.setDemographicDTO(getDemographicDTO(uin));
		RegistrationMetaDataDTO registrationMetaDataDTO = getRegistrationMetaDataDTO(registrationType, uin, centerId,
				machineId);
		String registrationId = generateRegistrationId(registrationMetaDataDTO.getCenterId(),
				registrationMetaDataDTO.getMachineId());
		registrationDTO.setRegistrationId(registrationId);
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);
		return registrationDTO;

	}

//	private DemographicDTO getDemographicDTO(String uin) {
//		DemographicDTO demographicDTO = new DemographicDTO();
//		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
//		MoroccoIdentity identity = new MoroccoIdentity();
//		identity.setIdSchemaVersion(1.0);
//		identity.setUin(new BigInteger(uin));
//		demographicInfoDTO.setIdentity(identity);
//		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);
//		return demographicDTO;
//	}

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
