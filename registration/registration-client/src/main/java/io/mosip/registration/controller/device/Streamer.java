package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.LoggerConstants.STREAMER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.RequestDetail;
import io.mosip.registration.mdm.service.impl.MosipBioDeviceManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Component
public class Streamer {

	private static final Logger LOGGER = AppConfig.getLogger(Streamer.class);

	private InputStream urlStream;

	private boolean isRunning = true;

	private final String CONTENT_LENGTH = "Content-Length:";

	@Autowired
	private MosipBioDeviceManager mosipBioDeviceManager;

	@Autowired
	private ScanPopUpViewController scanPopUpViewController;

	private Thread streamer_thread = null;

	public byte[] imageBytes = null;

	// Last streaming image
	private static Image streamImage;

	// Image View, which UI need to be shown
	private static ImageView imageView;

	// Set Streaming image
	public void setStreamImage(Image streamImage) {
		this.streamImage = streamImage;
	}

	// Set ImageView
	public static void setImageView(ImageView imageView) {
		Streamer.imageView = imageView;
	}

	// Set Streaming image to ImageView
	public void setStreamImageToImageView() {
		imageView.setImage(streamImage);
	}


	public void startStream(RequestDetail requestDetail, ImageView streamImage, ImageView scanImage) {
  
  LOGGER.info(STREAMER, APPLICATION_NAME, APPLICATION_ID, "Streamer Thread initiation started for : " + requestDetail.getType());

		streamer_thread = new Thread(new Runnable() {

			public void run() {

				LOGGER.info(STREAMER, APPLICATION_NAME, APPLICATION_ID, "Streamer Thread started for : " + requestDetail.getType());

				scanPopUpViewController.disableCloseButton();
				isRunning = true;
				try {
					if (urlStream != null) {
						urlStream.close();
						urlStream = null;
					}

					setPopViewControllerMessage(true, RegistrationUIConstants.STREAMING_PREP_MESSAGE, false);

					urlStream = mosipBioDeviceManager.stream(requestDetail);
					if (urlStream == null) {

						LOGGER.info(STREAMER, APPLICATION_NAME, APPLICATION_ID, "URL Stream was null for : " + requestDetail.getType());

						setPopViewControllerMessage(true,
								RegistrationUIConstants.getMessageLanguageSpecific("202_MESSAGE"), false);

						return;
					}

					setPopViewControllerMessage(true, RegistrationUIConstants.STREAMING_INIT_MESSAGE, true);

				} catch (RegBaseCheckedException | IOException | NullPointerException exception) {

					LOGGER.error(STREAMER, RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
							exception.getMessage() + ExceptionUtils.getStackTrace(exception));
					try {

						// Refreshing Device info, for checking of new connection
						mosipBioDeviceManager.refreshBioDeviceByDeviceType(requestDetail.getType());

						// Start stream with new device
						urlStream = mosipBioDeviceManager.stream(requestDetail);
						if (urlStream == null) {

							LOGGER.info(STREAMER, APPLICATION_NAME, APPLICATION_ID,
									"URL Stream was null for : " + requestDetail.getType());

							setPopViewControllerMessage(true,
									RegistrationUIConstants.getMessageLanguageSpecific("202_MESSAGE"), false);

							return;
						}
						setPopViewControllerMessage(true, RegistrationUIConstants.STREAMING_INIT_MESSAGE, true);

					} catch (RegBaseCheckedException | IOException regBaseCheckedException) {

						LOGGER.error(STREAMER, RegistrationConstants.APPLICATION_NAME,
								RegistrationConstants.APPLICATION_ID, regBaseCheckedException.getMessage()
										+ ExceptionUtils.getStackTrace(regBaseCheckedException));

						setPopViewControllerMessage(true,
								RegistrationUIConstants.getMessageLanguageSpecific("202_MESSAGE"), false);
					}

				}
				while (isRunning && null != urlStream) {
					try {
						imageBytes = retrieveNextImage();
						ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
						Image img = new Image(imageStream);
						streamImage.setImage(img);
						if (null != scanImage) {
							// scanImage.setImage(img);

							setImageView(scanImage);
							setStreamImage(img);
						}
					} catch (RuntimeException | IOException exception) {

						
						LOGGER.error(STREAMER, RegistrationConstants.APPLICATION_NAME,
								RegistrationConstants.APPLICATION_ID, exception.getMessage()
										+ ExceptionUtils.getStackTrace(exception));

						if(exception.getMessage().contains("Stream closed")){
							setPopViewControllerMessage(true, RegistrationUIConstants.STREAMING_CLOSED_MESSAGE, false);
						}
						    
						urlStream = null;
						isRunning = false;


					}
				}
			}

			
		}, "STREAMER_THREAD");

		streamer_thread.start();

		LOGGER.info(STREAMER, APPLICATION_NAME, APPLICATION_ID, "Streamer Thread initiated completed for : " + requestDetail.getType());

	}

	/**
	 * Using the urlStream get the next JPEG image as a byte[]
	 *
	 * @return byte[] of the JPEG
	 * @throws IOException
	 */
	private byte[] retrieveNextImage() throws IOException {

		int currByte = -1;

		boolean captureContentLength = false;
		StringWriter contentLengthStringWriter = new StringWriter(128);
		StringWriter headerWriter = new StringWriter(128);

		int contentLength = 0;

		while ((currByte = urlStream.read()) > -1) {
			if (captureContentLength) {
				if (currByte == 10 || currByte == 13) {
					contentLength = Integer.parseInt(contentLengthStringWriter.toString().replace(" ", ""));
					break;
				}
				contentLengthStringWriter.write(currByte);

			} else {
				headerWriter.write(currByte);
				String tempString = headerWriter.toString();
				int indexOf = tempString.indexOf(CONTENT_LENGTH);
				if (indexOf > 0) {
					captureContentLength = true;
				}
			}
		}

		// 255 indicates the start of the jpeg image
		while (urlStream.read() != 255) {

		}

		// && urlStream.read()!=-1
		// if(urlStream.read()==-1) {
		// throw new RuntimeException("No stream available");
		// }

		// rest is the buffer
		byte[] imageBytes = new byte[contentLength + 1];
		// since we ate the original 255 , shove it back in
		imageBytes[0] = (byte) 255;
		int offset = 1;
		int numRead = 0;
		while (offset < imageBytes.length
				&& (numRead = urlStream.read(imageBytes, offset, imageBytes.length - offset)) >= 0) {
			offset += numRead;
		}

		return imageBytes;
	}

	/**
	 * Stop the loop, and allow it to clean up
	 */
	public synchronized void stop() {

		if (streamer_thread != null) {
			try {
				isRunning = false;
				if (urlStream != null)
					urlStream.close();
				streamer_thread = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setPopViewControllerMessage(boolean enableCloseButton, String message, boolean isRunning) {
		if (enableCloseButton) {
			scanPopUpViewController.enableCloseButton();
		}
		scanPopUpViewController.setScanningMsg(message);
		this.isRunning = isRunning;
	}

}
