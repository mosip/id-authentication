package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.service.MasterSyncService;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID, runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	
	public void setResourceBundle() {
		validationBundle = ApplicationContext.applicationLanguageValidationBundle();
		messageBundle = ApplicationContext.applicationMessagesBundle();
		labelBundle = ApplicationContext.applicationLanguageBundle();
	}
	
	/**
	 * Iterate the fields to and call the validate method on them
	 */
	private boolean tempValid=true;
	public boolean validateTheFields(Pane pane, List<String> notTovalidate, boolean isValid,
			String isConsolidated) {
		for (Node node : pane.getChildren()) {
			System.out.println(node.getId());
			if (node instanceof Pane) {
				tempValid = validateTheFields((Pane) node, notTovalidate, isValid, isConsolidated);
				if(tempValid) {
					if(isValid)
						isValid=true;
					else
						isValid=false;
				}else {
					isValid=false;
				}
			} else {
				if (nodeToValidate(notTovalidate, node)) {
					tempValid = validateTheNode(pane, node, node.getId());
					if(tempValid) {
						if(isValid)
							isValid=true;
						else
							isValid=false;
					}else {
						isValid=false;
					}
					if (isConsolidated.equals(RegistrationConstants.ENABLE)) {
						isValid = getValidationMessage().toString().length() == 0;
					}
				}
			}
		}
		return isValid;
	}

	/**
	 * To decide whether this node should be validated or not
	 */
	private boolean nodeToValidate(List<String> notTovalidate, Node node) {
		return node.getId() != null && !(notTovalidate.contains(node.getId())) && !(node instanceof ImageView)
				&& !(node instanceof Button) && !(node instanceof Label ) && !(node instanceof Hyperlink );
	}

	public boolean validate(Pane pane, List<String> notTovalidate, boolean isValid,
			MasterSyncService masterSync) {
		this.blackListedWords = masterSync.getAllBlackListedWords(ApplicationContext.applicationLanguage()).stream()
				.map(b -> b.getWord()).collect(Collectors.toList());
		isConsolidated = "N";
		return validateTheFields(pane, notTovalidate, isValid, isConsolidated);
	}

	/**
	 * Pass the node to check for the validation, specific validation method
	 * will be called for each field
	 */
	public boolean validateTheNode(Pane parentPane, Node node, String id) {

		if (node instanceof ComboBox<?>)
			return validateComboBox(parentPane, (ComboBox<?>) node, id, isConsolidated);
		if (node instanceof VBox)
			return validateDocument((VBox) node, id, isConsolidated);
	
		return validateTextField(parentPane,(TextField) node, id, isConsolidated);
		
	}

	/**
	 * Validate for the document upload
	 */
	private boolean validateDocument(VBox node, String id, String isConsolidated) {
		boolean validated = false;
		try {

			outer: for (Node documentNode : node.getChildren()) {
				if (documentNode instanceof HBox) {
					for (Node innerNode : ((HBox) documentNode).getChildren()) {
						if (innerNode instanceof VBox) {
							validated = false;
							if (innerNode.isDisabled()) {
								validated = true;
							} else if (!(((VBox) innerNode).getChildren().isEmpty())) {
								validated = true;
							} else {
					/*			generateAlert(messageBundle.getString("docMandateMsg").replace("{}",
										((VBox) innerNode).getId()), isConsolidated, validationMessage);*/
								node.requestFocus();
								break outer;
							}
						}
					}
				}
			}

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			return false;
		}
		return node.getChildren().size() > 0 ? validated : true;
	}

	/**
	 * Validate for the TextField
	 */
	public boolean validateTextField(Pane parentPane, TextField node, String id, String isConsolidated) {
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
					generateAlert(parentPane, id, labelBundle.getString(label).concat(" ").concat(messageBundle.getString(RegistrationConstants.REG_LGN_001)), isConsolidated, validationMessage);
				return false;
			}
			if (node.getText().matches(regex)) {
				
				if(blackListedWords!=null) {
				if ( (!id.contains(RegistrationConstants.ON_TYPE)) && blackListedWords.contains(node.getText())) {
					if(!showAlert)
						generateAlert(parentPane, id,
								"For "+labelBundle.getString(label)+" "+node.getText().concat(" is ").concat(RegistrationConstants.BLOCKED).concat(" word"),
								isConsolidated, validationMessage);
					return false;
				}
				}
				
				if (isFixed.equals("false")) {
					if (node.getText().length() <= length) {
						return true;
					} else {
						if(!showAlert)
							generateAlert(parentPane, id,
									labelBundle.getString(label).concat(" ").concat(messageBundle.getString(RegistrationConstants.REG_DDC_002_1)).concat(" "+length+" ").concat(messageBundle.getString(RegistrationConstants.REG_DDC_002_2)),
									isConsolidated, validationMessage);
						return false;
					}

				} else {
					if (node.getText().length() == length) {
						return true;
					} else {
						if(!showAlert)
							generateAlert(parentPane, id,
									labelBundle.getString(label).concat(" ").concat(messageBundle.getString(RegistrationConstants.REG_DDC_003_1)).concat(" "+length+" ").concat(messageBundle.getString(RegistrationConstants.REG_DDC_003_2)),
									isConsolidated, validationMessage);
						return false;
					}
				}

			}
			if(!showAlert)
				generateAlert(parentPane, id,
						messageBundle.getString(label+"_"+RegistrationConstants.REG_DDC_004_1).concat(" "+labelBundle.getString(label)),
						isConsolidated, validationMessage);
			return false;
		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID, runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			return false;
		}
	}

	/**
	 * Validate for the ComboBox type of node
	 */
	private boolean validateComboBox(Pane parentPane, ComboBox<?> node, String id, String isConsolidated) {
		try {
			if (id.matches(RegistrationConstants.POR_DOCUMENTS) && !isChild)
				return true;
			if (node.isDisabled())
				return true;
			if (node.getValue() == null) {
				generateAlert(parentPane, id,
						labelBundle.getString(id).concat(" ")
								.concat(messageBundle.getString(RegistrationConstants.REG_LGN_001)),
						isConsolidated, validationMessage);
				return false;
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.VALIDATION_LOGGER, APPLICATION_NAME, APPLICATION_ID, runtimeException.getMessage()+ ExceptionUtils.getStackTrace(runtimeException));
			return false;
		}
		return true;
	}

	public boolean validateUinOrRid(TextField field, boolean isChild, UinValidator<String> uinValidator,
			RidValidator<String> ridValidator) {
		if (!isChild)
			return true;
		if (field.getText().length() <= Integer.parseInt((String)ApplicationContext.map().get(RegistrationConstants.UIN_LENGTH))) {
			try {
				uinValidator.validateId(field.getText());
			} catch (InvalidIDException invalidUinException) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UIN_INVALID);
				LOGGER.error("UIN VALIDATOIN FAILED", APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, invalidUinException.getMessage()+ ExceptionUtils.getStackTrace(invalidUinException));
				return false;
			}
		} else {
			try {
				ridValidator.validateId(field.getText());
			} catch (InvalidIDException invalidRidException) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.RID_INVALID);
				LOGGER.error("RID VALIDATOIN FAILED", APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, invalidRidException.getMessage() + ExceptionUtils.getStackTrace(invalidRidException));
				return false;
			}
		}

		return true;
	}
	
	
	/**
	 * Validate for the single string
	 */
	public boolean validateSingleString(String value, String id) {
		String validationProperty[] = validationBundle.getString(id).split(RegistrationConstants.VALIDATION_SPLITTER);

		String regex = validationProperty[0];

		if (value.matches(regex)) {
			return true;
		}
		return false;
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
