package io.mosip.registration.util.scan;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.device.scanner.IMosipDocumentScannerService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * Facade Class to help connect the document scanner implementation
 * 
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 */
@Component
public class DocumentScanFacade {

	private IMosipDocumentScannerService documentScannerService;

	private List<IMosipDocumentScannerService> documentScannerServices;

	private static final Logger LOGGER = AppConfig.getLogger(DocumentScanFacade.class);

	/**
	 * gets the sample scanned document
	 * 
	 * @return byte[] - image file in bytes
	 * @throws IOException
	 *             - holds the ioexception
	 */
	public byte[] getScannedDocument() throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		InputStream inputStream = this.getClass().getResourceAsStream(RegistrationConstants.DOC_STUB_PATH);

		byte[] byteArray = new byte[inputStream.available()];
		inputStream.read(byteArray);

		return byteArray;

	}

	/**
	 * gets all the possible scanner implementations
	 * 
	 * @param documentScannerServices
	 *            - list that holds the scanner impl details
	 */
	@Autowired
	public void setFingerprintProviders(List<IMosipDocumentScannerService> documentScannerServices) {
		this.documentScannerServices = documentScannerServices;
	}

	/**
	 * Checks the platform and selects the scanner implementation accordingly
	 * 
	 * @return boolean - sets the scanner factory based on the os platform
	 */
	public boolean setScannerFactory() {
		String factoryName = "";

		if (RegistrationAppHealthCheckUtil.isWindows()) {
			factoryName = "wia";
		} else if (RegistrationAppHealthCheckUtil.isLinux()) {
			factoryName = "sane";
		}

		for (IMosipDocumentScannerService documentScannerService : documentScannerServices) {
			if (documentScannerService.getClass().getName().toLowerCase().contains(factoryName.toLowerCase())) {
				this.documentScannerService = documentScannerService;
				return true;
			}
		}
		return false;

	}

	/**
	 * scans the document from the scanner
	 * 
	 * @return BufferedImage- scanned file
	 * @throws IOException
	 *             - holds the ioexception
	 */
	public BufferedImage getScannedDocumentFromScanner() throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.scan();

	}

	/**
	 * converts Buffredimage to byte[]
	 * 
	 * @param bufferedImage
	 *            - scanned file
	 * @return byte[] - holds the image data in bytes
	 * @throws IOException
	 *             - holds the ioexception
	 */
	public byte[] getImageBytesFromBufferedImage(BufferedImage bufferedImage) throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.getImageBytesFromBufferedImage(bufferedImage);

	}

	/**
	 * converts all the captured scanned docs to single image file
	 * 
	 * @param bufferedImages
	 *            - scanned files
	 * @return byte[] - image in bytes
	 * @throws IOException
	 *             - holds the ioexception
	 */
	public byte[] asImage(List<BufferedImage> bufferedImages) throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.asImage(bufferedImages);

	}

	/**
	 * converts all the captured scanned docs to single pdf file
	 * 
	 * @param bufferedImages
	 *            - scanned files
	 * @return byte[] - pdf file in bytes
	 * @throws IOException
	 *             - holds the ioexception
	 */
	public byte[] asPDF(List<BufferedImage> bufferedImages) throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.asPDF(bufferedImages);

	}

	/**
	 * converts single pdf file into list of images in order show it in the doc
	 * preview
	 * 
	 * @param pdfBytes
	 *            - pdf in bytes
	 * @return List - list of image files
	 * @throws IOException
	 *             - holds the ioexception
	 */
	public List<BufferedImage> pdfToImages(byte[] pdfBytes) throws IOException {

		return documentScannerService.pdfToImages(pdfBytes);
	}

	/**
	 * checks the scanner connectivity
	 * 
	 * @return boolean - true if connected or else false
	 */
	public boolean isConnected() {
		return documentScannerService.isConnected();

	}

}
