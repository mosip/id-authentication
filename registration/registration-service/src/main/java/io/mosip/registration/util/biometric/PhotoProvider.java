package io.mosip.registration.util.biometric;

import java.awt.image.BufferedImage;

import org.springframework.stereotype.Component;

import com.github.sarxos.webcam.Webcam;

/**
 * common class for webcam methods
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Component
public abstract class PhotoProvider implements MosipWebcamProvider {

	@Override
	public abstract Webcam connect(int width, int height);

	@Override
	public abstract BufferedImage captureImage(Webcam webcam);

	@Override
	public abstract void close(Webcam webcam);

}
