package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.RegistrationPacketVirusScanService;

/**
 * Implementation class for {@link RegistrationPacketVirusScanService}
 * 
 * @author saravanakumar gnanaguru
 * @since 1.0.0
 */
@Service
public class RegistrationPacketVirusScanServiceImpl extends BaseService implements RegistrationPacketVirusScanService {

	@Autowired
	private VirusScanner<Boolean, InputStream> virusScanner;

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationPacketVirusScanServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.impl.RegistrationPacketVirusScanService#
	 * scanPacket()
	 */
	@Override
	public synchronized ResponseDTO scanPacket() {

		LOGGER.info("REGISTRATION - PACKET_SCAN - REGISTRATION_PACKET_VIRUS_SCAN", APPLICATION_NAME, APPLICATION_ID,
				"Scanning of Virus Packet start");
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		List<String> pathList = Arrays.asList(
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.PKT_STORE_LOC)), 
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.PRE_REG_PACKET_LOCATION)),
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.LOGS_PATH)),
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.DB_PATH)),
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.CLIENT_PATH)));
		List<File> filesList = new ArrayList<>();
		List<String> infectedFiles = new ArrayList<>();
		List<ErrorResponseDTO> errorList = new ArrayList<>();
		StringBuilder infectedFileName = new StringBuilder();
		try {
			for (String path : pathList) {
				filesList.addAll(getFilesFromFolders(path, filesList));
			}

			for (File fileToScan : filesList) {
				
				if (!virusScanner.scanDocument(fileToScan)) {

					infectedFiles.add(fileToScan.getName());
				}
			}
			if (!infectedFiles.isEmpty()) {
				for (String fileName : infectedFiles) {
					infectedFileName.append(fileName + ";");
				}
				successResponseDTO.setMessage(infectedFileName.toString());
			} else {
				successResponseDTO.setMessage(RegistrationConstants.SUCCESS);
			}
			responseDTO.setSuccessResponseDTO(successResponseDTO);
		} catch (VirusScannerException virusScannerException) {
			LOGGER.error("REGISTRATION - PACKET_SCAN_EXCEPTION", APPLICATION_NAME, APPLICATION_ID,
					virusScannerException.getMessage());
			LOGGER.debug("REGISTRATION - PACKET_SCAN_EXCEPTION_DEBUG", APPLICATION_NAME, APPLICATION_ID,
					virusScannerException.getMessage() + ExceptionUtils.getStackTrace(virusScannerException));
			
			setSuccessResponse(responseDTO, RegistrationConstants.ANTIVIRUS_SERVICE_NOT_ACCESSIBLE, null);
			
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - PACKET_SCAN_IOEXCEPTION", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setCode("IOException");
			errorResponseDTO.setMessage("Error in reading the file");
			errorList.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorList);
		}
		LOGGER.info("REGISTRATION - PACKET_SCAN_END - REGISTRATION_PACKET_VIRUS_SCAN", APPLICATION_NAME, APPLICATION_ID,
				"Scanning of Virus Packet End");
		return responseDTO;
	}

	/**
	 * This method will get the folder path and return the list of which are present
	 * inside the folder
	 * 
	 * @param folderPath
	 * @param filesList
	 * @return
	 */
	private List<File> getFilesFromFolders(String folderPath, List<File> filesList) {
		File directory = FileUtils.getFile(folderPath);

		// Get all files from a directory.
		File[] filesToScan = directory.listFiles();
		if (filesToScan != null)
			for (File fileToScan : filesToScan) {
				if (fileToScan.isFile()) {
					filesList.add(fileToScan);
				} else if (fileToScan.isDirectory()) {
					getFilesFromFolders(fileToScan.getAbsolutePath(), filesList);
				}
			}
		return filesList;

	}
}
