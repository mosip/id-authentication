package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.service.MasterSyncService;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Class for validation of the Registration Field
 * 
 * @author Taleev.Aalam
 * @since 1.0.0
 *
 */

@Component
public class Validations extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(Validations.class);
	private boolean isChild;
	private ResourceBundle validationBundle;
	private ResourceBundle messageBundle;
	private ResourceBundle labelBundle;
	private String isConsolidated;
	private StringBuilder validationMessage;
	private List<String> blackListedWords;
	private List<String> noAlert;

	public Validations() {
		try {
			noAlert = new ArrayList<String>();
			noAlert.add("ageField");
			noAlert.add("dd");
			noAlert.add("mm");
			noAlert.add("yyyy");
			noAlert.add("ddLocalLanguage");
			noAlert.add("mmLocalLanguage");
			noAlert.add("yyyyLocalLanguage");
			noAlert.add("mobileNo");
			noAlert.add("postalCode");
			noAlert.add("postalCode");
			noAlert.add("cniOrPinNumber");
			noAlert.add("uinId");
			validationMessage = new StringBuilder();
			validationBundle = ApplicationContext.applicationLanguageValidationBundle();
			messageBundle = ApplicationContext.applicationMessagesBundle();
			labelBundle = ApplicationContext.applicationLanguageBundle();
		} catch (RuntimeException exception) {
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
		}
	}

	
	@PostConstruct
	public void setResourceBundle() {
		getGlobalParams();
		ApplicationContext.loadResources();
		validationBundle = ApplicationContext.applicationLanguageValidationBundle();
		messageBundle = ApplicationContext.applicationMessagesBundle();
		labelBundle = ApplicationContext.applicationLanguageBundle();
	}
	
	/**
	 * Iterate the fields to and call the validate method on them
	 */
	public boolean validateTheFields(AnchorPane pane, List<String> notTovalidate, boolean isValid,
			String isConsolidated) {
		for (Node node : pane.getChildren()) {
			if (node instanceof AnchorPane) {
				isValid = validateTheFields((AnchorPane) node, notTovalidate, isValid, isConsolidated);
			} else {
				if (nodeToValidate(notTovalidate, node)) {
					isValid = validateTheNode(node, node.getId());
					if (isConsolidated.equals(RegistrationConstants.ENABLE)) {
						isValid = getValidationMessage().toString().length() == 0;
					}
				}
			}
			if (!isValid && isConsolidated.equals(RegistrationConstants.DISABLE))
				break;
		}
		return isValid;
	}

	/**
	 * To decide whether this node should be validated or not
	 */
	private boolean nodeToValidate(List<String> notTovalidate, Node node) {
		return node.getId() != null && !(notTovalidate.contains(node.getId())) && !(node instanceof ImageView)
				&& !(node instanceof Button) && !(node instanceof Label);
	}

	public boolean validate(AnchorPane pane, List<String> notTovalidate, boolean isValid,
			MasterSyncService masterSync) {
		this.blackListedWords = masterSync.getAllBlackListedWords(ApplicationContext.applicationLanguage()).stream()
				.map(b -> b.getWord()).collect(Collectors.toList());
		isConsolidated = AppConfig.getApplicationProperty(RegistrationConstants.IS_CONSOLIDATED);
		return validateTheFields(pane, notTovalidate, isValid, isConsolidated);
	}

	/**
	 * Pass the node to check for the validation, specific validation method
	 * will be called for each field
	 */
	public boolean validateTheNode(Node node, String id) {

		if (node instanceof ComboBox<?>)
			return validateComboBox((ComboBox<?>) node, id, isConsolidated);
		if (node instanceof DatePicker)
			return validateDob((DatePicker) node, id, isConsolidated);
		if (node instanceof VBox)
			return validateDocument((VBox) node, id, isConsolidated);
		return validateTextField((TextField) node, id, isConsolidated);

	}

	/**
	 * Validate for the document upload
	 */
	private boolean validateDocument(VBox node, String id, String isConsolidated) {
		try {
			if (id.matches(RegistrationConstants.POR_BOX) && !isChild)
				return true;
			if (node.isDisabled())
				return true;
			if (!(node.getChildren().isEmpty()))
				return true;
			generateAlert(messageBundle.getString(id), isConsolidated, validationMessage);
			node.requestFocus();
		} catch (RuntimeException exception) {
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return false;
	}

	/**
	 * Validate for the TextField
	 */
	public boolean validateTextField(TextField node, String id, String isConsolidated) {
		try {
			String validationProperty[] = validationBundle.getString(id)
					.split(RegistrationConstants.VALIDATION_SPLITTER);
			String label = id.replaceAll(RegistrationConstants.ON_TYPE, RegistrationConstants.EMPTY);
			label = label.replaceAll(RegistrationConstants.LOCAL_LANGUAGE, RegistrationConstants.EMPTY);
			String regex = validationProperty[0];
			int length = Integer.parseInt(validationProperty[1]);
			String isMandetory = validationProperty[2];
			String isFixed = validationProperty[3];
			boolean showAlert = (noAlert.contains(node.getId()) && id.contains(RegistrationConstants.ON_TYPE));
			if (node.isDisabled())
				return true;
			if (isMandetory.equals("false") && node.getText().isEmpty())
				return true;
			if (!id.contains(RegistrationConstants.ON_TYPE) && isMandetory.equals("true") && node.getText().isEmpty()) {
				if(!showAlert)
					generateAlert(labelBundle.getString(label).concat(" ").concat(messageBundle.getString(RegistrationConstants.REG_LGN_001)), isConsolidated, validationMessage);
				node.requestFocus();
				return false;
			}
			if (node.getText().matches(regex)) {
				
				if(blackListedWords!=null) {
				if ( (!id.contains(RegistrationConstants.ON_TYPE)) && blackListedWords.contains(node.getText())) {
					if(!showAlert)
						generateAlert(
								"For "+labelBundle.getString(label)+" "+node.getText().concat(" is ").concat(RegistrationConstants.BLOCKED).concat(" word"),
								isConsolidated, validationMessage);
					node.requestFocus();
					return false;
				}
				}
				
				if (isFixed.equals("false")) {
					if (node.getText().length() <= length) {
						return true;
					} else {
						if(!showAlert)
							generateAlert(
									labelBundle.getString(label).concat(" ").concat(messageBundle.getString(RegistrationConstants.REG_DDC_002_1)).concat(" "+length+" ").concat(messageBundle.getString(RegistrationConstants.REG_DDC_002_2)),
									isConsolidated, validationMessage);
						node.requestFocus();
						return false;
					}

				} else {
					if (node.getText().length() == length) {
						return true;
					} else {
						if(!showAlert)
							generateAlert(
									labelBundle.getString(label).concat(" ").concat(messageBundle.getString(RegistrationConstants.REG_DDC_003_1)).concat(" "+length+" ").concat(messageBundle.getString(RegistrationConstants.REG_DDC_003_2)),
									isConsolidated, validationMessage);
						node.requestFocus();
						return false;
					}
				}

			}
			if(!showAlert)
				generateAlert(
						messageBundle.getString(label+"_"+RegistrationConstants.REG_DDC_004_1).concat(" "+labelBundle.getString(label)),
						isConsolidated, validationMessage);
			node.requestFocus();
			return false;
		} catch (RuntimeException exception) {
			exception.printStackTrace();
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
	}

	/**
	 * Validate for the AgeDatePicker type of node
	 */
	private boolean validateDob(DatePicker node, String id, String isConsolidated) {
		try {
			if (node.isDisabled())
				return true;
			if (node.getValue() == null) {
				generateAlert(labelBundle.getString(id).concat(" ").concat(messageBundle.getString(RegistrationConstants.REG_LGN_001)), isConsolidated, validationMessage);
				node.requestFocus();
				return false;
			}
		} catch (RuntimeException exception) {
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Validate for the ComboBox type of node
	 */
	private boolean validateComboBox(ComboBox<?> node, String id, String isConsolidated) {
		try {
			if (id.matches(RegistrationConstants.POR_DOCUMENTS) && !isChild)
				return true;
			if (node.isDisabled())
				return true;
			if (node.getValue() == null) {
				generateAlert(labelBundle.getString(id).concat(" ").concat(messageBundle.getString(RegistrationConstants.REG_LGN_001)), isConsolidated, validationMessage);
				node.requestFocus();
				return false;
			}
		} catch (RuntimeException exception) {
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return true;
	}

	public boolean validateUinOrRid(TextField field, boolean isChild, IdValidator<String> uinValidator,
			RidValidator<String> ridValidator) {
		if (!isChild)
			return true;
		if (field.getText().length() <= Integer.parseInt(AppConfig.getApplicationProperty("uin_length"))) {
			try {
				uinValidator.validateId(field.getText());
			} catch (InvalidIDException invalidUinException) {
				generateAlert(RegistrationConstants.ERROR, invalidUinException.getErrorText());
				field.requestFocus();
				return false;
			}
		} else {
			try {
				ridValidator.validateId(field.getText());
			} catch (InvalidIDException invalidRidException) {
				generateAlert(RegistrationConstants.ERROR, invalidRidException.getErrorText());
				field.requestFocus();
				return false;
			}
		}

		return true;
	}

	/**
	 * Check for child
	 */
	public boolean isChild() {
		return isChild;
	}

	/**
	 * Set for child
	 */
	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}

	public StringBuilder getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage() {
		validationMessage.delete(0, validationMessage.length());
	}

}
