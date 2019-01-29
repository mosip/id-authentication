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
import io.mosip.registration.device.scanner.DocumentScannerService;

@Component
public class DocumentScanFacade {

	@Autowired
	DocumentScannerService documentScannerService;

	private static final Logger LOGGER = AppConfig.getLogger(DocumentScanFacade.class);

	public byte[] getScannedDocument() throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		InputStream inputStream = this.getClass().getResourceAsStream(RegistrationConstants.DOC_STUB_PATH);

		byte[] byteArray = new byte[inputStream.available()];
		inputStream.read(byteArray);

		return byteArray;

	}

	public BufferedImage getScannedDocumentFromScanner() throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.scan();

	}

	public byte[] getImageBytesFromBufferedImage(BufferedImage bufferedImage) throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.getImageBytesFromBufferedImage(bufferedImage);

	}

	public byte[] asImage(List<BufferedImage> bufferedImages) throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.asImage(bufferedImages);

	}

	public byte[] asPDF(List<BufferedImage> bufferedImages) throws IOException {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.asPDF(bufferedImages);

	}

	public boolean isConnected() {
		return documentScannerService.isConnected();

	}

}
