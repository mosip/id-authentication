package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.controller.BaseController;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
	private static final Logger LOGGER = AppConfig.getLogger(ApplicationContext.class);
	private boolean isChild;
	private ResourceBundle validationBundle;
	private ResourceBundle messageBundle;
	private ResourceBundle labelBundle;
	private String isConsolidated;

	public Validations() {
		try {
			validationBundle = ApplicationContext.getInstance().getApplicationLanguagevalidationBundle();
			messageBundle = ApplicationContext.getInstance().getApplicationMessagesBundle();
			labelBundle = ApplicationContext.getInstance().getApplicationLanguageBundle();
		} catch (NullPointerException | MissingResourceException exception) {
			LOGGER.error("VALIDATIONS", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
		}
	}

	/**
	 * Iterate the fields to and call the validate method on them
	 */
	public boolean validateTheFields(AnchorPane pane, List<String> notTovalidate, boolean isValid, String isConsolidated) {
		for (Node node : pane.getChildren()) {
			if (node instanceof AnchorPane) {
				isValid = validateTheFields((AnchorPane) node, notTovalidate, isValid, isConsolidated);
			} else {
				if (node.getId() != null && !(notTovalidate.contains(node.getId())) && !(node instanceof ImageView)
						&& !(node instanceof Button) && !(node instanceof Label)) {
					isValid = validateTheNode(node, node.getId());
					if(isConsolidated.equals("Y")) {
						isValid = getValidationMessage().toString().length()==0;
					}
				}
			}
			if (!isValid && isConsolidated.equals("N"))
				break;
		}
		return isValid;
	}

	public boolean validate(AnchorPane pane, List<String> notTovalidate, boolean isValid) {
		isConsolidated = AppConfig.getApplicationProperty(RegistrationConstants.IS_CONSOLIDATED);
		return validateTheFields(pane, notTovalidate, isValid, isConsolidated);
	}

	/**
	 * Pass the node to check for the validation, specific validation method
	 * will be called for each field
	 */
	public boolean validateTheNode(Node node, String id) {

		if (node instanceof ScrollPane && node.getId().matches("porScroll"))
			return validateDocument((ScrollPane) node, isChild, id ,isConsolidated);
		if (node instanceof ComboBox<?> && node.getId().matches("porDocuments"))
			return validateComboBox((ComboBox<?>) node, isChild, id ,isConsolidated);
		if (node.getId().matches("parentName|uinId"))
			return validateTextField((TextField) node, isChild, id, isConsolidated);
		if (node instanceof ComboBox<?>)
			return validateComboBox((ComboBox<?>) node, id, isConsolidated);
		if (node instanceof DatePicker)
			return validateDob((DatePicker) node, id, isConsolidated);
		if (node instanceof ScrollPane)
			return validateDocument((ScrollPane) node, id, isConsolidated);
		return validateTextField((TextField) node, id, isConsolidated);

	}

	/**
	 * Validate for the document upload
	 */
	private boolean validateDocument(ScrollPane node, String id, String isConsolidated) {
		try {
			if (node.isDisabled())
				return true;
			if (!((VBox) node.getContent()).getChildren().isEmpty())
				return true;
			generateValidationAlert(messageBundle.getString(id), isConsolidated);
			node.requestFocus();
		} catch (MissingResourceException exception) {
			LOGGER.error("VALIDATIONS", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return false;
	}

	/**
	 * Validate for the document upload based on the isChild
	 */
	private boolean validateDocument(ScrollPane node, boolean isDependent, String id, String isConsolidated) {
		try {
			if (node.isDisabled())
				return true;
			if (isDependent) {
				if (!((VBox) node.getContent()).getChildren().isEmpty())
					return true;
				generateValidationAlert(messageBundle.getString(id), isConsolidated);
				node.requestFocus();
				return false;
			}
		} catch (MissingResourceException exception) {
			LOGGER.error("VALIDATIONS", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Validate for the TextField
	 */
	public boolean validateTextField(TextField node, String id, String isConsolidated) {
		try {
			String validationProperty[] = validationBundle.getString(id).split("\\s,");
			String label = id.replaceAll("_ontype", "");
			String message = id.replaceAll("_ontype", "");
			String regex = validationProperty[0];
			int length = Integer.parseInt(validationProperty[1]);
			String isMandetory = validationProperty[2];
			String isFixed = validationProperty[3];
			if (isMandetory.equals("false") && node.getText().isEmpty())
				return true;
			if (node.isDisabled())
				return true;
			if (!id.contains("ontype") && isMandetory.equals("true") && node.getText().isEmpty()) {
				generateValidationAlert(labelBundle.getString(label) + " is required", isConsolidated);
				node.requestFocus();
				return false;
			}
			if (node.getText().matches(regex)) {
				if (isFixed.equals("false")) {
					if (node.getText().length() <= length) {
						return true;
					} else {
						generateValidationAlert(
								labelBundle.getString(label) + " should be a maximum of " + length + " characters long",
								isConsolidated);
						node.requestFocus();
						return false;
					}

				} else {
					if (node.getText().length() == length) {
						return true;
					} else {
						generateValidationAlert(
								labelBundle.getString(label) + " should be exactly " + length + " characters long",
								isConsolidated);
						node.requestFocus();
						return false;
					}
				}

			}
			generateValidationAlert(labelBundle.getString(label) + " should contain " + messageBundle.getString(message)
					+ " characters only", isConsolidated);
			node.requestFocus();
			return false;
		} catch (MissingResourceException exception) {
			LOGGER.error("VALIDATIONS", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
	}

	/**
	 * Validate for the TextField based on the isChild
	 */
	private boolean validateTextField(TextField node, boolean dependency, String id, String isConsolidated) {
		try {
			if (dependency) {
				return validateTextField(node, id, isConsolidated);
			}
		} catch (MissingResourceException exception) {
			LOGGER.error("VALIDATIONS", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Validate for the AgeDatePicker type of node
	 */
	private boolean validateDob(DatePicker node, String id, String isConsolidated) {
		try {
			if (node.isDisabled())
				return true;
			if (node.getValue() == null) {
				generateValidationAlert(labelBundle.getString(id) + " " + "is required", isConsolidated);
				node.requestFocus();
				return false;
			}
		} catch (MissingResourceException exception) {
			LOGGER.error("VALIDATIONS", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Validate for the ComboBox type of node
	 */
	private boolean validateComboBox(ComboBox<?> node, String id, String isConsolidated) {
		try {
			if (node.isDisabled())
				return true;
			if (node.getValue() == null) {
				generateValidationAlert(labelBundle.getString(id) + " " + "is required", isConsolidated);
				node.requestFocus();
				return false;
			}
		} catch (MissingResourceException exception) {
			LOGGER.error("VALIDATIONS", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Validate for the ComboBox type of node based on the isChild
	 */
	private boolean validateComboBox(ComboBox<?> node, boolean isDependent, String id, String isConsolidated) {
		try {
			if (node.isDisabled())
				return true;
			if (isDependent && node.getValue() == null) {
				generateValidationAlert(labelBundle.getString(id) + " " + "is required", isConsolidated);
				node.requestFocus();
				return false;
			}
		} catch (MissingResourceException exception) {
			LOGGER.error("VALIDATIONS", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Check for childd
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

}
