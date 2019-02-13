package io.mosip.registration.device.scanner.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_DOC_SCAN_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SaneSession;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;

/**
 * This class is used to provide document scanner functionalities for Linux
 * platform through SANE Daemon service
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Service
public class DocumentScannerSaneServiceImpl extends DocumentScannerService {

	private static final Logger LOGGER = AppConfig.getLogger(DocumentScannerSaneServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.device.scanner.impl.DocumentScannerService#
	 * isScannerConnected()
	 */
	@Override
	public boolean isConnected() {

		return isListNotEmpty(getScannerDevices());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.device.scanner.impl.DocumentScannerService#scanDocument
	 * ()
	 */
	@Override
	public BufferedImage scan() {

		BufferedImage bufferedImage = null;
		try {
			SaneSession session = SaneSession.withRemoteSane(InetAddress.getByName(scannerhost), scannerTimeout,
					TimeUnit.MILLISECONDS);
			List<SaneDevice> saneDevices = session.listDevices();
			if (isListNotEmpty(saneDevices)) {
				SaneDevice saneDevice = saneDevices.get(0);

				saneDevice.open();

				setScannerSettings(saneDevice);
				bufferedImage = saneDevice.acquireImage();

				saneDevice.close();
				session.close();
			}
		} catch (IOException | SaneException e) {
			LOGGER.error(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, e.getMessage());
		}
		return bufferedImage;
	}

	/**
	 * This method is used to set the scanner settings for the given scanner device
	 * 
	 * @param saneDevice
	 *            - the scanner device
	 * @throws IOException
	 * @throws SaneException
	 */
	private void setScannerSettings(SaneDevice saneDevice) throws IOException, SaneException {
		/* setting the resolution in dpi for the quality of the document */
		saneDevice.getOption("resolution").setIntegerValue(scannerDpi);

		saneDevice.getOption("brightness").setIntegerValue(scannerBrightness);

		saneDevice.getOption("contrast").setIntegerValue(scannerContrast);

		saneDevice.getOption("depth").setIntegerValue(scannerDepth);

	}

	/**
	 * This method is used to get the lists of scanners connected to the machine
	 * 
	 * @return List<SaneDevice> - The list of connected scanner devices
	 */
	private List<SaneDevice> getScannerDevices() {
		List<SaneDevice> saneDevices = null;
		try {
			SaneSession session = SaneSession.withRemoteSane(InetAddress.getByName(scannerhost), scannerTimeout,
					TimeUnit.MILLISECONDS);
			saneDevices = session.listDevices();
			session.close();
		} catch (IOException | SaneException e) {
			LOGGER.error(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, e.getMessage());
		}
		return saneDevices;
	}
}
