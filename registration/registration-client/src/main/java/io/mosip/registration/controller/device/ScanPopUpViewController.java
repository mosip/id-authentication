package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_CAPTURE_CONTROLLER;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_SCAN_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.DocumentScanController;
import io.mosip.registration.mdm.service.impl.MosipBioDeviceManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class ScanPopUpViewController extends BaseController {
	private static final Logger LOGGER = AppConfig.getLogger(ScanPopUpViewController.class);

	@Autowired
	private BaseController baseController;
	
	@Autowired
	private DocumentScanController documentScanController;
	
	@Autowired
	private MosipBioDeviceManager mosipBioDeviceManager;

	@FXML
	private ImageView scanImage;

	@FXML
	private Label popupTitle;

	@FXML
	private Text totalScannedPages;

	@FXML
	private Button saveBtn;

	@FXML
	private Label scannedPagesLabel;

	@FXML
	private Text scanningMsg;

	private boolean isDocumentScan;
	
	private InputStream urlStream;
	
	private boolean isRunning=true;
	
	private final String CONTENT_LENGTH = "Content-Length:";


	public void streamer(String bioType) {
		new Thread(new Runnable() {
			
		    public void run() {
		    	
		    	urlStream  = mosipBioDeviceManager.stream(bioType);
		    	
		        while (isRunning) {
		            try {
		                byte[] imageBytes = retrieveNextImage();
		                ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);

		                scanImage.setImage(new Image(imageStream));
		            } catch (SocketTimeoutException ste) {
		                stop();

		            } catch (IOException e) {
		                System.err.println("failed stream read: " + e);
		                stop();
		            }
		        }

		        // close streams
		        try {
		            urlStream.close();
		        } catch (IOException ioe) {
		            System.err.println("Failed to close the stream: " + ioe);
		        }
		    }

		}).start();
		
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
                    contentLength = Integer.parseInt(contentLengthStringWriter.toString());
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
        int sI;
        while ((sI=urlStream.read()) != 255) {
            System.out.print(sI);
        }

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
    private synchronized void stop() {
        isRunning = false;
    }

	
	/**
	 * @return the scanImage
	 */
	public ImageView getScanImage() {
		return scanImage;
	}

	private Stage popupStage;

	/**
	 * This method will open popup to scan
	 * 
	 * @param parentControllerObj
	 * @param title
	 */
	public void init(BaseController parentControllerObj, String title) {

		try {

			LOGGER.info(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to scan for user registration");

			baseController = parentControllerObj;
			popupStage = new Stage();
			popupStage.initStyle(StageStyle.UNDECORATED);
			Parent scanPopup = BaseController.load(getClass().getResource(RegistrationConstants.SCAN_PAGE));
			popupStage.setResizable(false);
			popupTitle.setText(title);
			Scene scene = new Scene(scanPopup);
			scene.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			popupStage.setScene(scene);
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(fXComponents.getStage());
			popupStage.show();

			if (!isDocumentScan) {
				totalScannedPages.setVisible(false);
				saveBtn.setVisible(false);
				scannedPagesLabel.setVisible(false);
				scanningMsg.setVisible(false);
			} else {
				isDocumentScan = false;
			}
			LOGGER.info(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Opening pop-up screen to scan for user registration");

		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"%s -> Exception while Opening pop-up screen to capture in user registration  %s -> %s",
							RegistrationConstants.USER_REG_SCAN_EXP, ioException.getMessage(),
							ExceptionUtils.getStackTrace(ioException)));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_SCAN_POPUP);
		}

	}

	/**
	 * This method will allow to scan
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	@FXML
	public void scan() throws MalformedURLException, IOException {
		scanningMsg.setVisible(true);
		LOGGER.info(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Invoke scan method for the passed controller");
		baseController.scan(popupStage);
	}

	/**
	 * event class to exit from present pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {

		LOGGER.info(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Calling exit window to close the popup");

		popupStage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		popupStage.close();
		
		if (documentScanController.getScannedPages() != null) {
			documentScanController.getScannedPages().clear();
		}

		LOGGER.info(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Popup is closed");

	}

	@FXML
	private void save() {
		if (baseController instanceof DocumentScanController) {
			DocumentScanController documentScanController = (DocumentScanController) baseController;
			try {
				documentScanController.attachScannedDocument(popupStage);
			} catch (IOException ioException) {
				LOGGER.error(LOG_REG_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(ioException));
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
			}
		}

	}

	public boolean isDocumentScan() {
		return isDocumentScan;
	}

	public void setDocumentScan(boolean isDocumentScan) {
		this.isDocumentScan = isDocumentScan;
	}

	public Text getTotalScannedPages() {
		return totalScannedPages;
	}

	public void setTotalScannedPages(Text totalScannedPages) {
		this.totalScannedPages = totalScannedPages;
	}

	public Text getScanningMsg() {
		return scanningMsg;
	}

	public void setScanningMsg(Text scanningMsg) {
		this.scanningMsg = scanningMsg;
	}

}
