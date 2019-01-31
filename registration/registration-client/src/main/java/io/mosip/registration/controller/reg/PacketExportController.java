package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.packet.PacketExportService;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

@Controller
public class PacketExportController extends BaseController {

	@Autowired
	private PacketExportService packetExportServiceImpl;

	private static final Logger LOGGER = AppConfig.getLogger(PacketExportController.class);

	/**
	 * To Get the Synced Packets and export the external device
	 */
	public List<Registration> packetExport() {

		LOGGER.debug("REGISTRATION - HANDLE_PACKET_EXPORT - PACKET_EXPORT_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Export the packets to the External device");

		// Fetching the Synched Packets
		List<Registration> synchedRecords = packetExportServiceImpl.getSynchedRecords();
		List<Registration> exportedPackets = new ArrayList<>();

		if (!synchedRecords.isEmpty()) {

			Stage primaryStage = new Stage();
			DirectoryChooser destinationSelector = new DirectoryChooser();
			destinationSelector.setTitle(RegistrationConstants.FILE_EXPLORER_NAME);
			Path currentRelativePath = Paths.get("");
			File defaultDirectory = new File(currentRelativePath.toAbsolutePath().toString());
			destinationSelector.setInitialDirectory(defaultDirectory);
			File destinationPath = destinationSelector.showDialog(primaryStage);

			if (destinationPath != null) {
				// Iterate through the synched packets and copy to the Destination folder
				synchedRecords.forEach(packetToCopy -> {
					String ackFileName = packetToCopy.getAckFilename();
					int lastIndex = ackFileName.indexOf(RegistrationConstants.ACKNOWLEDGEMENT_FILE);
					String packetPath = ackFileName.substring(0, lastIndex);
					File packet = new File(packetPath + RegistrationConstants.ZIP_FILE_EXTENSION);
					try {
						FileUtils.copyFileToDirectory(packet, destinationPath);
						packetToCopy.setClientStatusCode(RegistrationClientStatusCode.EXPORT.getCode());
						exportedPackets.add(packetToCopy);
					} catch (IOException ioException) {
						LOGGER.error("REGISTRATION - HANDLE_PACKET_EXPORT_ERROR - PACKET_EXPORT_CONTROLLER",
								APPLICATION_NAME, APPLICATION_ID,
								"Error while exporting packets. packet id : " + packetToCopy.getId());
					}
				});
				if (!exportedPackets.isEmpty()) {
					ResponseDTO responseDTO = packetExportServiceImpl.updateRegistrationStatus(exportedPackets);
					if (responseDTO.getSuccessResponseDTO() != null
							&& responseDTO.getSuccessResponseDTO().getMessage().equals(RegistrationConstants.SUCCESS)) {
						generateAlert(RegistrationConstants.INFO,
								exportedPackets.size() + " " + RegistrationUIConstants.PACKET_EXPORT_SUCCESS_MESSAGE);
					} else {
						generateAlert(RegistrationConstants.INFO, RegistrationUIConstants.PACKET_EXPORT_MESSAGE);
					}
				} else {
					generateAlert(RegistrationConstants.INFO, RegistrationUIConstants.PACKET_EXPORT_MESSAGE);
				}
			} else {
				generateAlert(RegistrationConstants.INFO, RegistrationUIConstants.PACKET_EXPORT_MESSAGE);
			}
		}
		return exportedPackets;

	}
}
