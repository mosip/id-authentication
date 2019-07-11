package io.mosip.registration.device.webcam;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.springframework.stereotype.Component;

/**
 * common class for webcam methods
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Component
public abstract class MosipWebcamServiceImpl implements IMosipWebcamService {
	
	@Override
	public abstract JPanel getCameraPanel();
	
	@Override
	public abstract boolean isWebcamConnected();

	@Override
	public abstract void connect(int width, int height);

	@Override
	public abstract BufferedImage captureImage();

	@Override
	public abstract void close();

}
