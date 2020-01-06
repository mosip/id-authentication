package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.biometric.BiometricDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * {@code UserOnboardController} is to initialize user onboard 
 * 
 * @author Dinesh Ashokan
 * @version 1.0
 *
 */
@Controller
public class UserOnboardController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnboardController.class);

	@FXML
	private Label operatorName;
	@FXML
	private GridPane getOnboardedPane;
	@FXML
	private ImageView getOnboardedImageView;
	@FXML
	private GridPane onboardGridPane;
	@FXML
	private ImageView onboardImageView;
	@FXML
	private GridPane registerGridPane;
	@FXML
	private ImageView registerImageView;
	@FXML
	private GridPane syncDataGridPane;
	@FXML
	private ImageView syncDataImageView;
	@FXML
	private GridPane mapDevicesGridPane;
	@FXML
	private ImageView mapDevicesImageView;
	@FXML
	private GridPane uploadDataGridPane;
	@FXML
	private ImageView uploadDataImageView;
	@FXML
	private GridPane updateBiometricsGridPane;
	@FXML
	private ImageView updateBiometricsImageView;

	@Autowired
	private UserOnboardParentController userOnboardParentController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setImagesOnHover();
		operatorName.setText(RegistrationUIConstants.USER_ONBOARD_HI + " " + SessionContext.userContext().getName()
				+ ", " + RegistrationUIConstants.USER_ONBOARD_NOTONBOARDED);
	}

	@FXML
	public void initUserOnboard() {
		clearOnboard();
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		SessionContext.map().put(RegistrationConstants.USER_ONBOARD_DATA, biometricDTO);
		SessionContext.map().put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ, RegistrationConstants.DISABLE);
		userOnboardParentController.showCurrentPage(RegistrationConstants.ONBOARD_USER_PARENT,
				getOnboardPageDetails(RegistrationConstants.ONBOARD_USER_PARENT, RegistrationConstants.NEXT));
		clearAllValues();
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "User Onboard Controller initUserOnboard Method Exit");
	}

	public void clearOnboard() {
		SessionContext.map().remove(RegistrationConstants.USER_ONBOARD_DATA);
		SessionContext.map().remove(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION);
		SessionContext.map().remove(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);
	}
	
	@FXML
	public void onboardingYourself() {
		onboardGridPane.setOnMouseClicked(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(RegistrationConstants.MOSIP_URL));
		        } catch (IOException ioException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		        } catch (URISyntaxException uriSyntaxException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
		        			uriSyntaxException.getMessage() + ExceptionUtils.getStackTrace(uriSyntaxException));
		        }
		    }
		});
	}
	
	@FXML
	public void registeringIndividual() {
		registerGridPane.setOnMouseClicked(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(RegistrationConstants.MOSIP_URL));
		        } catch (IOException ioException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		        } catch (URISyntaxException uriSyntaxException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
		        			uriSyntaxException.getMessage() + ExceptionUtils.getStackTrace(uriSyntaxException));
		        }
		    }
		});
	}
	
	@FXML
	public void synchronizingData() {
		syncDataGridPane.setOnMouseClicked(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(RegistrationConstants.MOSIP_URL));
		        } catch (IOException ioException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		        } catch (URISyntaxException uriSyntaxException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
		        			uriSyntaxException.getMessage() + ExceptionUtils.getStackTrace(uriSyntaxException));
		        }
		    }
		});
	}
	
	@FXML
	public void mappingDevices() {
		mapDevicesGridPane.setOnMouseClicked(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(RegistrationConstants.MOSIP_URL));
		        } catch (IOException ioException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		        } catch (URISyntaxException uriSyntaxException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
		        			uriSyntaxException.getMessage() + ExceptionUtils.getStackTrace(uriSyntaxException));
		        }
		    }
		});
	}
	
	@FXML
	public void uploadingData() {
		uploadDataGridPane.setOnMouseClicked(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(RegistrationConstants.MOSIP_URL));
		        } catch (IOException ioException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		        } catch (URISyntaxException uriSyntaxException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
		        			uriSyntaxException.getMessage() + ExceptionUtils.getStackTrace(uriSyntaxException));
		        }
		    }
		});
	}
	
	@FXML
	public void updatingBiometrics() {
		updateBiometricsGridPane.setOnMouseClicked(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(RegistrationConstants.MOSIP_URL));
		        } catch (IOException ioException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		        } catch (URISyntaxException uriSyntaxException) {
		        	LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
		        			uriSyntaxException.getMessage() + ExceptionUtils.getStackTrace(uriSyntaxException));
		        }
		    }
		});
	}
	
	private void setImagesOnHover() {
		getOnboardedPane.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				getOnboardedImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.GET_ONBOARDED_FOCUSED)));
			} else {
				getOnboardedImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.GET_ONBOARDED_IMG_PATH)));
			}
		});
		onboardGridPane.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				onboardImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.ONBOARDING_FOCUSED)));
			} else {
				onboardImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.ONBOARDING_IMG_PATH)));
			}
		});
		registerGridPane.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				registerImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.REGISTERING_FOCUSED)));
			} else {
				registerImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.REGISTERING_IMG_PATH)));
			}
		});
		syncDataGridPane.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				syncDataImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.SYNC_DATA_FOCUSED)));
			} else {
				syncDataImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.SYNC_DATA_IMAGE)));
			}
		});
		mapDevicesGridPane.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				mapDevicesImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.SYNC_DATA_FOCUSED)));
			} else {
				mapDevicesImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.SYNC_DATA_IMAGE)));
			}
		});
		uploadDataGridPane.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				uploadDataImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.UPDATE_OP_BIOMETRICS_FOCUSED)));
			} else {
				uploadDataImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.UPDATE_OP_BIOMETRICS_IMAGE)));
			}
		});
		updateBiometricsGridPane.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				updateBiometricsImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.UPDATE_BIOMETRICS_FOCUSED)));
			} else {
				updateBiometricsImageView.setImage(new Image(getClass().getResourceAsStream(RegistrationConstants.UPDATE_BIOMETRICS_IMG_PATH)));
			}
		});
	}
}