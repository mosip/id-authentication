package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;

@Repository
public class RegPacketStatusDAOImpl implements RegPacketStatusDAO {

	/** The registration repository. */
	@Autowired
	private RegistrationRepository registrationRepository;

	/**
	 * Object for Logger
	 */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender
	 *            the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@Override
	public List<String> getPacketIdsByStatusUploaded() {
		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME,
				APPLICATION_ID, "getting packets by status uploaded-successfully has been started");
		
		List<Registration> registrationList = registrationRepository
				.findByclientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
		List<String> packetIds = new ArrayList<>();
		for (Registration registration : registrationList) {
			packetIds.add(registration.getId());
		}
		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME,
				APPLICATION_ID, "getting packets by status post-sync has been ended");
		return packetIds;
	}

	@Override
	public void updatePacketIdsByServerStatus(List<RegPacketStatusDTO> packetStatus) {
		try {
			LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME,
					APPLICATION_ID, "packets status sync from server has been started");
			for (RegPacketStatusDTO regPacketStatusDTO : packetStatus) {
				Registration reg = registrationRepository.findById(Registration.class, regPacketStatusDTO.getPacketId());
				reg.setServerStatusCode(regPacketStatusDTO.getStatus());
				reg.setClientStatusCode(RegistrationClientStatusCode.SERVER_VALIDATED.getCode());
				registrationRepository.update(reg);
			}
			LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME,
					APPLICATION_ID, "packets status sync from server has been ended");
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_UPDATE_STATUS,
					runtimeException.toString());
		}
		
	}

}
