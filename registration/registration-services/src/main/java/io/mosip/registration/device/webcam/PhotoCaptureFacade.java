package io.mosip.registration.device.webcam;

import java.awt.image.BufferedImage;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.sarxos.webcam.Webcam;

import io.mosip.registration.device.webcam.impl.LogitechPhotoProvider;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author Himaja Dhanyamraju
 */
@Component
public class PhotoCaptureFacade extends LogitechPhotoProvider {

	@Autowired
	private MosipWebcamProvider webcamProvider;
	
	private List<MosipWebcamProvider> webCamProviders;
	
	public MosipWebcamProvider getPhotoProviderFactory(String make) {
		for (MosipWebcamProvider mosipWebcamProvider : webCamProviders) {
			if (mosipWebcamProvider.getClass().getName().toLowerCase().contains(make.toLowerCase())) {
				webcamProvider = mosipWebcamProvider;
			}
		}
		return webcamProvider;
	}

	@Autowired
	public void setWebCamProviders(List<MosipWebcamProvider> mosipWebcamProvider) {
		this.webCamProviders = mosipWebcamProvider;
	}

	public Webcam connect(int width, int height) {
		return webcamProvider.connect(width, height);
	}

	public BufferedImage captureImage(Webcam webcam) {
		return webcamProvider.captureImage(webcam);
	}

	public void close(Webcam webcam) {
		webcamProvider.close(webcam);
	}

}
