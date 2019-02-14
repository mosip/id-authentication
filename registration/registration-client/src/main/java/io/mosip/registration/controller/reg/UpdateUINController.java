package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_UIN_UPDATE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import io.mosip.registration.dto.SelectionListDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

@Controller
public class UpdateUINController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(UpdateUINController.class);

	@Autowired
	private RegistrationController registrationController;

	@FXML
	private TextField uinId;
	@FXML
	private CheckBox name;
	@FXML
	private CheckBox age;
	@FXML
	private CheckBox gender;
	@FXML
	private CheckBox address;
	@FXML
	private CheckBox contactDetails;
	@FXML
	private CheckBox biometricException;
	@FXML
	private CheckBox biometricIris;
	@FXML
	private CheckBox biometricFingerprint;
	@FXML
	private CheckBox cnieNumber;
	@FXML
	private CheckBox parentOrGuardianDetails;
	@FXML
	private CheckBox foreigner;
	@FXML
	private Label toggleLabel1;
	@FXML
	private Label toggleLabel2;
	@FXML
	private HBox biometricBox;
	@FXML
	private HBox demographicHBox;

	private SimpleBooleanProperty switchedOn;
	private boolean isChild;

	@Autowired
	private UinValidator<String> uinValidatorImpl;

	@Autowired
	Validations validation;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		switchedOn = new SimpleBooleanProperty(false);
		isChild = switchedOn.get();
		toggleFunction();
		FXUtils fxUtils = FXUtils.getInstance();
		SessionContext.map().put(RegistrationConstants.IS_CONSOLIDATED, RegistrationConstants.DISABLE);
		fxUtils.validateOnType(uinId, validation);
		if (applicationContext.getApplicationMap().get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)
				.equals(RegistrationConstants.ENABLE)) {

			biometricBox.getChildren().forEach(bio -> {
				if (bio.getId().equals("biometricFingerprint")) {
					bio.setVisible(false);
					bio.setManaged(false);
				}
			});
		}
		configuringUpdateUINDemographicFields();
		configuringUpdateUINBiometricFields();
	}

	/**
	 * Configuring update UIN fields.
	 */
	private void configuringUpdateUINDemographicFields() {
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_NAME_ENABLE_FLAG)
				.equals(RegistrationConstants.ENABLE)) {
			demographicHBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("name")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_AGE_ENABLE_FLAG)
				.equals(RegistrationConstants.ENABLE)) {
			demographicHBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("age")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_GENDER_ENABLE_FLAG)
				.equals(RegistrationConstants.ENABLE)) {
			demographicHBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("gender")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_ADDRESS_ENABLE_FLAG)
				.equals(RegistrationConstants.ENABLE)) {
			demographicHBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("address")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_CONTACT_DTLS_ENABLE_FLG)
				.equals(RegistrationConstants.ENABLE)) {
			demographicHBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("contactDetails")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_PARENT_DTLS_ENABLE_FLG)
				.equals(RegistrationConstants.ENABLE)) {
			demographicHBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("parentOrGuardianDetails")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_FOREIGNER_ENABLE_FLG)
				.equals(RegistrationConstants.ENABLE)) {
			demographicHBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("foreigner")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_CNIE_NUMBER_ENABLE_FLAG)
				.equals(RegistrationConstants.ENABLE)) {
			biometricBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("cnieNumber")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
	}

	private void configuringUpdateUINBiometricFields() {
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_BIO_EXCEPTION_ENABLE_FLG)
				.equals(RegistrationConstants.ENABLE)) {
			biometricBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("biometricException")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_BIO_FP_ENABLE_FLG)
				.equals(RegistrationConstants.ENABLE)) {
			biometricBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("biometricFingerprint")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
		if (!ApplicationContext.map().get(RegistrationConstants.UIN_UPDATE_BIO_IRIS_ENABLE_FLG)
				.equals(RegistrationConstants.ENABLE)) {
			biometricBox.getChildren().forEach(demographicNode -> {
				if (demographicNode.getId().equals("biometricIris")) {
					demographicNode.setVisible(false);
					demographicNode.setManaged(false);
				}
			});
		}
	}

	/**
	 * Toggle functionality to give individual is adult or child.
	 */
	private void toggleFunction() {
		try {
			LOGGER.info(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					"Entering into toggle function for toggle label 1 and toggle level 2");

			toggleLabel1.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
			toggleLabel2.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
			switchedOn.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						toggleLabel1.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						toggleLabel2.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						isChild = newValue;

					} else {
						toggleLabel1.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						toggleLabel2.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						isChild = newValue;

					}
				}
			});

			toggleLabel1.setOnMouseClicked(event -> switchedOn.set(!switchedOn.get()));
			toggleLabel2.setOnMouseClicked(event -> switchedOn.set(!switchedOn.get()));
			LOGGER.info(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					"Exiting the toggle function for toggle label 1 and toggle level 2");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID, runtimeException.getMessage());
		}
	}

	@FXML
	public void submitUINUpdate(ActionEvent event) {
		LOGGER.info(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Updating UIN details");
		try {

			if (StringUtils.isEmpty(uinId.getText())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UPDATE_UIN_ENTER_UIN_ALERT);
			} else {

				if (uinValidatorImpl.validateId(uinId.getText())) {

					SelectionListDTO selectionListDTO = new SelectionListDTO();

					selectionListDTO.setName(name.isSelected());
					selectionListDTO.setAge(age.isSelected());
					selectionListDTO.setGender(gender.isSelected());
					selectionListDTO.setAddress(address.isSelected());
					selectionListDTO.setContactDetails(contactDetails.isSelected());
					selectionListDTO.setBiometricException(biometricException.isSelected());
					selectionListDTO.setBiometricIris(biometricIris.isSelected());
					selectionListDTO.setBiometricFingerprint(biometricFingerprint.isSelected());
					selectionListDTO.setCnieNumber(cnieNumber.isSelected());
					selectionListDTO.setParentOrGuardianDetails(parentOrGuardianDetails.isSelected());
					selectionListDTO.setForeigner(foreigner.isSelected());

					selectionListDTO.setChild(isChild);
					selectionListDTO.setUinId(uinId.getText());

					if (name.isSelected() || age.isSelected() || gender.isSelected() || address.isSelected()
							|| contactDetails.isSelected() || biometricException.isSelected()
							|| biometricIris.isSelected() || biometricFingerprint.isSelected()
							|| cnieNumber.isSelected() || parentOrGuardianDetails.isSelected()
							|| foreigner.isSelected()) {
						registrationController.init(selectionListDTO);

						Parent createRoot = BaseController.load(
								getClass().getResource(RegistrationConstants.CREATE_PACKET_PAGE),
								applicationContext.getApplicationLanguageBundle());

						getScene(createRoot).setRoot(createRoot);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UPDATE_UIN_SELECTION_ALERT);
				}
			}
		} catch (InvalidIDException invalidIdException) {
			LOGGER.error(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID, invalidIdException.getMessage());

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UPDATE_UIN_VALIDATION_ALERT);
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}
}
