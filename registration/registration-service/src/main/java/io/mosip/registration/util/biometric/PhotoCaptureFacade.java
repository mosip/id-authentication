package io.mosip.registration.util.biometric;

import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.sarxos.webcam.Webcam;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author Himaja Dhanyamraju
 */
@Component
public class PhotoCaptureFacade extends LogietchPhotoProvider {

	@Autowired
	private MosipWebcamProvider mosipWebcamProvider;

	public MosipWebcamProvider getPhotoProviderFactory(String make) {
		mosipWebcamProvider = null;
		if (make.equals("Logitech")) {
			mosipWebcamProvider = new LogietchPhotoProvider();
		}
		return mosipWebcamProvider;
	}

	public Webcam connect(int width, int height) {
		return mosipWebcamProvider.connect(width, height);
	}

	public BufferedImage captureImage(Webcam webcam) {
		return mosipWebcamProvider.captureImage(webcam);
	}

	public void close(Webcam webcam) {
		mosipWebcamProvider.close(webcam);
	}

}
