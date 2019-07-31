package io.mosip.registration.processor.request.handler.service.impl;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.processor.request.handler.service.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.processor.request.handler.service.mapper.CustomObjectMapper.MAPPER_FACADE;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.util.ServerUtil;
import io.mosip.registration.processor.request.handler.service.PacketCreationService;
import io.mosip.registration.processor.request.handler.service.builder.AuditRequestBuilder;
import io.mosip.registration.processor.request.handler.service.builder.Builder;
import io.mosip.registration.processor.request.handler.service.constants.RegistrationConstants;
import io.mosip.registration.processor.request.handler.service.dto.AuditDTO;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationDTO;
import io.mosip.registration.processor.request.handler.service.dto.json.DemographicSequence;
import io.mosip.registration.processor.request.handler.service.dto.json.FieldValue;
import io.mosip.registration.processor.request.handler.service.dto.json.FieldValueArray;
import io.mosip.registration.processor.request.handler.service.dto.json.HashSequence;
import io.mosip.registration.processor.request.handler.service.dto.json.PacketMetaInfo;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.service.external.ZipCreationService;
import io.mosip.registration.processor.request.handler.service.utils.HMACGeneration;

/**
 * Class for creating the Resident Registration
 * 
 * @author Sowmya
 * @since 1.0.0
 *
 */
@Service
public class PacketCreationServiceImpl implements PacketCreationService {

	@Autowired
	private ZipCreationService zipCreationService;

	@Autowired
	private IdObjectValidator idObjectSchemaValidator;
	
	@Autowired
	private Environment environment;

	private String creationTime = null;

	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketCreationServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PacketCreationService#create(io.mosip.
	 * registration.dto.RegistrationDTO)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public byte[] create(final RegistrationDTO registrationDTO) throws RegBaseCheckedException, IdObjectValidationFailedException, IdObjectIOException, JsonParseException, JsonMappingException, IOException {
		String rid = registrationDTO.getRegistrationId();
		try {

			String loggerMessage = "Byte array of %s file generated successfully";

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rid, "PacketCreationServiceImpl ::create()::entry");

			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

			// Generating Demographic JSON as byte array
			String idJsonAsString = javaObjectToJsonString(registrationDTO.getDemographicDTO().getDemographicInfoDTO());
			ObjectMapper mapper=new ObjectMapper();
			JSONObject idObject=mapper.readValue(idJsonAsString, JSONObject.class);
			idObjectSchemaValidator.validateIdObject(idObject,IdObjectValidatorSupportedOperations.UPDATE_UIN);
			filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, idJsonAsString.getBytes());

			AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
			// Getting Host IP Address and Name
			String hostIP = null;
			String hostName = null;
			try {
				hostIP = InetAddress.getLocalHost().getHostAddress();
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException unknownHostException) {

				hostIP = ServerUtil.getServerUtilInstance().getServerIp();
				hostName = ServerUtil.getServerUtilInstance().getServerName();
			}
			auditRequestBuilder.setActionTimeStamp(LocalDateTime.now(ZoneOffset.UTC))
					.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC)).setUuid("")
					.setApplicationId(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_ID))
					.setApplicationName(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_NAME))
					.setCreatedBy("Packet_Generator").setDescription("Packet uploaded successfully")
					.setEventId("RPR_405").setEventName("packet uploaded").setEventType("USER").setHostIp(hostIP)
					.setHostName(hostName).setId(rid).setIdType("REGISTRATION_ID").setModuleId("REG - MOD - 119")
					.setModuleName("Packet Generator").setSessionUserId("mosip").setSessionUserName("Registration");
			AuditDTO auditDto = auditRequestBuilder.build();

			filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(auditDto).getBytes());
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rid, String.format(loggerMessage, RegistrationConstants.AUDIT_JSON_FILE));

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(new DemographicSequence(new LinkedList<>()),
					new LinkedList<>());
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME,
					HMACGeneration.generatePacketDTOHash(registrationDTO, filesGeneratedForPacket, hashSequence));

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rid, String.format(loggerMessage, RegistrationConstants.PACKET_DATA_HASH_FILE_NAME));

			// Generating packet_osi_hash text file as byte array
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME, HMACGeneration
					.generatePacketOSIHash(filesGeneratedForPacket, hashSequence.getOsiDataHashSequence()));

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rid, String.format(loggerMessage, RegistrationConstants.PACKET_OSI_HASH_FILE_NAME));

			// Generating Packet Meta-Info JSON as byte array
			PacketMetaInfo packetInfo = MAPPER_FACADE.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");
			List<FieldValue> metadata = packetInfo.getIdentity().getMetaData();
			for (FieldValue field : metadata) {
				if (field.getLabel().equalsIgnoreCase(JsonConstant.CREATIONDATE)) {
					creationTime = field.getValue();
				}

			}
			// Add HashSequence
			packetInfo.getIdentity().setHashSequence1(buildHashSequence(hashSequence));
			List<String> hashsequence2List = new ArrayList<String>();
			hashsequence2List.add("audit");
			// Add HashSequence for packet_osi_data
			packetInfo.getIdentity()
					.setHashSequence2(
							(List<FieldValueArray>) Builder.build(ArrayList.class)
									.with(values -> values.add(Builder.build(FieldValueArray.class)
											.with(field -> field.setLabel("otherFiles"))
											.with(field -> field.setValue(hashsequence2List)).get()))
									.get());
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_META_JSON_NAME,
					javaObjectToJsonString(packetInfo).getBytes());
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rid, String.format(loggerMessage, RegistrationConstants.PACKET_META_JSON_NAME));
			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rid, "PacketCreationServiceImpl ::create()::exit()");
			return packetZipBytes;
		} catch (JsonProcessingException mosipJsonProcessingException) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rid, PlatformErrorMessages.RPR_PGS_JSON_PROCESSING_EXCEPTION.getMessage()
							+ ExceptionUtils.getStackTrace(mosipJsonProcessingException));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_JSON_PROCESSING_EXCEPTION,
					mosipJsonProcessingException);

		} catch (RuntimeException runtimeException) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					rid, PlatformErrorMessages.RPR_SYS_SERVER_ERROR.getMessage()
							+ ExceptionUtils.getStackTrace(runtimeException));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_SYS_SERVER_ERROR, runtimeException);

		}
	}

	private List<FieldValueArray> buildHashSequence(final HashSequence hashSequence) {
		List<FieldValueArray> hashSequenceList = new LinkedList<>();
		// Add Sequence of Applicant Biometric
		FieldValueArray fieldValueArray = new FieldValueArray();

		// Add Sequence of Applicant Demographic
		fieldValueArray.setLabel("applicantDemographicSequence");
		fieldValueArray.setValue(hashSequence.getDemographicSequence().getApplicant());
		hashSequenceList.add(fieldValueArray);

		return hashSequenceList;
	}

	@Override
	public String getCreationTime() {

		return creationTime;
	}

}
