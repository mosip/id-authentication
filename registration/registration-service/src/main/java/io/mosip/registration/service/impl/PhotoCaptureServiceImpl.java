package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;

import org.springframework.stereotype.Service;

import com.github.sarxos.webcam.Webcam;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.service.PhotoCaptureService;
import io.mosip.registration.util.webcam.WebcamDevice;
import io.mosip.registration.util.webcam.WebcamDeviceImpl;

/**
 * Service class to access the webcam and its functionalities.
 *
 * @author Himaja Dhanyamraju
 */	
@Service
public class PhotoCaptureServiceImpl implements PhotoCaptureService {
	
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(PhotoCaptureServiceImpl.class);
	
	private WebcamDevice webcamDeviceImpl = new WebcamDeviceImpl();

	@Override
	public Webcam connect(int width, int height) {
		LOGGER.debug("REGISTRATION - PHOTO_CAPTURE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"connecting to webcam");
		return webcamDeviceImpl.connect(width, height);
	}

	@Override
	public BufferedImage captureImage(Webcam webcam) {
		LOGGER.debug("REGISTRATION - PHOTO_CAPTURE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"capturing the image from webcam");
		return webcamDeviceImpl.captureImage(webcam);
	}

	@Override
	public void close(Webcam webcam) {
		LOGGER.debug("REGISTRATION - PHOTO_CAPTURE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"closing the webcam");
		webcamDeviceImpl.close(webcam);
	}

}
