package io.mosip.registration.processor.request.handler.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.status.util.StatusUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.request.handler.service.PacketCreationService;
import io.mosip.registration.processor.request.handler.service.PacketGeneratorService;
import io.mosip.registration.processor.request.handler.service.dto.PackerGeneratorFailureDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationDTO;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationMetaDataDTO;
import io.mosip.registration.processor.request.handler.service.dto.ResidentIndividialIDType;
import io.mosip.registration.processor.request.handler.service.dto.ResidentUpdateDto;
import io.mosip.registration.processor.request.handler.service.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.processor.request.handler.service.dto.demographic.DemographicDTO;
import io.mosip.registration.processor.request.handler.service.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.upload.SyncUploadEncryptionService;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationType;

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

	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	private static final String PROOF_OF_ADDRESS = "proofOfAddress";
	private static final String PROOF_OF_DOB = "proofOfDOB";
	private static final String PROOF_OF_RELATIONSHIP = "proofOfRelationship";
	private static final String PROOF_OF_IDENTITY = "proofOfIdentity";
	private static final String IDENTITY = "identity";
	private static final String FORMAT = "format";
	private static final String TYPE = "type";
	private static final String VALUE = "value";

	@Override
	public PacketGeneratorResDto createPacket(ResidentUpdateDto request) throws RegBaseCheckedException, IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(),
				request.getIdValue(), "ResidentUpdateServiceImpl::createPacket()");
		byte[] packetZipBytes = null;
		PackerGeneratorFailureDto dto = new PackerGeneratorFailureDto();
		boolean isTransactional = false;
		try {
			if (validator.isValidCenter(request.getCenterId()) && validator.isValidMachine(request.getMachineId())
					&& request.getIdType().equals(ResidentIndividialIDType.UIN)
							? validator.isValidRegistrationTypeAndUin(RegistrationType.RES_UPDATE.toString(),
									request.getIdValue())
							: validator.isValidVid(request.getIdValue())) {

				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(),
						request.getIdValue(),
						"ResidentUpdateServiceImpl::createPacket()::validations for UIN,TYPE,CENTER,MACHINE are successful");

				RegistrationDTO registrationDTO = createRegistrationDTOObject(request.getIdValue(),
						request.getRequestType().toString(), request.getCenterId(), request.getMachineId());

				try {
					String demoJsonString = new String(CryptoUtil.decodeBase64(request.getIdentityJson()));
					JSONObject demoJsonObject = JsonUtil.objectMapperReadValue(demoJsonString, JSONObject.class);

					// set demographic documents
					Map<String, DocumentDetailsDTO> map = new HashMap<>();
					if (request.getProofOfAddress() != null && !request.getProofOfAddress().isEmpty())
						setDemographicDocuments(request.getProofOfAddress(), demoJsonObject, PROOF_OF_ADDRESS, map);
					if (request.getProofOfDateOfBirth() != null && !request.getProofOfDateOfBirth().isEmpty())
						setDemographicDocuments(request.getProofOfAddress(), demoJsonObject, PROOF_OF_DOB, map);
					if (request.getProofOfRelationship() != null && !request.getProofOfRelationship().isEmpty())
						setDemographicDocuments(request.getProofOfAddress(), demoJsonObject, PROOF_OF_RELATIONSHIP,
								map);
					if (request.getProofOfIdentity() != null && !request.getProofOfIdentity().isEmpty())
						setDemographicDocuments(request.getProofOfAddress(), demoJsonObject, PROOF_OF_IDENTITY, map);
					if (map.size() > 0) {
						DemographicDTO dummyDemoDto = new DemographicDTO();
						ApplicantDocumentDTO dummyApplicantDto = new ApplicantDocumentDTO();
						registrationDTO.setDemographicDTO(dummyDemoDto);
						registrationDTO.getDemographicDTO().setApplicantDocumentDTO(dummyApplicantDto);
						registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setDocuments(map);

					}
					registrationDTO.setRegType(RegistrationType.RES_UPDATE.toString());
					packetZipBytes = packetCreationService.create(registrationDTO, demoJsonObject);

					String rid = registrationDTO.getRegistrationId();
					String packetCreatedDateTime = rid.substring(rid.length() - 14);
					String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
							+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);
					LocalDateTime ldt = LocalDateTime.parse(formattedDate,
							DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
					String creationTime = ldt.toString() + ".000Z";

					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationDTO.getRegistrationId(),
							"ResidentUpdateServiceImpl::createPacket()::packet created and sent for sync service");

					PacketGeneratorResDto packerGeneratorResDto = syncUploadEncryptionService.uploadUinPacket(
							registrationDTO.getRegistrationId(), creationTime, request.getRequestType().toString(),
							packetZipBytes);

					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationDTO.getRegistrationId(),
							"ResidentUpdateServiceImpl::createPacket()::packet synched and uploaded");
					isTransactional = true;
					return packerGeneratorResDto;
				} catch (Exception e) {
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(),
							PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION.getMessage(),
							ExceptionUtils.getStackTrace(e));
					if (e instanceof RegBaseCheckedException) {
						throw (RegBaseCheckedException) e;
					}
					throw new RegBaseCheckedException(StatusUtil.UNKNOWN_EXCEPTION_OCCURED, e);

				}

			} else
				return dto;

		} finally {
			String eventId = isTransactional ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactional ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactional ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();
			String message = isTransactional ? StatusUtil.RESIDENT_UPDATE_SUCCES.getMessage()
					: StatusUtil.RESIDENT_UPDATE_FAILED.getMessage();
			String registrationId = dto.getRegistrationId() == null || dto.getRegistrationId().isEmpty()
					? request.getIdValue()
					: dto.getRegistrationId();
			String moduleName = ModuleName.REQUEST_HANDLER_SERVICE.toString();
			String moduleId = isTransactional ? StatusUtil.RESIDENT_UPDATE_SUCCES.getCode()
					: StatusUtil.RESIDENT_UPDATE_FAILED.getCode();
			auditLogRequestBuilder.createAuditRequestBuilder(message, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}
	}

	private void setDemographicDocuments(String documentBytes, JSONObject demoJsonObject, String documentName,
			Map<String, DocumentDetailsDTO> map) {
		JSONObject identityJson = JsonUtil.getJSONObject(demoJsonObject, IDENTITY);
		JSONObject documentJson = JsonUtil.getJSONObject(identityJson, documentName);
		if (documentJson == null)
			return;
		DocumentDetailsDTO docDetailsDto = new DocumentDetailsDTO();
		docDetailsDto.setDocument(CryptoUtil.decodeBase64(documentBytes));
		docDetailsDto.setFormat((String) JsonUtil.getJSONValue(documentJson, FORMAT));
		docDetailsDto.setValue((String) JsonUtil.getJSONValue(documentJson, VALUE));
		docDetailsDto.setType((String) JsonUtil.getJSONValue(documentJson, TYPE));
		map.put(documentName, docDetailsDto);
	}

	private RegistrationDTO createRegistrationDTOObject(String uin, String registrationType, String centerId,
			String machineId) throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		RegistrationMetaDataDTO registrationMetaDataDTO = getRegistrationMetaDataDTO(registrationType, uin, centerId,
				machineId);
		String registrationId = generateRegistrationId(registrationMetaDataDTO.getCenterId(),
				registrationMetaDataDTO.getMachineId());
		registrationDTO.setRegistrationId(registrationId);
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);
		return registrationDTO;

	}

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
