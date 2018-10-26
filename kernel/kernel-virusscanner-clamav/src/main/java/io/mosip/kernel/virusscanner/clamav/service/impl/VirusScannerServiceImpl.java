package io.mosip.kernel.virusscanner.clamav.service.impl;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.virusscanner.clamav.exception.ServerNotAccessibleException;
import io.mosip.kernel.virusscanner.clamav.service.VirusScannerService;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;
import xyz.capybara.clamav.commands.scan.result.ScanResult.Status;
import xyz.capybara.clamav.exceptions.ClamavException;

/**
 * The implementation Class for VirusScannerService.
 *
 * @author Mukul Puspam
 */
@Component
public class VirusScannerServiceImpl implements VirusScannerService<Boolean, String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(VirusScannerServiceImpl.class);

	@Value("${clamav.host}")
	private String host;

	@Value("${clamav.port}")
	private int port;

	protected ClamavClient clamavClient;

	private static final String LOGDISPLAY = "{} - {}";

	private static final String ANTIVIRUS_SERVICE_NOT_ACCESSIBLE = "The anti virus service is not accessible";

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
		try {
			ScanResult scanResult = this.clamavClient.scan(Paths.get(fileName));
			if (scanResult.getStatus() == Status.OK) {
				result = Boolean.TRUE;
			} else {
				Map<String, Collection<String>> listOfVirus = scanResult.getFoundViruses();
				LOGGER.warn("Virus Found in file " + fileName + ": ", listOfVirus);
			}
		} catch (ClamavException e) {
			LOGGER.error(LOGDISPLAY, e.getMessage());
			throw new ServerNotAccessibleException(ANTIVIRUS_SERVICE_NOT_ACCESSIBLE);
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

		Boolean result = Boolean.FALSE;
		createConnection();
		try {
			ScanResult scanResult = this.clamavClient.scan(Paths.get(folderPath), false);
			if (scanResult.getStatus() == Status.OK) {
				result = Boolean.TRUE;
			} else {
				Map<String, Collection<String>> listOfVirus = scanResult.getFoundViruses();
				LOGGER.warn("Virus Found in folder " + folderPath + ": ", listOfVirus);
			}
		} catch (ClamavException e) {
			LOGGER.error(LOGDISPLAY, e.getMessage());
			throw new ServerNotAccessibleException(ANTIVIRUS_SERVICE_NOT_ACCESSIBLE);
		}

		return result;
	}

}
