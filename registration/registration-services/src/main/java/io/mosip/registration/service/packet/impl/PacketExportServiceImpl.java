package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.packet.PacketExportService;

@Service
public class PacketExportServiceImpl implements PacketExportService {

	@Autowired
	private RegistrationDAO registrationDAO;

	private static final Logger LOGGER = AppConfig.getLogger(PacketExportServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.packet.impl.PacketExportService#
	 * getSynchedRecords()
	 */
	@Override
	public List<Registration> getSynchedRecords() {

		LOGGER.debug("REGISTRATION - FETCH_EXPORT_PACKETS - PACKET_EXPORT_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetch the packets that needs to be exported");

		return registrationDAO.getPacketsToBeSynched(RegistrationConstants.PACKET_EXPORT_STATUS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.packet.impl.PacketExportService#
	 * updateRegistrationStatus(java.util.List)
	 */
	@Override
	public ResponseDTO updateRegistrationStatus(List<Registration> exportedPackets) {

		LOGGER.debug("REGISTRATION - UPDATE_EXPORTED_PACKETS - PACKET_EXPORT_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Updating the table with the updated status");

		ResponseDTO responseDTO = new ResponseDTO();
		List<Registration> updatedExportPackets = new ArrayList<>();
		exportedPackets.forEach(regPacket -> {
			updatedExportPackets.add(registrationDAO.updateRegStatus(regPacket));
		});
		if (exportedPackets.size() == updatedExportPackets.size() || !updatedExportPackets.isEmpty()) {
			SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setMessage(RegistrationConstants.SUCCESS);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
		}

		return responseDTO;
	}
}
