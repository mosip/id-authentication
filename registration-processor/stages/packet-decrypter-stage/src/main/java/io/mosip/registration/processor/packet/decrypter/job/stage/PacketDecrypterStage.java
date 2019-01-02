package io.mosip.registration.processor.packet.decrypter.job.stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.packet.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.decrypter.job.Decryptor;
import io.mosip.registration.processor.packet.decryptor.job.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Component
public class PacketDecrypterStage extends MosipVerticleManager {
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketDecrypterStage.class);

	private static final String USER = "MOSIP_SYSTEM";

	private static final String LOGDISPLAY = "{} - {} - {}";

	// @Value("${landingzone.scanner.stage.time.interval}")
	private long secs = 30;

	MosipEventBus mosipEventBus = null;

	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Autowired
	private Decryptor decryptor;

	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	private PacketArchiver packetArchiver;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";

	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Registration Status table "
			+ "is not accessible";

	private String description = "";
	private boolean isTransactionSuccessful = false;
	private String registrationId = "";

	@Override
	public MessageDTO process(MessageDTO object) {
		List<InternalRegistrationStatusDto> dtolist = null;

		try {
			dtolist = registrationStatusService
					.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
			if (!(dtolist.isEmpty())) {
				dtolist.forEach(dto -> {
					this.registrationId = dto.getRegistrationId();
					try {
						decryptpacket(dto);

					} catch (TablenotAccessibleException e) {
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId(),REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE+e.getMessage());
						this.isTransactionSuccessful = false;
						this.description = "Registration status table is not accessible for packet "
								+ this.registrationId;
					} catch (PacketDecryptionFailureException e) {
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId(),e.getErrorCode()+" "+e.getErrorText());
						dto.setStatusCode(RegistrationStatusCode.PACKET_DECRYPTION_FAILED.toString());
						dto.setStatusComment("Packet decryption failed");
						dto.setUpdatedBy(USER);
						registrationStatusService.updateRegistrationStatus(dto);
						this.isTransactionSuccessful = false;
						this.description = "Packet decryption failed for packet " + this.registrationId;
					} catch (IOException e) {
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId(),DFS_NOT_ACCESSIBLE+" "+ e.getMessage());
						this.isTransactionSuccessful = false;
						this.description = "File System is not accessible for packet " + this.registrationId;
					} finally {

						String eventId = "";
						String eventName = "";
						String eventType = "";
						eventId = this.isTransactionSuccessful ? EventId.RPR_402.toString()
								: EventId.RPR_405.toString();
						eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
								: EventName.EXCEPTION.toString();
						eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
								: EventType.SYSTEM.toString();

						auditLogRequestBuilder.createAuditRequestBuilder(this.description, eventId, eventName,
								eventType, this.registrationId);

					}
				});
			} else if (dtolist.isEmpty()) {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),"NOFILESTOBEDECRYPTED","There are currently no files to be decrypted");
			}
		} catch (TablenotAccessibleException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE,e.getMessage());
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
			packetArchiver.archivePacket(dto.getRegistrationId());
		} catch (UnableToAccessPathException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId(),e.getErrorCode()+" "+e.getMessage());
		} catch (PacketNotFoundException ex) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId(), ex.getErrorCode()+" "+ex.getMessage());
		}

		InputStream encryptedPacket = adapter.getPacket(dto.getRegistrationId());
		InputStream decryptedData = decryptor.decrypt(encryptedPacket, dto.getRegistrationId());

		// if (decryptedData != null) {

		encryptedPacket.close();

		adapter.storePacket(dto.getRegistrationId(), decryptedData);

		adapter.unpackPacket(dto.getRegistrationId());

		dto.setStatusCode(RegistrationStatusCode.PACKET_DECRYPTION_SUCCESS.toString());
		dto.setStatusComment("Packet is succesfully decrypted");
		dto.setUpdatedBy(USER);
		registrationStatusService.updateRegistrationStatus(dto);

		MessageDTO messageDTO = new MessageDTO();

		messageDTO.setRid(dto.getRegistrationId());
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId()," Packet decrypted and extracted encrypted files stored in DFS.");
		MessageDTO message = new MessageDTO();
		message.setRid(dto.getRegistrationId());

		sendMessage(mosipEventBus, message);
		this.description = "packet decryption was successful for packet" + this.registrationId;
		this.isTransactionSuccessful = true;
		// else {
		// encryptedPacket.close();
		//
		// dto.setStatusCode(RegistrationStatusCode.PACKET_DECRYPTION_FAILED.toString());
		// dto.setStatusComment("packet is in status packet for decryption failed");
		// dto.setUpdatedBy(USER);
		// registrationStatusService.updateRegistrationStatus(dto);
		// this.description="packet decryption failed for packet"+this.registrationId;
		// this.isTransactionSuccessful=false;
		// LOGGER.info(LOGDISPLAY, dto.getRegistrationId(), " Packet is null and could
		// not be decrypted ");
		// }

	}

	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		mosipEventBus.getEventbus().setPeriodic(secs * 1000, msg ->
		// sendMessage(mosipEventBus, new MessageDTO())
		process(new MessageDTO()));
	}

	private void sendMessage(MosipEventBus mosipEventBus, MessageDTO message) {
		this.send(mosipEventBus, MessageBusAddress.BATCH_BUS, message);
	}

}
