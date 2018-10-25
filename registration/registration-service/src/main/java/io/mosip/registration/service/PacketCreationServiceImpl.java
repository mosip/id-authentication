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
import org.springframework.stereotype.Service;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.constants.RegProcessorExceptionEnum;
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
import io.mosip.registration.util.checksum.CheckSumUtil;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.util.zip.ZipCreationService;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;
import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_CREATION;
import static io.mosip.registration.constants.RegConstants.REGISTRATION_ID;

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

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.PacketCreationService#create(io.mosip.registration.dto.RegistrationDTO)
	 */
	public byte[] create(RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
				"Registration Creation had been called");
		try {
			String rid = registrationDTO.getRegistrationId();
			// Fetch unsync'ed audit logs from DB
			registrationDTO.setAuditDTOs(MAPPER_FACADE.mapAsList(auditDAO.getAllUnsyncAudits(), AuditDTO.class));
			
			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> jsonsMap = new HashMap<>();

			// Generating Demographic JSON as byte array
			jsonsMap.put(DEMOGRPAHIC_JSON_NAME,
					javaObjectToJsonString(MAPPER_FACADE.map(registrationDTO.getDemographicDTO(), Demographic.class))
							.getBytes());
			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Demographic Json created successfully");
			auditFactory.audit(AuditEventEnum.PACKET_DEMO_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Demographic JSON created successfully", REGISTRATION_ID, rid);

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(
					new BiometricSequence(new LinkedList<String>(), new LinkedList<String>()),
					new DemographicSequence(new LinkedList<String>()));
			jsonsMap.put(RegConstants.HASHING_JSON_NAME, HMACGeneration.generatePacketDTOHash(registrationDTO,
					jsonsMap.get(DEMOGRPAHIC_JSON_NAME), hashSequence));
			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"HMAC File generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_HMAC_FILE_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet HMAC File created successfully", REGISTRATION_ID, rid);

			// Generating Packet Meta-Info JSON as byte array
			PacketInfo packetInfo = MAPPER_FACADE.map(registrationDTO, PacketInfo.class);
			// Generate HMAC for RegistrationId
			packetInfo.getMetaData().setRegistrationIdHash(HMACUtils.digestAsPlainText(
					HMACUtils.generateHash(packetInfo.getMetaData().getRegistrationId().getBytes())));
			packetInfo.setHashSequence(hashSequence);
			packetInfo.setCheckSumMap(CheckSumUtil.getCheckSumMap());
			packetInfo.setOsiData(MAPPER_FACADE.convert(registrationDTO, OSIData.class, "osiDataConverter"));
			jsonsMap.put(RegConstants.PACKET_META_JSON_NAME, javaObjectToJsonString(packetInfo).getBytes());
			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_META_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", REGISTRATION_ID, rid);

			// Generating Audit JSON as byte array
			jsonsMap.put(RegConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(MAPPER_FACADE.mapAsList(registrationDTO.getAuditDTOs(), Audit.class))
							.getBytes());
			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_META_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", REGISTRATION_ID, rid);

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = ZipCreationService.createPacket(registrationDTO, jsonsMap);
			logger.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Registration Creation had been ended");
			auditFactory.audit(AuditEventEnum.PACKET_INTERNAL_ZIP, AppModuleEnum.PACKET_CREATOR,
					"Packet Internal Zip File created successfully", REGISTRATION_ID, rid);
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
