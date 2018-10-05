package io.mosip.registration.processor.packet.decryptor.job.tasklet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.SdkClientException;

import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.decryptor.job.Decryptor;
import io.mosip.registration.processor.packet.decryptor.job.messagesender.DecryptionMessageSender;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Component
public class PacketDecryptorTasklet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecryptorTasklet.class);

	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	private FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter = new FilesystemCephAdapterImpl();
	@Autowired
	private Decryptor decryptor;

	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";

	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Registration Status table "
			+ "is not accessible";

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<RegistrationStatusDto> dtolist = null;

		try {
			dtolist = registrationStatusService
					.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());

			if (!(dtolist.isEmpty())) {
				dtolist.forEach(dto -> {
					try {

						adapter.unpackPacket(dto.getRegistrationId());

						InputStream encryptedPacket = adapter.getPacket(dto.getRegistrationId());
						InputStream decryptedData = decryptor.decrypt(encryptedPacket, dto.getRegistrationId());

						if (decryptedData != null) {

							encryptedPacket.close();

							adapter.storePacket(dto.getRegistrationId(), decryptedData);

							adapter.unpackPacket(dto.getRegistrationId());

							dto.setStatusCode(RegistrationStatusCode.PACKET_DECRYPTION_SUCCESSFUL.toString());
							registrationStatusService.updateRegistrationStatus(dto);

							LOGGER.info(LOGDISPLAY, dto.getRegistrationId(),
									" Packet decrypted and extracted encrypted files stored in DFS.");

							DecryptionMessageSender decryptionMessageSender = new DecryptionMessageSender();
							decryptionMessageSender.sendMessage(dto.getRegistrationId());

							LOGGER.info(LOGDISPLAY, dto.getRegistrationId(), "Packet sent for structure validation");

						} else {
							encryptedPacket.close();

							LOGGER.info(LOGDISPLAY, dto.getRegistrationId(), " Packet could not be  decrypted ");
						}

					} catch (TablenotAccessibleException e) {

						LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);

					} catch (SdkClientException | IOException e) {

						LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e);

					}
				});
			} else if (dtolist.isEmpty()) {

				LOGGER.info("There are currently no files to be decrypted");
			}
		} catch (TablenotAccessibleException e) {

			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
		}
		return RepeatStatus.FINISHED;
	}
}
