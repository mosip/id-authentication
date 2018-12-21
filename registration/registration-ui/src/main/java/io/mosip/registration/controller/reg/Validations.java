package io.mosip.registration.controller.reg;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

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
 * @author Taleev.Aalam
 * @since 1.0.0
 *
 */

@Component
public class Validations extends BaseController {

	private boolean isChild;
	private ResourceBundle validationBundle;
	private ResourceBundle messageBundle;
	private ResourceBundle labelBundle;

	public Validations() {
		validationBundle = ResourceBundle.getBundle("validations", new Locale("en"));
		messageBundle = ResourceBundle.getBundle("messages", new Locale("en"));
		labelBundle = ResourceBundle.getBundle("labels", new Locale("en"));

	}


	/**
	 * Iterate the fields to and call the validate method on them
	 */
	public boolean validateTheFields(AnchorPane pane, List<String> notTovalidate, boolean isValid) {
		for (Node node : pane.getChildren()) {
			if (node instanceof AnchorPane) {
				isValid = validateTheFields((AnchorPane) node, notTovalidate, isValid);
			} else {
				if (node.getId() != null && !(notTovalidate.contains(node.getId())) && !(node instanceof ImageView)
						&& !(node instanceof Button) && !(node instanceof Label)) {
					isValid = validate(node, node.getId());
				}
			}
			if (!isValid)
				break;
		}
		return isValid;
	}


	/**
	 * Pass the node to check for the validation, specific validation method will be called for each field
	 */
	public boolean validate(Node node, String id) {
		if (node instanceof ScrollPane && node.getId().matches("porScroll"))
			return validateDocument((ScrollPane) node, isChild, id);
		if (node instanceof ComboBox<?> && node.getId().matches("porDocuments"))
			return validateComboBox((ComboBox<?>) node, isChild, id);
		if (node.getId().matches("parentName|uinId"))
			return validateTextField((TextField) node, isChild, id);
		if (node instanceof ComboBox<?>)
			return validateComboBox((ComboBox<?>) node, id);
		if (node instanceof DatePicker)
			return validateDob((DatePicker) node, id);
		if (node instanceof ScrollPane)
			return validateDocument((ScrollPane) node, id);

		return validateTextField((TextField) node, id);
	}

	/**
	 * Validate for the document upload
	 */
	private boolean validateDocument(ScrollPane node, String id) {
		if (node.isDisabled())
			return true;
		if (!((VBox) node.getContent()).getChildren().isEmpty())
			return true;
		generateAlert("", messageBundle.getString(id));
		node.requestFocus();
		return false;
	}

	/**
	 * Validate for the document upload based on the isChild
	 */
	private boolean validateDocument(ScrollPane node, boolean isDependent, String id) {
		if (node.isDisabled())
			return true;
		if (isDependent) {
			if (!((VBox) node.getContent()).getChildren().isEmpty())
				return true;
			generateAlert("", messageBundle.getString(id));
			node.requestFocus();
			return false;
		}
		return true;
	}

	/**
	 * Validate for the TextField
	 */
	public boolean validateTextField(TextField node, String id) {
		String validationProperty[] = validationBundle.getString(id).split("\\s,");
		String isMandetory = validationProperty[1];
		String label = id.replaceAll("_ontype", "");
		String message = id.replaceAll("ontype", "WARNING");
		if (isMandetory.equals("false") && node.getText().isEmpty())
			return true;
		if (node.isDisabled())
			return true;
		if (node.getText().matches(validationProperty[0]))
			return true;
		generateAlert(labelBundle.getString(label), messageBundle.getString(message));
		node.requestFocus();
		return false;
	}
	
	/**
	 * Validate for the TextField based on the isChild
	 */
	private boolean validateTextField(TextField node, boolean dependency, String id) {
		String validationProperty[] = validationBundle.getString(id).split("\\s,");
		String isMandetory = validationProperty[1];
		String label = id.replaceAll("_ontype", "");
		String message = id.replaceAll("ontype", "WARNING");
		if (isMandetory.equals("false") && node.getText().isEmpty())
			return true;
		if (node.isDisabled())
			return true;
		if (dependency) {
			if (node.getText().matches(validationProperty[0])) {
				return true;
			}
			generateAlert(labelBundle.getString(label), messageBundle.getString(message));
			node.requestFocus();
			return false;
		}
		return true;
	}

	/**
	 * Validate for the AgeDatePicker type of node
	 */
	private boolean validateDob(DatePicker node, String id) {
		if (node.isDisabled())
			return true;
		if (node.getValue() == null) {
			generateAlert(labelBundle.getString(id), messageBundle.getString(id));
			node.requestFocus();
			return false;
		}
		return true;
	}

	/**
	 * Validate for the ComboBox type of node
	 */
	private boolean validateComboBox(ComboBox<?> node, String id) {
		if (node.isDisabled())
			return true;
		if (node.getValue() == null) {
			generateAlert(labelBundle.getString(id), messageBundle.getString(id));
			node.requestFocus();
			return false;
		}
		return true;
	}

	/**
	 * Validate for the ComboBox type of node based on the isChild
	 */
	private boolean validateComboBox(ComboBox<?> node, boolean isDependent, String id) {
		if (node.isDisabled())
			return true;
		if (isDependent) {
			if (node.getValue() == null) {
				generateAlert(labelBundle.getString(id), messageBundle.getString(id));
				node.requestFocus();
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

}
