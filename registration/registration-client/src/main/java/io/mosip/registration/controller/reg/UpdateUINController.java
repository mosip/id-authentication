package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_UIN_UPDATE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import io.mosip.registration.dto.SelectionListDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * UpdateUINController Class.
 * 
 * @author Mahesh Kumar
 *
 */
@Controller
public class UpdateUINController extends BaseController implements Initializable {

	private static final List<String> UIN_UPDATE_CONFIGURED_DEMOGRAPHIC_FIELDS_LIST = Arrays.asList(
			RegistrationConstants.UIN_UPDATE_NAME, RegistrationConstants.UIN_UPDATE_AGE,
			RegistrationConstants.UIN_UPDATE_FOREIGNER, RegistrationConstants.UIN_UPDATE_GENDER,
			RegistrationConstants.UIN_UPDATE_ADDRESS, RegistrationConstants.UIN_UPDATE_PHONE,
			RegistrationConstants.UIN_UPDATE_EMAIL);

	private static final List<String> UIN_UPDATE_CONFIGURED_BIO_FIELDS_LIST = Arrays.asList(
			RegistrationConstants.UIN_UPDATE_CNIE_NUMBER, RegistrationConstants.UIN_UPDATE_PARENT_DETAILS,
			RegistrationConstants.UIN_UPDATE_BIOMETRICS);

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
	private CheckBox phone;
	@FXML
	private CheckBox email;
	@FXML
	private CheckBox biometrics;
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
	@FXML
	private GridPane uinUpdateRoot;

	@Autowired
	private UinValidator<String> uinValidatorImpl;

	@Autowired
	Validations validation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			FXUtils fxUtils = FXUtils.getInstance();
			listenerOnFields(fxUtils);
			listenerOnFieldsParentOrGuardian(fxUtils);
			fxUtils.validateOnType(uinUpdateRoot, uinId, validation);
			updateUINFieldsConfiguration();

			if (getRegistrationDTOFromSession()!= null && getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				SelectionListDTO selectionListDTO = getRegistrationDTOFromSession().getSelectionListDTO();

				uinId.setText(selectionListDTO.getUinId());
				name.setSelected(selectionListDTO.isName());
				age.setSelected(selectionListDTO.isAge());
				gender.setSelected(selectionListDTO.isGender());
				address.setSelected(selectionListDTO.isAddress());
				phone.setSelected(selectionListDTO.isPhone());
				email.setSelected(selectionListDTO.isEmail());
				biometrics.setSelected(selectionListDTO.isBiometrics());
				cnieNumber.setSelected(selectionListDTO.isCnieNumber());
				parentOrGuardianDetails.setSelected(selectionListDTO.isParentOrGuardianDetails());
				foreigner.setSelected(selectionListDTO.isForeigner());
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * Update UIN fields configuration.
	 */
	private void updateUINFieldsConfiguration() {

		List<String> configuredFieldsfromDB = Arrays.asList(
				getValueFromApplicationContext(RegistrationConstants.UIN_UPDATE_CONFIG_FIELDS_FROM_DB).split(","));

		List<String> configvalues = new ArrayList<>();
		configvalues.addAll(configuredFieldsfromDB);

		for (String configureField : UIN_UPDATE_CONFIGURED_BIO_FIELDS_LIST) {
			if (!configvalues.contains(configureField)) {
				biometricBox.getChildren().forEach(demographicNode -> {
					if (demographicNode.getId().equalsIgnoreCase(configureField)) {
						demographicNode.setVisible(false);
						demographicNode.setManaged(false);
					}
				});
			} else {
				biometricBox.getChildren().forEach(demographicNode -> {
					if (demographicNode.getId().equalsIgnoreCase(configureField) && configvalues.size() == 1) {
						demographicNode.setDisable(true);
						((CheckBox) demographicNode).setSelected(true);
					}
				});
			}
		}

		for (String configureField : UIN_UPDATE_CONFIGURED_DEMOGRAPHIC_FIELDS_LIST) {
			if (!configvalues.contains(configureField)) {
				demographicHBox.getChildren().forEach(demographicNode -> {
					if (demographicNode.getId().equalsIgnoreCase(configureField)) {
						demographicNode.setVisible(false);
						demographicNode.setManaged(false);
					}
				});
			} else {
				demographicHBox.getChildren().forEach(demographicNode -> {
					if (demographicNode.getId().equalsIgnoreCase(configureField) && configvalues.size() == 1) {
						demographicNode.setDisable(true);
						((CheckBox) demographicNode).setSelected(true);
					}
				});

			}

		}
	}

	private void listenerOnFields(FXUtils fxUtils) {
		fxUtils.listenOnSelectedCheckBox(name);
		fxUtils.listenOnSelectedCheckBox(age);
		fxUtils.listenOnSelectedCheckBox(gender);
		fxUtils.listenOnSelectedCheckBox(address);
		fxUtils.listenOnSelectedCheckBox(phone);
		fxUtils.listenOnSelectedCheckBox(email);
		fxUtils.listenOnSelectedCheckBox(cnieNumber);
		fxUtils.listenOnSelectedCheckBox(foreigner);
	}

	private void listenerOnFieldsParentOrGuardian(FXUtils fxUtils) {
		fxUtils.listenOnSelectedCheckBoxParentOrGuardian(parentOrGuardianDetails, biometrics);
		fxUtils.listenOnSelectedCheckBoxParentOrGuardian(parentOrGuardianDetails, biometrics);

	}

	/**
	 * Submitting for UIN update after selecting the required fields.
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	public void submitUINUpdate(ActionEvent event) {
		LOGGER.info(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Updating UIN details");
		try {

			if (StringUtils.isEmpty(uinId.getText())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UPDATE_UIN_ENTER_UIN_ALERT);
			} else {

				if (uinValidatorImpl.validateId(uinId.getText())) {

					if (name.isSelected() || age.isSelected() || gender.isSelected() || address.isSelected()
							|| phone.isSelected() || email.isSelected() || biometrics.isSelected()
							|| cnieNumber.isSelected() || parentOrGuardianDetails.isSelected()
							|| foreigner.isSelected()) {

						SelectionListDTO selectionListDTO = new SelectionListDTO();

						selectionListDTO.setName(name.isSelected());
						selectionListDTO.setAge(age.isSelected());
						selectionListDTO.setGender(gender.isSelected());
						selectionListDTO.setAddress(address.isSelected());
						selectionListDTO.setPhone(phone.isSelected());
						selectionListDTO.setEmail(email.isSelected());
						selectionListDTO.setBiometrics(biometrics.isSelected());
						selectionListDTO.setCnieNumber(cnieNumber.isSelected());
						selectionListDTO.setParentOrGuardianDetails(parentOrGuardianDetails.isSelected());
						selectionListDTO.setForeigner(foreigner.isSelected());

						selectionListDTO.setUinId(uinId.getText());

						registrationController.init(selectionListDTO);

						Parent createRoot = BaseController.load(
								getClass().getResource(RegistrationConstants.CREATE_PACKET_PAGE),
								applicationContext.getApplicationLanguageBundle());

						getScene(createRoot).setRoot(createRoot);
						if (!biometrics.isSelected()) {
							getRegistrationDTOFromSession().setUpdateUINonBiometric(true);
						}
						if (parentOrGuardianDetails.isSelected()) {
							SessionContext.map().put(RegistrationConstants.UIN_UPDATE_PARENTORGUARDIAN,
									RegistrationConstants.ENABLE);
						} else {
							SessionContext.map().put(RegistrationConstants.UIN_UPDATE_PARENTORGUARDIAN,
									RegistrationConstants.DISABLE);
						}
					} else {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UPDATE_UIN_SELECTION_ALERT);
					}
				}
			}
		} catch (InvalidIDException invalidIdException) {
			LOGGER.error(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					invalidIdException.getMessage() + ExceptionUtils.getStackTrace(invalidIdException));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UPDATE_UIN_VALIDATION_ALERT);
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_UIN_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}
}
