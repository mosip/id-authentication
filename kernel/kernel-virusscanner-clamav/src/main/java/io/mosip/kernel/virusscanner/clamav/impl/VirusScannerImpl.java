package io.mosip.kernel.virusscanner.clamav.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.kernel.virusscanner.clamav.constant.VirusScannerErrorCodes;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;
import xyz.capybara.clamav.commands.scan.result.ScanResult.Status;
import xyz.capybara.clamav.exceptions.ClamavException;

// TODO: Auto-generated Javadoc
/**
 * The implementation Class for VirusScannerService.
 *
 * @author Mukul Puspam
 * @author Pranav Kumar
 */
@Component
public class VirusScannerImpl implements VirusScanner<Boolean, InputStream> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(VirusScannerImpl.class);

	/** The host. */
	@Value("${mosip.kernel.virus-scanner.host}")
	private String host;

	/** The port. */
	@Value("${mosip.kernel.virus-scanner.port}")
	private int port;

	/** The clamav client. */
	protected ClamavClient clamavClient;

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The Constant ANTIVIRUS_SERVICE_NOT_ACCESSIBLE. */
	private static final String ANTIVIRUS_SERVICE_NOT_ACCESSIBLE = "The anti virus service is not accessible";

	/** The Constant FILE_NOT_PRESENT. */
	private static final String FILE_NOT_PRESENT = "The file not found for for scanning";

	/**
	 * Creates the connection to client.
	 */
	public void createConnection() {
		if (this.clamavClient == null)
			this.clamavClient = new ClamavClient(host, port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.idissuance.virus.scanner.service.VirusScannerService#scanFile(java.
	 * lang.Object)
	 */
	@Override
	public Boolean scanFile(String fileName) {
		Boolean result = Boolean.FALSE;
		createConnection();
		File file = new File(fileName);
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			throw new VirusScannerException(VirusScannerErrorCodes.IIS_EPP_EPV_FILE_NOT_PRESENT, FILE_NOT_PRESENT, e1);
		}
		try {
			ScanResult scanResult = this.clamavClient.scan(is);
			if (scanResult.getStatus() == Status.OK) {
				result = Boolean.TRUE;
			} else {
				Map<String, Collection<String>> listOfVirus = scanResult.getFoundViruses();
				LOGGER.warn("Virus Found in file " + fileName + ": ", listOfVirus);
			}
		} catch (ClamavException e) {
			throw new VirusScannerException(VirusScannerErrorCodes.IIS_EPP_EPV_SERVICE_NOT_ACCESSIBLE,
					ANTIVIRUS_SERVICE_NOT_ACCESSIBLE, e);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.virusscanner.spi.VirusScanner#scanFile(java.io.
	 * InputStream)
	 */
	@Override
	public Boolean scanFile(InputStream is) {
		Boolean result = Boolean.FALSE;
		createConnection();
		try {
			ScanResult scanResult = this.clamavClient.scan(is);
			if (scanResult.getStatus() == Status.OK) {
				result = Boolean.TRUE;
			} else {
				Map<String, Collection<String>> listOfVirus = scanResult.getFoundViruses();
				LOGGER.warn("Virus Found in file : " + listOfVirus);
			}
		} catch (ClamavException e) {
			throw new VirusScannerException(VirusScannerErrorCodes.IIS_EPP_EPV_SERVICE_NOT_ACCESSIBLE,
					ANTIVIRUS_SERVICE_NOT_ACCESSIBLE, e);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.idissuance.virus.scanner.service.VirusScannerService#scanFolder(
	 * java.lang.Object)
	 */
	@Override
	public Boolean scanFolder(String folderPath) {

		Boolean result = Boolean.TRUE;
		createConnection();
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		for (File file : files) {
			try {
				ScanResult scanResult = this.clamavClient.scan(new FileInputStream(file));
				if (scanResult.getStatus() != Status.OK) {
					result = Boolean.FALSE;
					break;
				}
			} catch (FileNotFoundException e) {
				throw new VirusScannerException(VirusScannerErrorCodes.IIS_EPP_EPV_FILE_NOT_PRESENT, FILE_NOT_PRESENT,
						e);
			} catch (ClamavException e) {
				throw new VirusScannerException(VirusScannerErrorCodes.IIS_EPP_EPV_SERVICE_NOT_ACCESSIBLE,
						ANTIVIRUS_SERVICE_NOT_ACCESSIBLE, e);
			}
		}
		return result;
	}

	/**
	 * This Method is used to scan byte array
	 * 
	 * @param docArray
	 *            array
	 * 
	 * @return a true if file is virus free and false if file is infected
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */

	@Override
	public Boolean scanDocument(byte[] docArray) throws IOException {
		Boolean result = Boolean.FALSE;
		InputStream docInputStream = new ByteArrayInputStream(docArray);

		createConnection();
		try {

			ScanResult scanResult = this.clamavClient.scan(docInputStream);
			if (scanResult.getStatus() == Status.OK) {
				result = Boolean.TRUE;
			} else {
				Map<String, Collection<String>> listOfVirus = scanResult.getFoundViruses();
				LOGGER.warn("Virus Found in file " + docInputStream + ": ", listOfVirus);
			}
		} catch (ClamavException e) {
			LOGGER.error(LOGDISPLAY, e.getMessage());
			throw new VirusScannerException(VirusScannerErrorCodes.IIS_EPP_EPV_SERVICE_NOT_ACCESSIBLE,
					ANTIVIRUS_SERVICE_NOT_ACCESSIBLE, e);
		} finally {

			docInputStream.close();
		}

		return result;
	}

	/**
	 * This Method is used to scan File
	 * 
	 * @param doc
	 *            object
	 * 
	 * @return a true if file is virus free and false if file is infected
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public Boolean scanDocument(File doc) throws IOException {
		Boolean result = Boolean.FALSE;

		createConnection();
		try (FileInputStream docInputStream = new FileInputStream(doc)) {

			ScanResult scanResult = this.clamavClient.scan(docInputStream);
			if (scanResult.getStatus() == Status.OK) {
				result = Boolean.TRUE;
			} else {
				Map<String, Collection<String>> listOfVirus = scanResult.getFoundViruses();
				LOGGER.warn("Virus Found in file " + doc + ": ", listOfVirus);
			}
		} catch (ClamavException e) {
			LOGGER.error(LOGDISPLAY, e.getMessage());
			throw new VirusScannerException(VirusScannerErrorCodes.IIS_EPP_EPV_SERVICE_NOT_ACCESSIBLE,
					ANTIVIRUS_SERVICE_NOT_ACCESSIBLE, e);
		}

		return result;
	}

}
