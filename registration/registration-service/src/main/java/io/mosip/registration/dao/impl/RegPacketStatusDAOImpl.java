package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationTransactionType;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;

/**
 * The implementation class of {@link RegPacketStatusDAO}.
 *
 * @author Himaja Dhanyamraju
 */
@Repository
public class RegPacketStatusDAOImpl implements RegPacketStatusDAO {

	/** The registration repository. */
	@Autowired
	private RegistrationRepository registrationRepository;

	/**
	 * Object for Logger
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegPacketStatusDAOImpl.class);

	@Override
	public List<String> getPacketIdsByStatusUploaded() {
		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status uploaded-successfully has been started");

		List<Registration> registrationList = registrationRepository
				.findByclientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
		List<String> packetIds = new ArrayList<>();
		for (Registration registration : registrationList) {
			packetIds.add(registration.getId());
		}
		LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status post-sync has been ended");
		return packetIds;
	}

	@Override
	public void updatePacketIdsByServerStatus(List<RegPacketStatusDTO> packetStatus) {
		try {
			LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
					"packets status sync from server has been started");
			for (RegPacketStatusDTO regPacketStatusDTO : packetStatus) {
				Registration registration = registrationRepository.findById(Registration.class,
						regPacketStatusDTO.getPacketId());
				registration.setServerStatusCode(regPacketStatusDTO.getStatus());
				registration.setServerStatusTimestamp(new Timestamp(System.currentTimeMillis()));
				List<RegistrationTransaction> transactionList = registration.getRegistrationTransaction();
				RegistrationTransaction registrationTxn = new RegistrationTransaction();

				registrationTxn.setRegId(registration.getId());
				registrationTxn.setTrnTypeCode(RegistrationTransactionType.CREATED.getCode());
				registrationTxn.setLangCode("ENG");
				registrationTxn.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
				registrationTxn.setCrDtime(new Timestamp(System.currentTimeMillis()));

				File ackFile = null;
				File zipFile = null;
				if (regPacketStatusDTO.getStatus()
						.equalsIgnoreCase(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED)) {
					registration.setClientStatusCode(RegistrationClientStatusCode.DELETED.getCode());
					registrationTxn.setStatusCode(registration.getClientStatusCode());
					String ackPath = registration.getAckFilename();
					ackFile = new File(ackPath);
					String zipPath = ackPath.replace("_Ack.png", RegistrationConstants.ZIP_FILE_EXTENSION);
					zipFile = new File(zipPath);
				} else {
					registrationTxn.setStatusCode(registration.getClientStatusCode());
				}
				transactionList.add(registrationTxn);
				registration.setRegistrationTransaction(transactionList);
				Registration updatedRegistration = registrationRepository.update(registration);
				if (ackFile != null && updatedRegistration != null) {
					Files.delete(ackFile);
					Files.delete(zipFile);
				}
			}
			LOGGER.debug("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
					"packets status sync from server has been ended");
		} catch (RuntimeException runtimeException) {			
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_UPDATE_STATUS,
					runtimeException.toString());
		}

	}

}
