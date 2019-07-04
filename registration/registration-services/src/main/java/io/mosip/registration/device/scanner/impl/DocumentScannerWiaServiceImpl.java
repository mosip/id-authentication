package io.mosip.registration.device.scanner.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_DOC_SCAN_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.device.scanner.wia.ClassFactory;
import io.mosip.registration.device.scanner.wia.ICommonDialog;
import io.mosip.registration.device.scanner.wia.IImageFile;
import io.mosip.registration.device.scanner.wia.WiaDeviceType;
import io.mosip.registration.device.scanner.wia.WiaImageBias;
import io.mosip.registration.device.scanner.wia.WiaImageIntent;

/**
 * This class is used to provide document scanner functionalities for windows
 * platform through WIA service
 * 
 * @author balamurugan.ramamoorthy
 * @since 1.0.0
 *
 */
@Service
public class DocumentScannerWiaServiceImpl extends DocumentScannerService {

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
		try {
			ICommonDialog wiaObj = ClassFactory.createCommonDialog();
			IImageFile iImageFile = wiaObj.showAcquireImage(WiaDeviceType.ScannerDeviceType,
					WiaImageIntent.GrayscaleIntent, WiaImageBias.MinimizeSize, "{00000000-0000-0000-0000-000000000000}",
					false, false, false);

			String tempFilePath = File.createTempFile("tempImgFile", ".jpg").getAbsolutePath();
			new File(tempFilePath).delete();

			iImageFile.saveFile(tempFilePath);

			bufferedImage = ImageIO.read(new File(tempFilePath));
			new File(tempFilePath).delete();

		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(ioException));
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(runtimeException));
		}
		return bufferedImage;
	}

}
