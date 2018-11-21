package io.mosip.registration.processor.packet.decrypter.job.stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.packet.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.decrypter.job.Decryptor;
import io.mosip.registration.processor.packet.decryptor.job.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Component
public class PacketDecrypterStage extends MosipVerticleManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecrypterStage.class);

	private static final String USER = "MOSIP_SYSTEM";

	private static final String LOGDISPLAY = "{} - {} - {}";

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	//	@Value("${landingzone.scanner.stage.time.interval}")
	private int secs = 60;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;
	
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Autowired
	private Decryptor decryptor;

	@Autowired
	private PacketArchiver packetArchiver;

	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";

	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Registration Status table "
			+ "is not accessible";

	public MessageDTO process(MessageDTO object) {
		List<InternalRegistrationStatusDto> dtolist = null;

		try {
			dtolist = registrationStatusService
					.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
			System.out.println("list of packet: "+ dtolist.size());
			if (!(dtolist.isEmpty())) {
				dtolist.forEach(dto -> {
					try {
						System.out.println("decrypting packet one by one");
						decryptpacket(dto);

					} catch (TablenotAccessibleException e) {

						LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e.getMessage(), e);

					} catch (PacketDecryptionFailureException e) {

						LOGGER.error(LOGDISPLAY, e.getErrorCode(), e.getErrorText(), e);

						dto.setStatusCode(RegistrationStatusCode.PACKET_DECRYPTION_FAILED.toString());
						dto.setStatusComment("packet is in status packet for decryption failed");
						dto.setUpdatedBy(USER);
						registrationStatusService.updateRegistrationStatus(dto);

					} catch (IOException e) {

						LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e.getMessage(), e);

					}
				});
			} else if (dtolist.isEmpty()) {

				LOGGER.info("There are currently no files to be decrypted");
			}
		} catch (TablenotAccessibleException e) {

			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
		}

		return null;
	}

	/**
	 * method for decrypting registration packet
	 *
	 * @param dto
	 *            RegistrationStatus of the packet to be decrypted
	 * @throws IOException
	 * @throws PacketDecryptionFailureException
	 */
	private void decryptpacket(InternalRegistrationStatusDto dto) throws IOException, PacketDecryptionFailureException {
		try {
			System.out.println("inside decrypt packet");
			packetArchiver.archivePacket(dto.getRegistrationId());
		} catch (UnableToAccessPathException e) {
			LOGGER.error(LOGDISPLAY, e.getErrorCode(), e.getMessage(), e.getCause());
		} catch (PacketNotFoundException ex) {
			LOGGER.error(LOGDISPLAY, ex.getErrorCode(), ex.getMessage(), ex.getCause());
		}

		InputStream encryptedPacket = adapter.getPacket(dto.getRegistrationId());
		InputStream decryptedData = decryptor.decrypt(encryptedPacket, dto.getRegistrationId());

		if (decryptedData != null) {

			encryptedPacket.close();

			adapter.storePacket(dto.getRegistrationId(), decryptedData);

			adapter.unpackPacket(dto.getRegistrationId());

			dto.setStatusCode(RegistrationStatusCode.PACKET_DECRYPTION_SUCCESSFUL.toString());
			dto.setStatusComment("packet is in status packet for decryption successful");
			dto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(dto);

			MessageDTO messageDTO = new MessageDTO();

			messageDTO.setRid(dto.getRegistrationId());

			LOGGER.info(LOGDISPLAY, dto.getRegistrationId(),
					" Packet decrypted and extracted encrypted files stored in DFS.");

		} else {
			encryptedPacket.close();

			dto.setStatusCode(RegistrationStatusCode.PACKET_DECRYPTION_FAILED.toString());
			dto.setStatusComment("packet is in status packet for decryption failed");
			dto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(dto);

			LOGGER.info(LOGDISPLAY, dto.getRegistrationId(), " Packet is null and could not be  decrypted ");
		}

	}

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		mosipEventBus.getEventbus().setPeriodic(secs  * 1000, msg -> {
			this.send(mosipEventBus, MessageBusAddress.BATCH_BUS, new MessageDTO());
		});
	}

}
