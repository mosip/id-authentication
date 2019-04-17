package io.mosip.registration.processor.packet.uploader.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.SftpJschConnectionDto;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.decryptor.Decryptor;
import io.mosip.registration.processor.packet.uploader.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.packet.uploader.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.uploader.exception.PacketReceiverAppException;
import io.mosip.registration.processor.packet.uploader.exception.UnequalHashSequenceException;
import io.mosip.registration.processor.packet.uploader.exception.VirusScanFailedException;
import io.mosip.registration.processor.packet.uploader.exception.VirusScannerServiceException;
import io.mosip.registration.processor.packet.uploader.service.PacketUploaderService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

/**
 * The Class PacketReceiverServiceImpl.
 *
 */
@RefreshScope
@Component
public class PacketUploaderServiceImpl implements PacketUploaderService<MessageDTO> {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketUploaderServiceImpl.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	@Autowired
	private FileSystemAdapter hdfsAdapter;

	//@Value("${registration.processor.server.ppk.filelocation}")
	private String ppkFileLocation;

	//@Value("${registration.processor.server.ppk.filename}")
	private String ppkFileName;

	@Value("${registration.processor.dmz.server.host}")
	private String host;

	@Value("${registration.processor.dmz.server.port}")
	private String dmzPort;

	@Value("${registration.processor.dmz.server.user}")
	private String dmzServerUser;

	@Value("${registration.processor.dmz.server.protocal}")
	private String dmzServerProtocal;

	/** The file manager. */
	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The sync registration service. */
	@Autowired
	private SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	RegistrationExceptionMapperUtil registrationStatusMapperUtil = new RegistrationExceptionMapperUtil();

	/** The packet receiver stage. */

	@Value("${registration.processor.packet.ext}")
	private String extention;

	/** The file size. */
	@Value("${registration.processor.max.file.size}")
	private String fileSize;

	/** The registration exception mapper util. */
	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	/** The reg entity. */
	private SyncRegistrationEntity regEntity;

	/** The virus scanner service. */
	@Autowired
	private VirusScanner<Boolean, InputStream> virusScannerService;

	/** The decryptor. */
	@Autowired
	private Decryptor decryptor;

	@Value("${registration.processor.uploader.max.retry.count}")
	private int maxRetryCount;

	/** The env. */
	@Autowired
	private Environment env;

	/** The storage flag. */
	private Boolean storageFlag = false;

	/** The description. */
	private String description = "";

	/** The is transaction successful. */
	boolean isTransactionSuccessful = false;

	/** The registration id. */
	private String registrationId;

	/** The packet archiver. */
	@Autowired
	private PacketArchiver packetArchiver;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.id.issuance.packet.handler.service.PacketUploadService#
	 * validatePacket( java.lang.Object)
	 */



	@Override
	public MessageDTO validatePacket(String regId) {

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setInternalError(false);
		messageDTO.setIsValid(false);
		SftpJschConnectionDto jschConnectionDto=new SftpJschConnectionDto();
		jschConnectionDto.setHost(host);
		jschConnectionDto.setPort(Integer.parseInt(dmzPort));
		jschConnectionDto.setPpkFileLocation(ppkFileLocation+File.separator+ppkFileName);
		jschConnectionDto.setUser(dmzServerUser);
		jschConnectionDto.setProtocal(dmzServerProtocal);
		byte[] encryptedByteArray=fileManager.getFile(DirectoryPathDto.LANDING_ZONE, regId, jschConnectionDto);
		InputStream decryptedData = null;

		this.registrationId = regId;
		isTransactionSuccessful = false;
		InternalRegistrationStatusDto dto = new InternalRegistrationStatusDto();

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "PacketReceiverServiceImpl::validatePacket()::entry");
		messageDTO.setRid(registrationId);

		regEntity = syncRegistrationService.findByRegistrationId(registrationId);

		try  {
			if(encryptedByteArray != null) {
				
				if(validateHashCode(new ByteArrayInputStream(encryptedByteArray))) {


					if(scanFile(new ByteArrayInputStream(encryptedByteArray))) {


						decryptedData = decryptor.decrypt(new ByteArrayInputStream(encryptedByteArray),registrationId);

						if(scanFile(decryptedData)) {

							dto = registrationStatusService.getRegistrationStatus(registrationId);
							int retrycount = (dto.getRetryCount() == null) ? 0 : dto.getRetryCount() + 1;
							dto.setRetryCount(retrycount);
							dto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.UPLOAD_PACKET.toString());
							dto.setRegistrationStageName(this.getClass().getSimpleName());
							if (retrycount < getMaxRetryCount()) {
								regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
										LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
										"PacketUploaderStage::process()::entry");

								messageDTO = uploadpacket(dto, messageDTO);
								if (messageDTO.getIsValid()) {
									dto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
									isTransactionSuccessful = true;
									description = "Packet uploaded to DFS successfully for registrationId " + this.registrationId;
									regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
											LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
									regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
											LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
											"PacketUploaderStage::process()::exit");

								}
							} else {

								messageDTO.setInternalError(Boolean.TRUE);
								description = "Failure in uploading the packet to Packet Store" + registrationId;
								dto.setLatestTransactionStatusCode(registrationStatusMapperUtil
										.getStatusCode(RegistrationExceptionTypeCode.PACKET_UPLOADER_FAILED));
								dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOAD_TO_PACKET_STORE_FAILED.toString());
								dto.setStatusComment("Packet upload to packet store failed for " + registrationId);
								dto.setUpdatedBy(USER);
							}



						}else {

							////decrypted file scan log

						}





					}else {
						//encrypted file scan log

					}



				}else {
					//hash code log
				}
			}else {

				//if encrypted data is null
			}

		} catch (IOException e) {

			description = " IOException in packet receiver for registrationId" + registrationId + "::"
					+ e.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			throw new PacketReceiverAppException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
		} catch (DataAccessException e) {

			description = "DataAccessException in packet receiver for registrationId" + registrationId + "::"
					+ e.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), registrationId, "Error while updating status : "
							+ PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getMessage());
			throw new PacketReceiverAppException(PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getMessage());

		} catch (PacketDecryptionFailureException e) {
			description = "Packet decryption failed for registrationId " + registrationId + "::" + e.getErrorCode()
			+ e.getErrorText();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), registrationId, e.getMessage());
			throw new PacketReceiverAppException(e.getErrorCode(), e.getMessage());

		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), registrationId, "API resource not accessible : "
							+ PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage());
			description = PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage();
			throw new PacketReceiverAppException(PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getCode(),
					PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage());

		} finally {

			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId, ApiName.AUDIT);
		}

		if (storageFlag) {
			messageDTO.setIsValid(true);
		}


		return messageDTO;
	}





	/**
	 * Scan file.
	 *
	 * @param inputStream
	 *            the input stream
	 */
	private boolean scanFile(InputStream inputStream) {
		boolean isInputFileClean=false;
		try {
			isInputFileClean = virusScannerService.scanFile(inputStream);
			if (!isInputFileClean) {
				description = "Packet virus scan failed exception in packet receiver for registrationId ::"
						+ registrationId + PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCAN_FAILED.getMessage();
				//	throw new VirusScanFailedException(PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCAN_FAILED.getMessage());
			}
		} catch (VirusScannerException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCANNER_SERVICE_FAILED.getMessage());
			//	throw new VirusScannerServiceException(PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCANNER_SERVICE_FAILED.getMessage());
		}
		return isInputFileClean;
	}





	private boolean validateHashCode(InputStream inputStream) throws IOException {
		boolean isValidHash=false;
		byte[] isbytearray = IOUtils.toByteArray(inputStream);
		byte[] hashSequence = HMACUtils.generateHash(isbytearray);
		byte[] packetHashSequenceFromEntity = hashSequence;//Todo: PacketHashSequesnce
		if (!(Arrays.equals(hashSequence, packetHashSequenceFromEntity))) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PKR_PACKET_HASH_NOT_EQUALS_SYNCED_HASH.getMessage());

			return isValidHash;
		}else {

			isValidHash=true;
			return isValidHash;

		}
	}



	private MessageDTO uploadpacket(InternalRegistrationStatusDto dto, MessageDTO object) throws IOException {

		packetArchiver.archivePacket(dto.getRegistrationId());
		String filepath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString()) + File.separator
				+ dto.getRegistrationId() + ".zip";
		File file = new File(filepath);
		InputStream decryptedData = new FileInputStream(file);
		object = sendToDFS(dto, decryptedData, object);
		if (object.getIsValid()) {
			isTransactionSuccessful = true;
			description = "Packet sent to DFS with registrationId " + dto.getRegistrationId();
		}

		return object;
	}


	private MessageDTO sendToDFS(InternalRegistrationStatusDto entry, InputStream decryptedData, MessageDTO object)
			throws IOException {

		object.setIsValid(false);

		registrationId = entry.getRegistrationId();

		hdfsAdapter.storePacket(registrationId, decryptedData);
		hdfsAdapter.unpackPacket(registrationId);

		if (hdfsAdapter.isPacketPresent(registrationId)) {

			fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_DEC, registrationId);
			fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
			fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_UNPACK, registrationId);

			entry.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
			entry.setStatusComment("Packet " + registrationId + " is uploaded in file system.");
			entry.setUpdatedBy(USER);
			object.setInternalError(false);
			object.setIsValid(true);
			object.setRid(registrationId);

			isTransactionSuccessful = true;
			description = " packet sent to DFS for registrationId " + registrationId;
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PUM_PACKET_DELETION_INFO.getMessage());

		}

		return object;
	}

	/**
	 * Get max retry count.
	 * 
	 * @return maxRetryCount
	 */
	public int getMaxRetryCount() {
		return maxRetryCount;
	}
}