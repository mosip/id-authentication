package io.mosip.registration.device.webcam.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.device.webcam.MosipWebcamServiceImpl;

/**
 * class to access the webcam and its functionalities.
 *
 * @author Himaja Dhanyamraju
 */
@Component
public class WebcamSarxosServiceImpl extends MosipWebcamServiceImpl {
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(WebcamSarxosServiceImpl.class);

	private Webcam webcam;

	@Override
	public JPanel getCameraPanel() {
		WebcamPanel cameraPanel = new WebcamPanel(webcam);
		JPanel jPanelWindow = new JPanel();
		jPanelWindow.add(cameraPanel);
		jPanelWindow.setVisible(true);
		return jPanelWindow;
	}

	@Override
	public boolean isWebcamConnected() {
		return (webcam != null) ? true : false;
	}

	@Override
	public void connect(int width, int height) {
		LOGGER.info("REGISTRATION - WEBCAMDEVICE", APPLICATION_NAME, APPLICATION_ID, "connecting to webcam");
		List<Webcam> webcams = Webcam.getWebcams();
		if (!webcams.isEmpty()) {
			if (webcams.get(0).getName().toLowerCase().contains("integrated")) {
				if (webcams.size() > 1) {
					webcam = webcams.get(1);
				} else {
					webcam = webcams.get(0);
					// return null;
				}
			} else {
				webcam = webcams.get(0);
			}
			Dimension requiredDimension = new Dimension(width, height);
			webcam.setViewSize(requiredDimension);
			webcam.open();
			Webcam.getDiscoveryService().stop();
		}
	}

	@Override
	public BufferedImage captureImage() {
		LOGGER.info("REGISTRATION - WEBCAMDEVICE", APPLICATION_NAME, APPLICATION_ID, "capturing the image from webcam");
		return webcam.getImage();
	}

	@Override
	public void close() {
		LOGGER.info("REGISTRATION - WEBCAMDEVICE", APPLICATION_NAME, APPLICATION_ID, "closing the webcam");
		webcam.close();
	}
}
