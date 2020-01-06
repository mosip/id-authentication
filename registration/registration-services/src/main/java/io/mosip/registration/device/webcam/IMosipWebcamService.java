package io.mosip.registration.device.webcam;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.github.sarxos.webcam.WebcamPanel;

/**
 * Interface to access the webcam and its functionalities.
 *
 * @author Himaja Dhanyamraju
 */
public interface IMosipWebcamService {

	/**
	 * This method is to get the JPanel to which WebcamPanel is added to open the
	 * web-camera.
	 * 
	 * <p>
	 * It opens the {@link WebcamPanel} and adds it to the {@link JPanel} which is a
	 * light-weight container inside which the web-camera gets opened.
	 * </p>
	 * 
	 * @return JPanel - the {@link JPanel} to which {@link WebcamPanel} is added.
	 */
	public JPanel getCameraPanel();

	/**
	 * This method returns true/false based on webcam connectivity.
	 * 
	 * @return boolean - if webcam is connected, it returns true, otherwise, false
	 */
	public boolean isWebcamConnected();

	/**
	 * This method is to open the first on-boarded camera from the list of webcams
	 * with specified resolution.
	 * 
	 * <p>
	 * This method takes the list of webcam devices that are connected and opens the
	 * device with the specified name. If the specified webcam is not found, it
	 * opens the integrated/default web-camera if available.
	 * </p>
	 * 
	 * @param width
	 *            - Required width for the camera to be set-up.
	 * @param height
	 *            - Required height for the camera to be set-up.
	 */
	public void connect(int width, int height);

	/**
	 * This method captures the image from webcam and return it.
	 * 
	 * <p>
	 * It will return image object or null if webcam is closed or has been already
	 * disposed by JVM.
	 * </p>
	 * 
	 * @return returns the image object that is captured from the webcam as {@link BufferedImage}.
	 */
	public BufferedImage captureImage();

	/**
	 * This method is to close the webcam which is open
	 */
	public void close();

}
