package io.mosip.registration.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.exception.MosipJsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.constants.RegProcessorExceptionEnum;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.json.demo.Demographic;
import io.mosip.registration.dto.json.metadata.Audit;
import io.mosip.registration.dto.json.metadata.BiometricSequence;
import io.mosip.registration.dto.json.metadata.DemographicSequence;
import io.mosip.registration.dto.json.metadata.HashSequence;
import io.mosip.registration.dto.json.metadata.OSIData;
import io.mosip.registration.dto.json.metadata.PacketInfo;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.checksum.CheckSumUtil;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.util.zip.ZipCreationManager;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;
import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;

/**
 * Class for creating the Resident Registration
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class PacketCreationService {
	
	@Autowired
	private AuditDAO auditDAO;
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/**
	 * Creates the packet
	 * 
	 * @param registrationDTO
	 *            the enrollment data for which packet has to be created
	 * @throws RegBaseCheckedException
	 */
	public byte[] create(RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		String loggerSessionId = "REGISTRATION - PACKET_CREATION - CREATE";
		LOGGER.debug(loggerSessionId, getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Registration Creation had been called");
		try {
			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> jsonsMap = new HashMap<>();

			// Generating Demographic JSON as byte array
			jsonsMap.put(DEMOGRPAHIC_JSON_NAME,
					javaObjectToJsonString(MAPPER_FACADE.map(registrationDTO.getDemographicDTO(), Demographic.class))
							.getBytes());
			LOGGER.debug(loggerSessionId, getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Demographic Json created successfully");
			auditFactory.audit(AuditEventEnum.PACKET_DEMO_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Demographic JSON created successfully", "refId", "refIdType");

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(
					new BiometricSequence(new LinkedList<String>(), new LinkedList<String>(), new LinkedList<String>()),
					new DemographicSequence(new LinkedList<String>(), new LinkedList<String>(),
							new LinkedList<String>()),
					null);
			jsonsMap.put(RegConstants.HASHING_JSON_NAME, HMACGeneration.generatePacketDTOHash(registrationDTO,
					jsonsMap.get(DEMOGRPAHIC_JSON_NAME), hashSequence));
			LOGGER.debug(loggerSessionId, getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"HMAC File generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_HMAC_FILE_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet HMAC File created successfully", "refId", "refIdType");

			// Generating Packet Meta-Info JSON as byte array
			PacketInfo packetInfo = MAPPER_FACADE.map(registrationDTO, PacketInfo.class);
			// Generate HMAC for RegistrationId
			packetInfo.getMetaData().setRegistrationIdHash(HMACUtils.digestAsPlainText(
					HMACUtils.generateHash(packetInfo.getMetaData().getRegistrationId().getBytes())));
			packetInfo.setHashSequence(hashSequence);
			packetInfo.setCheckSumMap(CheckSumUtil.getCheckSumMap());
			packetInfo.setOsiData(MAPPER_FACADE.convert(registrationDTO, OSIData.class, "osiDataConverter"));
			jsonsMap.put(RegConstants.PACKET_META_JSON_NAME, javaObjectToJsonString(packetInfo).getBytes());
			LOGGER.debug(loggerSessionId, getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_META_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", "refId", "refIdType");

			// Generating Audit JSON as byte array
			jsonsMap.put(RegConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(MAPPER_FACADE.mapAsList(auditDAO.getAllAudits(), Audit.class))
							.getBytes());
			LOGGER.debug(loggerSessionId, getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_META_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", "refId", "refIdType");

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = ZipCreationManager.createPacket(registrationDTO, jsonsMap);
			LOGGER.debug(loggerSessionId, getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Registration Creation had been ended");
			auditFactory.audit(AuditEventEnum.PACKET_INTERNAL_ZIP, AppModuleEnum.PACKET_CREATOR,
					"Packet Internal Zip File created successfully", "refId", "refIdType");
			return packetZipBytes;
		} catch (MosipJsonProcessingException mosipJsonProcessingException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_JSON_PROCESSING_EXCEPTION.getErrorCode(),
					RegProcessorExceptionEnum.REG_JSON_PROCESSING_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_CREATION_EXCEPTION,
					runtimeException.toString());
		}
	}

}
