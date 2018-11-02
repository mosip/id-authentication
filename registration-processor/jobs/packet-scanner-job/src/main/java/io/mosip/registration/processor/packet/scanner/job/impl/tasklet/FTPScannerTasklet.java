package io.mosip.registration.processor.packet.scanner.job.impl.tasklet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.scanner.job.exception.FTPNotAccessibleException;

/**
 * The Class FTPScannerTasklet.
 */
@Component
public class FTPScannerTasklet implements Tasklet {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FTPScannerTasklet.class);

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The filemanager. */
	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	/** The packet handler service. */
	@Autowired
	protected PacketReceiverService<MultipartFile, Boolean> packetHandlerService;

	/** The Constant FTP_NOT_ACCESSIBLE. */
	private static final String FTP_NOT_ACCESSIBLE = "The FTP Path set by the System is not accessible";

	/** The Constant DUPLICATE_UPLOAD. */
	private static final String DUPLICATE_UPLOAD = "Duplicate file uploading to landing zone";

	/** The core audit request builder. */
	@Autowired
	CoreAuditRequestBuilder coreAuditRequestBuilder;

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/** The is transaction successful. */
	boolean isTransactionSuccessful = false;


	/**
	 * Executes FTPScannerTasklet to move enrollment packet from the FTP zone to
	 * Landing zone folder.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return RepeatStatus
	 * @throws Exception the exception
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {

		String filepath = this.filemanager.getCurrentDirectory();
		Stream<Path> paths = Files.walk(Paths.get(filepath));
		paths.filter(Files::isRegularFile).forEach(filepathName -> {

			File file = new File(filepathName.toString());
			String pattern = Pattern.quote(System.getProperty("file.separator"));
			String[] directory = filepathName.getParent().toString().split(pattern);
			String childFolder = directory[directory.length - 1];
			try(FileInputStream input = new FileInputStream(file)) {

				MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "mixed/multipart",
						IOUtils.toByteArray(input));
				packetHandlerService.storePacket(multipartFile);
				this.filemanager.cleanUpFile(DirectoryPathDto.FTP_ZONE, DirectoryPathDto.LANDING_ZONE,
						filepathName.getFileName().toString().split("\\.")[0], childFolder);
				isTransactionSuccessful = true;

			} catch (FileNotFoundInDestinationException e) {
				LOGGER.error(e.getErrorCode(), e.getErrorText(), e);
			} catch (DuplicateUploadRequestException e) {
				this.filemanager.cleanUpFile(DirectoryPathDto.FTP_ZONE, DirectoryPathDto.LANDING_ZONE,
						filepathName.getFileName().toString().split("\\.")[0], childFolder);
				LOGGER.error(LOGDISPLAY, DUPLICATE_UPLOAD, e);
			} catch (IOException e) {
				FTPNotAccessibleException ftpNotAccessibleException = new FTPNotAccessibleException(FTP_NOT_ACCESSIBLE,
						e);
				LOGGER.error(ftpNotAccessibleException.getErrorCode(), ftpNotAccessibleException.getErrorText(),
						ftpNotAccessibleException);
			}finally{

				if(isTransactionSuccessful) {
					eventId = EventId.RPR_403.toString();
				}else {
					eventId = EventId.RPR_405.toString();
				}
				eventName=	eventId.equalsIgnoreCase(EventId.RPR_403.toString()) ? EventName.DELETE.toString(): EventName.EXCEPTION.toString();	
				eventType=	eventId.equalsIgnoreCase(EventId.RPR_403.toString()) ? EventType.BUSINESS.toString(): EventType.SYSTEM.toString();
				String description = isTransactionSuccessful ? "File moved from FTP zone to landing zone successfully" : "File moving from FTP zone to landing zone  Failed";

				coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,AuditLogConstant.NO_ID.toString());
			}
		});

		paths.close();
		deleteFolder(filepath);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Delete empty folder from FTP zone after all the files are copied.
	 *
	 * @param filepath the filepath
	 */
	public void deleteFolder(String filepath) {

		try {
			Stream<Path> deletepath = Files.walk(Paths.get(filepath));
			deletepath.filter(Files::isDirectory).forEach(filepathName -> {
				File file = new File(filepathName.toString());
				if (file.isDirectory() && !(file.getName().equalsIgnoreCase(new File(filepath).getName()))
						&& (file.list().length == 0)) {
					try {
						Files.delete(file.toPath());
						isTransactionSuccessful = true;

					} catch (IOException e) {
						FTPNotAccessibleException ftpNotAccessibleException = new FTPNotAccessibleException(
								FTP_NOT_ACCESSIBLE, e);
						LOGGER.error(ftpNotAccessibleException.getErrorCode(), ftpNotAccessibleException.getErrorText(),
								ftpNotAccessibleException);
					}
				}
			});
			deletepath.close();
		} catch (IOException e) {
			FTPNotAccessibleException ftpNotAccessibleException = new FTPNotAccessibleException(FTP_NOT_ACCESSIBLE, e);
			LOGGER.error(ftpNotAccessibleException.getErrorCode(), ftpNotAccessibleException.getErrorText(),
					ftpNotAccessibleException);
		}finally {	

			eventId = isTransactionSuccessful ? EventId.RPR_403.toString() : EventId.RPR_405.toString();
			eventName=	eventId.equalsIgnoreCase(EventId.RPR_403.toString()) ? EventName.DELETE.toString(): EventName.EXCEPTION.toString();	
			eventType=	eventId.equalsIgnoreCase(EventId.RPR_403.toString()) ? EventType.BUSINESS.toString(): EventType.SYSTEM.toString();
			String description = isTransactionSuccessful ? "Deleted empty folder from FTP zone successfully after all the files are copied" : "Deleting empty folder from FTP zone Failed";

			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
		}

	}

}
