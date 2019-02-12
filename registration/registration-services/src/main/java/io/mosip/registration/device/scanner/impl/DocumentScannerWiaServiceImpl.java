package io.mosip.registration.device.scanner.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_DOC_SCAN_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.device.scanner.wia.ClassFactory;
import io.mosip.registration.device.scanner.wia.ICommonDialog;
import io.mosip.registration.device.scanner.wia.IImageFile;
import io.mosip.registration.device.scanner.wia.IVector;
import io.mosip.registration.device.scanner.wia.WiaDeviceType;
import io.mosip.registration.device.scanner.wia.WiaImageBias;
import io.mosip.registration.device.scanner.wia.WiaImageIntent;

@Service
public class DocumentScannerWiaServiceImpl extends DocumentScannerServiceImpl {

	private static final Logger LOGGER = AppConfig.getLogger(DocumentScannerSaneServiceImpl.class);

	@Override
	public boolean isConnected() {

		LOGGER.info(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Scannner idConnected method called");
		return ClassFactory.createDeviceManager().deviceInfos().count() >= 1 ? true : false;
	}

	@Override
	public BufferedImage scan() {

		BufferedImage bufferedImage = null;
		ICommonDialog wiaObj = ClassFactory.createCommonDialog();
		IImageFile iImageFile = wiaObj.showAcquireImage(WiaDeviceType.ScannerDeviceType, WiaImageIntent.GrayscaleIntent,
				WiaImageBias.MinimizeSize, "{00000000-0000-0000-0000-000000000000}", false, false, false);
		try {
			bufferedImage = getBufferedImageFromBytes(scannedImageTobytes(iImageFile));

		} catch (IOException e) {
			LOGGER.error(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, e.getMessage());
		}
		return bufferedImage;
	}

	private byte[] scannedImageTobytes(IImageFile iImageFile) {
		IVector vector = iImageFile.fileData();
		byte[] imageBytes = new byte[vector.count()];
		for (int i = 1; i <= vector.count(); i++) {
			Short sh = (Short) vector.item(i);
			imageBytes[i - 1] = sh.byteValue();
		}
		return imageBytes;
	}

}
