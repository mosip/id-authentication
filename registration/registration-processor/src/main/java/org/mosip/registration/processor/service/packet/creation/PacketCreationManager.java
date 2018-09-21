package org.mosip.registration.processor.service.packet.creation;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.mosip.kernel.core.audit.builder.AuditRequestBuilder;
import org.mosip.kernel.core.audit.handler.AuditHandler;
import org.mosip.kernel.core.audit.request.AuditRequest;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.mosip.kernel.core.utils.exception.MosipJsonProcessingException;
import org.mosip.registration.processor.exception.RegBaseCheckedException;
import org.mosip.registration.processor.exception.RegBaseUncheckedException;
import org.mosip.registration.processor.dto.EnrollmentDTO;
import org.mosip.registration.processor.dto.json.demo.Demographic;
import org.mosip.registration.processor.dto.json.metadata.HashSequence;
import org.mosip.registration.processor.dto.json.metadata.PacketInfo;
import org.mosip.registration.processor.util.checksum.CheckSumUtil;
import org.mosip.registration.processor.consts.RegProcessorExceptionCode;
import org.mosip.registration.processor.util.hmac.HMACGeneration;
import org.mosip.registration.processor.util.zip.ZipCreationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.mosip.kernel.core.utils.json.JsonUtil.javaObjectToJsonString;
import static org.mosip.registration.processor.mapper.CustomObjectMapper.mapperFacade;
import static org.mosip.registration.processor.consts.RegConstants.DEMOGRPAHIC_JSON_NAME;
import static org.mosip.registration.processor.consts.RegConstants.HASHING_JSON_NAME;
import static org.mosip.registration.processor.consts.RegConstants.PACKET_META_JSON_NAME;

/**
 * Class for creating the Resident Registration
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class PacketCreationManager {

	@Autowired
	@Qualifier("registrationAuditBuilder")
	private AuditRequestBuilder auditRequestBuilder;
	@Autowired
	private AuditHandler auditHandler;
	private static MosipLogger LOGGER;
	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}
	
	/**
	 * Creates the packet
	 * 
	 * @param enrollmentDTO
	 *            the enrollment data for which packet has to be created
	 * @throws RegBaseCheckedException
	 */
	public byte[] create(EnrollmentDTO enrollmentDTO) throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Registration Creation had been called");
		try {
			// TODO : add Log and Audit
			// TODO: Add for Registration Creation Starts

			// TODO: Add JSON Conversion - Audit and Log
			Map<String, byte[]> jsonsMap = new HashMap<>();

			jsonsMap.put(
					DEMOGRPAHIC_JSON_NAME, javaObjectToJsonString(mapperFacade
									.map(enrollmentDTO.getPacketDTO().getDemographicDTO(), Demographic.class))
							.getBytes());
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Demographic Json created successfully");

			// TODO: Add HMAC generation - Audit and Log
			LinkedList<String> hmacApplicantSequence = new LinkedList<>();
			LinkedList<String> hmacHOFSequence = new LinkedList<>();
			LinkedList<String> hmacIntroducerSequence = new LinkedList<>();

			jsonsMap.put(HASHING_JSON_NAME,
					HMACGeneration.generatePacketDtoHash(enrollmentDTO.getPacketDTO(),
							jsonsMap.get(DEMOGRPAHIC_JSON_NAME), hmacApplicantSequence, hmacHOFSequence,
							hmacIntroducerSequence));
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "HMAC generateds successfully");

			// TODO : Add Audit and Log
			PacketInfo packetInfo = mapperFacade.map(enrollmentDTO, PacketInfo.class);
			HashSequence hashSequence = new HashSequence();
			hashSequence.setApplicant(hmacApplicantSequence);
			hashSequence.setHof(hmacHOFSequence);
			hashSequence.setIntroducer(hmacIntroducerSequence);
			packetInfo.setHashSequence(hashSequence);
			packetInfo.setCheckSumMap(CheckSumUtil.checkSumMap);
			jsonsMap.put(PACKET_META_JSON_NAME, javaObjectToJsonString(packetInfo).getBytes());
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Registration Packet Meta-Info JSON generated successfully");
			
			// Creating zip file for AES Encryption
			// TODO: Add Log and Audit for zip file creation before AES encryption
			auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId("applicationId")
			.setApplicationName("applicationName").setCreatedBy("createdBy").setDescription("description")
			.setEventId("eventId").setEventName("eventName").setEventType("eventType").setHostIp("hostIp")
			.setHostName("hostName").setId("id").setIdType("idType").setModuleId("moduleId")
			.setModuleName("moduleName").setSessionUserId("sessionUserId").setSessionUserName("sessionUserName");
			
			AuditRequest auditRequest = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequest);
			LOGGER.debug("REGISTRATION - PACKET_CREATION - CREATE", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Registration Creation had been ended");
			return ZipCreationManager.createPacket(enrollmentDTO, jsonsMap);
		} catch (MosipJsonProcessingException e) {
			// TODO Auto-generated catch block
			throw new RegBaseCheckedException("", "");
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_CREATION_EXCEPTION,
					uncheckedException.getMessage());
		}
	}

}
