package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_1;
import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_2;
import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_3;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

@Controller
public class RegistrationPreviewController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationPreviewController.class);

	@FXML
	private WebView webView;

	@FXML
	private Label consent;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Autowired
	private TemplateGenerator templateGenerator;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private RegistrationController registrationController;

	@FXML
	private Text registrationNavlabel;

	@FXML
	private RadioButton noRadio;

	@FXML
	private RadioButton yesRadio;
	
	@FXML
	private Button nextButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		nextButton.setDisable(true);
		yesRadio.selectedProperty().addListener((abs, old, newValue) -> {
			noRadio.setSelected(!newValue.booleanValue());
			nextButton.setDisable(false);
		});

		noRadio.selectedProperty().addListener((abs, old, newValue) -> {
			yesRadio.setSelected(!newValue.booleanValue());
			nextButton.setDisable(false);
		});

		String key = "mosip.registration.consent_" + applicationContext.getApplicationLanguage();

		consent.setText((String) applicationContext.getApplicationMap().get(key));
		if (getRegistrationDTOFromSession() != null && getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			registrationNavlabel.setText(ApplicationContext.applicationLanguageBundle().getString("uinUpdateNavLbl"));
		}
		if (getRegistrationDTOFromSession() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
						.equals(RegistrationConstants.PACKET_TYPE_LOST)) {
			registrationNavlabel.setText(ApplicationContext.applicationLanguageBundle().getString("/lostuin"));
		}
	}

	@FXML
	public void goToPrevPage(ActionEvent event) {
		auditFactory.audit(AuditEvent.REG_PREVIEW_BACK, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			SessionContext.map().put("registrationPreview", false);

			updateUINFlowMethod();

			registrationController.showUINUpdateCurrentPage();
		} else {
			registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
					getPageDetails(RegistrationConstants.REGISTRATION_PREVIEW, RegistrationConstants.PREVIOUS));
		}
	}

	private void updateUINFlowMethod() {

		long fingerPrintCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
				.getBiometricExceptionDTO().stream()
				.filter(bio -> bio.getBiometricType().equalsIgnoreCase("fingerprint")).count();

		long irisCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
				.getBiometricExceptionDTO().stream()
				.filter(bio -> bio.getBiometricType().equalsIgnoreCase(RegistrationConstants.IRIS)).count();

		if (!RegistrationConstants.DISABLE.equalsIgnoreCase(
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.FACE_DISABLE_FLAG)))) {
			SessionContext.getInstance().getMapObject().put("faceCapture", true);
		} else if (irisCount > 0) {
			SessionContext.map().put("irisCapture", true);
		} else if (fingerPrintCount > 0) {
			SessionContext.map().put("fingerPrintCapture", true);
		}
	}

	@FXML
	public void goToNextPage(ActionEvent event) {
		auditFactory.audit(AuditEvent.REG_PREVIEW_SUBMIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
		if (yesRadio.isSelected() || noRadio.isSelected()) {
			if (yesRadio.isSelected()) {
				getRegistrationDTOFromSession().getRegistrationMetaDataDTO()
						.setConsentOfApplicant(RegistrationConstants.CONCENT_OF_APPLICANT_SELECTED);
			} else {
				getRegistrationDTOFromSession().getRegistrationMetaDataDTO()
						.setConsentOfApplicant(RegistrationConstants.CONCENT_OF_APPLICANT_UNSELECTED);
			}

			if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				SessionContext.map().put("registrationPreview", false);
				SessionContext.map().put("operatorAuthenticationPane", true);
				registrationController.showUINUpdateCurrentPage();
			} else {
				registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
						getPageDetails(RegistrationConstants.REGISTRATION_PREVIEW, RegistrationConstants.NEXT));
			}
			registrationController.goToAuthenticationPage();
		} else {
			nextButton.setDisable(true);
		}
	}

	public void setUpPreviewContent() {
		LOGGER.info("REGISTRATION - UI - REGISTRATION_PREVIEW_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Setting up preview content has been started");

		StringBuilder templateContent = new StringBuilder();
		String platformLanguageCode = ApplicationContext.applicationLanguage();
		templateContent.append(templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE_PART_1, platformLanguageCode));
		templateContent.append(templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE_PART_2, platformLanguageCode));
		templateContent.append(templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE_PART_3, platformLanguageCode));
		String ackTemplateText = templateContent.toString();

		if (ackTemplateText != null && !ackTemplateText.isEmpty()) {
			ResponseDTO templateResponse = templateGenerator.generateTemplate(ackTemplateText,
					getRegistrationDTOFromSession(), templateManagerBuilder, RegistrationConstants.TEMPLATE_PREVIEW);
			if (templateResponse != null && templateResponse.getSuccessResponseDTO() != null) {
				Writer stringWriter = (Writer) templateResponse.getSuccessResponseDTO().getOtherAttributes()
						.get(RegistrationConstants.TEMPLATE_NAME);
				webView.getEngine().loadContent(stringWriter.toString());
				webView.getEngine().documentProperty()
						.addListener((observableValue, oldValue, document) -> listenToButton(document));
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_PREVIEW_PAGE);
				clearRegistrationData();
				goToHomePageFromRegistration();
			}
		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_PREVIEW_PAGE);
			clearRegistrationData();
			goToHomePageFromRegistration();
		}
	}

	private void listenToButton(Document document) {
		LOGGER.info("REGISTRATION - UI - REGISTRATION_PREVIEW_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Button click action happened on preview content");

		if (document == null) {
			return;
		}

		List<String> modifyElements = new ArrayList<>();
		modifyElements.add(RegistrationConstants.MODIFY_DEMO_INFO);
		modifyElements.add(RegistrationConstants.MODIFY_DOCUMENTS);
		modifyElements.add(RegistrationConstants.MODIFY_BIOMETRICS);
		for (String element : modifyElements) {
			Element button = document.getElementById(element);
			((EventTarget) button).addEventListener(RegistrationConstants.CLICK, event -> modifyElement(element),
					false);
		}
	}

	private void modifyElement(String element) {
		LOGGER.info("REGISTRATION - UI - REGISTRATION_PREVIEW_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Modifying Registration Information");

		if (element.equals(RegistrationConstants.MODIFY_DEMO_INFO)) {
			modifyDemographicInfo();
		} else if (element.equals(RegistrationConstants.MODIFY_DOCUMENTS)) {
			modifyDocuments();
		} else if (element.equals(RegistrationConstants.MODIFY_BIOMETRICS)) {
			modifyBiometrics();
		}
	}

	public void modifyDemographicInfo() {
		LOGGER.info("REGISTRATION - UI - REGISTRATION_PREVIEW_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Modifying Demographic Information");

		auditFactory.audit(AuditEvent.REG_PREVIEW_DEMO_EDIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			SessionContext.map().put("registrationPreview", false);
			SessionContext.map().put("demographicDetail", true);
			registrationController.showUINUpdateCurrentPage();
		} else {
			registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
					RegistrationConstants.DEMOGRAPHIC_DETAIL);
		}
	}

	public void modifyDocuments() {
		LOGGER.info("REGISTRATION - UI - REGISTRATION_PREVIEW_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Modifying Documents");

		auditFactory.audit(AuditEvent.REG_PREVIEW_DOC_EDIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			SessionContext.map().put("registrationPreview", false);
			if (!RegistrationConstants.DISABLE.equalsIgnoreCase(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_DISABLE_FLAG)))) {
				SessionContext.map().put("documentScan", true);
			} else {
				SessionContext.map().put("registrationPreview", true);
			}
			registrationController.showUINUpdateCurrentPage();
		} else {
			registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
					RegistrationConstants.DOCUMENT_SCAN);
		}
	}

	public void modifyBiometrics() {
		LOGGER.info("REGISTRATION - UI - REGISTRATION_PREVIEW_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Modifying Biometrics Information");

		auditFactory.audit(AuditEvent.REG_PREVIEW_BIO_EDIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			SessionContext.map().put("registrationPreview", false);

			long fingerPrintCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
					.getFingerprintDetailsDTO().stream().count();

			long irisCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
					.getIrisDetailsDTO().stream().count();
			if ((Boolean) SessionContext.userMap().get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION)) {
				SessionContext.map().put("biometricException", true);
			} else if (fingerPrintCount > 0) {
				SessionContext.map().put("fingerPrintCapture", true);
			} else if (irisCount > 0) {
				SessionContext.map().put("irisCapture", true);
			} else if (!RegistrationConstants.DISABLE.equalsIgnoreCase(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.FACE_DISABLE_FLAG)))) {
				SessionContext.map().put("faceCapture", true);
			} else {
				SessionContext.map().put("registrationPreview", true);
			}
			registrationController.showUINUpdateCurrentPage();
		} else {
			registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
					RegistrationConstants.FINGERPRINT_CAPTURE);
		}

	}
}
