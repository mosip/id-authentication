package io.mosip.registration.device.webcam;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Interface to access the webcam and its functionalities.
 *
 * @author Himaja Dhanyamraju
 */
public interface IMosipWebcamService {
	
	/**
	 * This method is to get the JPanel to which WebcamPanel is added to open the webcamera.
	 * 
	 * @return JPanel - the JPanel to which WebcamPanel is added.
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
	 * @param width  Required width for the camera to be set-up.
	 * @param height Required height for the camera to be set-up.
	 */
	public void connect(int width, int height);

	/**
	 * This method captures the image from webcam and return it. Will return image
	 * object or null if webcam is closed or has been already disposed by JVM.
	 * 
	 * @param webcam The on-boarded camera which is open.
	 * @return BufferedImage returns the image object that is captured from the
	 *         webcam.
	 */
	public BufferedImage captureImage();

	/**
	 * This method is to close the webcam which is open
	 */
	public void close();

}
