package io.mosip.registration.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.exception.MosipJsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dto.AuditDTO;
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
import io.mosip.registration.service.PacketCreationService;
import io.mosip.registration.util.checksum.CheckSumUtil;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.service.ZipCreationService;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;
import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_CREATION;
import static io.mosip.registration.constants.RegistrationConstants.REGISTRATION_ID;

/**
 * Class for creating the Resident Registration
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class PacketCreationServiceImpl implements PacketCreationService {

	@Autowired
	private AuditDAO auditDAO;
	@Autowired
	private ZipCreationService zipCreationService;
	private static MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PacketCreationService#create(io.mosip.
	 * registration.dto.RegistrationDTO)
	 */
	@Override
	public byte[] create(RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been called");
		
		try {
			String rid = registrationDTO.getRegistrationId();
			// Fetch unsync'ed audit logs from DB
			// TODO: Commented below line intentionally. Will be updated
			//registrationDTO.setAuditDTOs(MAPPER_FACADE.mapAsList(auditDAO.getAllUnsyncAudits(), AuditDTO.class));

			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> jsonsMap = new HashMap<>();

			// Generating Demographic JSON as byte array
			jsonsMap.put(DEMOGRPAHIC_JSON_NAME,
					javaObjectToJsonString(MAPPER_FACADE.map(registrationDTO.getDemographicDTO(), Demographic.class))
							.getBytes());

			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Demographic Json created successfully");
			auditFactory.audit(AuditEvent.PACKET_DEMO_JSON_CREATED, AppModule.PACKET_CREATOR,
					"Demographic JSON created successfully", REGISTRATION_ID, rid);

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(
					new BiometricSequence(new LinkedList<String>(), new LinkedList<String>()),
					new DemographicSequence(new LinkedList<String>()));
			jsonsMap.put(RegistrationConstants.HASHING_JSON_NAME, HMACGeneration.generatePacketDTOHash(registrationDTO,
					jsonsMap.get(DEMOGRPAHIC_JSON_NAME), hashSequence));

			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "HMAC File generated successfully");
			auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, AppModule.PACKET_CREATOR,
					"Packet HMAC File created successfully", REGISTRATION_ID, rid);

			// Generating Packet Meta-Info JSON as byte array
			PacketInfo packetInfo = MAPPER_FACADE.map(registrationDTO, PacketInfo.class);
			// Generate HMAC for RegistrationId
			packetInfo.getMetaData().setRegistrationIdHash(HMACUtils.digestAsPlainText(
					HMACUtils.generateHash(packetInfo.getMetaData().getRegistrationId().getBytes())));
			packetInfo.setHashSequence(hashSequence);
			packetInfo.setCheckSumMap(CheckSumUtil.getCheckSumMap());
			packetInfo.setOsiData(MAPPER_FACADE.convert(registrationDTO, OSIData.class, "osiDataConverter"));
			jsonsMap.put(RegistrationConstants.PACKET_META_JSON_NAME, javaObjectToJsonString(packetInfo).getBytes());

			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, AppModule.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", REGISTRATION_ID, rid);

			// Generating Audit JSON as byte array
			jsonsMap.put(RegistrationConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(MAPPER_FACADE.mapAsList(registrationDTO.getAuditDTOs(), Audit.class))
							.getBytes());

			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, AppModule.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", REGISTRATION_ID, rid);

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = zipCreationService.createPacket(registrationDTO, jsonsMap);

			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been ended");
			auditFactory.audit(AuditEvent.PACKET_INTERNAL_ZIP, AppModule.PACKET_CREATOR,
					"Packet Internal Zip File created successfully", REGISTRATION_ID, rid);

			return packetZipBytes;
		} catch (MosipJsonProcessingException mosipJsonProcessingException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_JSON_PROCESSING_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_JSON_PROCESSING_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_CREATION_EXCEPTION,
					runtimeException.toString());
		}
	}

}
