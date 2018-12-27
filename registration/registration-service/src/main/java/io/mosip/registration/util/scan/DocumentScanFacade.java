package io.mosip.registration.util.scan;

import java.io.IOException;
import java.io.InputStream;

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

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		InputStream inputStream = this.getClass().getResourceAsStream(RegistrationConstants.DOC_STUB_PATH);

		byte[] byteArray = new byte[inputStream.available()];
		inputStream.read(byteArray);

		return byteArray;

	}

	public byte[] getScannedDocumentFromScanner() throws IOException {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Redaing byte array from Scanner");

		return documentScannerService.scanDocument();

	}

}
