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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.scanner.job.exception.FTPNotAccessibleException;

@RefreshScope
@Component
public class FTPScannerTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(FTPScannerTasklet.class);

	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	@Autowired
	protected PacketReceiverService<MultipartFile, Boolean> packetHandlerService;

	private static final String FTP_NOT_ACCESSIBLE = "The FTP Path set by the System is not accessible";
	private static final String DUPLICATE_UPLOAD = "Duplicate file uploading to landing zone";

	/**
	 * Executes FTPScannerTasklet to move enrollment packet from the FTP zone to
	 * Landing zone folder
	 *
	 * @param StepContribution
	 *            arg0
	 * @param ChunkContext
	 *            arg1
	 * @return RepeatStatus
	 * @throws Exception
	 *
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
			try {
				FileInputStream input = new FileInputStream(file);
				MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "mixed/multipart",
						IOUtils.toByteArray(input));
				packetHandlerService.storePacket(multipartFile);
				input.close();
				this.filemanager.cleanUpFile(DirectoryPathDto.FTP_ZONE, DirectoryPathDto.LANDING_ZONE,
						filepathName.getFileName().toString().split("\\.")[0], childFolder);
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
			}
		});
		paths.close();
		deleteFolder(filepath);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Delete empty folder from FTP zone after all the files are copied.
	 * 
	 * @param filepath
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
		}

	}

}
