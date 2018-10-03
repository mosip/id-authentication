package org.mosip.registration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.assertj.core.util.Sets;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.dao.RegTransactionDAO;
import org.mosip.registration.dao.RegistrationDAO;
import org.mosip.registration.entity.Registration;
import org.mosip.registration.entity.RegistrationTransaction;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.util.common.PacketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * 
 * This class will update the packet status in the table after the Packets got uploaded in the FTP server.
 * 
 * @author M1046564
 *
 */
@Service
@Transactional
public class PacketUploadService {
	
	/** Object for Logger. */
	private static MosipLogger LOGGER;

	private static final Set<String> PACKET_STATUS = Sets.newHashSet(Arrays.asList("I", "H", "A", "S"));

	@Autowired
	private RegistrationDAO registrationDAO;

	@Autowired
	private RegTransactionDAO regTransactionDAO;
	
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	} 

	/**
	 * This method is used to verify the packets that needs to be uploaded.
	 * 
	 * @param packetNames
	 * @param packetMap
	 * @return
	 */
	public List<File> verifyPacket(List<String> packetNames, Map<String, File> packetMap) {
		LOGGER.debug("REGISTRATION - VERIFY_PACKET - PACKET_UPLOAD_SERVICE", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"verifying the packets that needs to be uploaded");
		List<Registration> packetList = registrationDAO.getRegistrationById(packetNames);
		List<File> verifiedPackets = new ArrayList<>();
		for (Registration packetDet : packetList) {
			if (PACKET_STATUS.contains(packetDet.getClientStatusCode())) {
				verifiedPackets.add(packetMap.get(packetDet.getId()));
			}
		}
		return verifiedPackets;
	}

	/**
	 * This method is used to update the packet status that are uploaded.
	 * 
	 * @param uploadedPackets
	 * @return
	 */
	public Boolean updateStatus(List<File> uploadedPackets) {
		LOGGER.debug("REGISTRATION - UPDATE_STATUS - PACKET_UPLOAD_SERVICE", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Update the status of the uploaded packet");
		List<RegistrationTransaction> registrationTransactions = new ArrayList<>();
		PacketUtil packetUtil = new PacketUtil();
		List<String> fileNames = packetUtil.getPacketNames(uploadedPackets);
		for (String packetName : fileNames) {
			registrationDAO.updateRegStatus(packetName);
		}

		try {
			for (String id : fileNames) {
				registrationTransactions.add(regTransactionDAO.save(id));
			}
			regTransactionDAO.insertPacketTransDetails(registrationTransactions);
		} catch (RegBaseCheckedException e) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.CREATE_PACKET_ENTITY, e.toString());
		}
		return true;

	}

}
