package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.ReRegistrationService;

/**
 * Implementation class for {@link ReRegistrationService}
 * 
 * @author saravanakumar gnanaguru
 * @since 1.0.0
 */
@Service
public class ReRegistrationServiceImpl extends BaseService implements ReRegistrationService {
	
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(ReRegistrationServiceImpl.class);

	@Autowired
	private RegistrationDAO registrationDAO;


	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.impl.ReRegistrationService#
	 * getAllReRegistrationPackets()
	 */
	@Override
	public List<PacketStatusDTO> getAllReRegistrationPackets() {
		LOGGER.info(LoggerConstants.LOG_GET_RE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Getting all the re-registration packets from the table");
		String[] packetStatus = { RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode(),
				RegistrationConstants.RE_REGISTRATION_STATUS };
		List<Registration> reRegisterPackets = registrationDAO.getAllReRegistrationPackets(packetStatus);
		List<PacketStatusDTO> uiPacketDto = new ArrayList<>();
		for (Registration reRegisterPacket : reRegisterPackets) {
			PacketStatusDTO packetStatusDTO = new PacketStatusDTO();
			packetStatusDTO.setFileName(reRegisterPacket.getId());
			packetStatusDTO.setPacketPath(reRegisterPacket.getAckFilename());
			packetStatusDTO.setCreatedTime(regDateConversion(reRegisterPacket.getCrDtime()));
			uiPacketDto.add(packetStatusDTO);
		}
		LOGGER.info(LoggerConstants.LOG_GET_RE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Fetching from the table finished");
		return uiPacketDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.impl.ReRegistrationService#
	 * updateReRegistrationStatus(java.util.Map)
	 */
	@Override
	public boolean updateReRegistrationStatus(Map<String, String> reRegistrationStatus) {
		LOGGER.info(LoggerConstants.LOG_UPADTE_RE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Update the registration status of the packet in the table");
		for (Map.Entry<String, String> reRegistration : reRegistrationStatus.entrySet()) {
			PacketStatusDTO registration = new PacketStatusDTO();
			registration.setFileName(reRegistration.getKey());
			registration.setPacketClientStatus(RegistrationClientStatusCode.RE_REGISTER.getCode());
			registration.setClientStatusComments("Re-Register-" + reRegistration.getValue());
			registrationDAO.updateRegStatus(registration);
		}
		LOGGER.info(LoggerConstants.LOG_UPADTE_RE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"All the reregistered packets are updated in the table");
		return true;
	}
	
}
