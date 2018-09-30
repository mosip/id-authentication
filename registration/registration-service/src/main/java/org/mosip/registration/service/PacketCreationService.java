package org.mosip.registration.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.audit.AuditFactory;
import org.mosip.registration.constants.AppModuleEnum;
import org.mosip.registration.constants.AuditEventEnum;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.constants.RegProcessorExceptionEnum;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.dto.json.demo.Demographic;
import org.mosip.registration.dto.json.metadata.Audit;
import org.mosip.registration.dto.json.metadata.HashSequence;
import org.mosip.registration.dto.json.metadata.OSIData;
import org.mosip.registration.dto.json.metadata.PacketInfo;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.util.checksum.CheckSumUtil;
import org.mosip.registration.util.hmac.HMACGeneration;
import org.mosip.registration.util.zip.ZipCreationManager;
import org.mosip.kernel.core.utils.exception.MosipJsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mosip.kernel.core.utils.JsonUtil.javaObjectToJsonString;
import static org.mosip.registration.constants.RegConstants.DEMOGRPAHIC_JSON_NAME;
import static org.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;
import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME; 
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;


/**
 * Class for creating the Resident Registration
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class PacketCreationService {

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
		LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Registration Creation had been called");
		try {
			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> jsonsMap = new HashMap<>();

			// Generating Demographic JSON as byte array
			jsonsMap.put(DEMOGRPAHIC_JSON_NAME,
					javaObjectToJsonString(
							MAPPER_FACADE.map(registrationDTO.getDemographicDTO(), Demographic.class))
									.getBytes());
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Demographic Json created successfully");
			auditFactory.audit(AuditEventEnum.PACKET_DEMO_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Demographic JSON created successfully", "refId", "refIdType");

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
			jsonsMap.put(RegConstants.HASHING_JSON_NAME, HMACGeneration.generatePacketDTOHash(
					registrationDTO, jsonsMap.get(DEMOGRPAHIC_JSON_NAME), hashSequence));
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "HMAC File generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_HMAC_FILE_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet HMAC File created successfully", "refId", "refIdType");

			// Generating Packet Meta-Info JSON as byte array
			PacketInfo packetInfo = MAPPER_FACADE.map(registrationDTO, PacketInfo.class);
			packetInfo.setHashSequence(hashSequence);
			packetInfo.setCheckSumMap(CheckSumUtil.getCheckSumMap());
			packetInfo.setOsiData(MAPPER_FACADE.convert(registrationDTO, OSIData.class, "osiDataConverter"));
			jsonsMap.put(RegConstants.PACKET_META_JSON_NAME, javaObjectToJsonString(packetInfo).getBytes());
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_META_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", "refId", "refIdType");

			// Generating Audit JSON as byte array
			jsonsMap.put(RegConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(
							MAPPER_FACADE.mapAsList(registrationDTO.getAuditDTOs(), Audit.class))
									.getBytes());
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEventEnum.PACKET_META_JSON_CREATED, AppModuleEnum.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", "refId", "refIdType");

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = ZipCreationManager.createPacket(registrationDTO, jsonsMap);
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Registration Creation had been ended");
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
