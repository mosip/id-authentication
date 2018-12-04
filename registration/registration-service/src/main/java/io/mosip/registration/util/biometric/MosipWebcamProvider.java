package io.mosip.registration.util.biometric;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

/**
 * Interface to access the webcam and its functionalities.
 *
 * @author Himaja Dhanyamraju
 */
public interface MosipWebcamProvider {

	/**
	 * This method is to open the first on-boarded camera from the list of webcams
	 * with specified resolution.
	 * 
	 * @param width  Required width for the camera to be set-up.
	 * @param height Required height for the camera to be set-up.
	 * @return Webcam returns the webcam that is set-up and opened with the
	 *         specified width and height.
	 */
	public Webcam connect(int width, int height);

	/**
	 * This method captures the image from webcam and return it. Will return image
	 * object or null if webcam is closed or has been already disposed by JVM.
	 * 
	 * @param webcam The on-boarded camera which is open.
	 * @return BufferedImage returns the image object that is captured from the
	 *         webcam.
	 */
	public BufferedImage captureImage(Webcam webcam);

	/**
	 * This method is to close the webcam.
	 * 
	 * @param webcam The on-boarded camera which is open.
	 */
	public void close(Webcam webcam);

}
