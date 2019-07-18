package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_1;
import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_2;
import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_3;
import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_4;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

@Controller
public class RegistrationPreviewController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationPreviewController.class);

	@FXML
	private WebView webView;
	
	@FXML
	private Button backBtn;

	@FXML
	private ImageView backImageView;

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
	private Button nextButton;

	private String consentText;

	private StringBuilder templateContent;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Image backInWhite = new Image(getClass().getResourceAsStream(RegistrationConstants.BACK_FOCUSED));
		Image backImage = new Image(getClass().getResourceAsStream(RegistrationConstants.BACK));

		backBtn.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				backImageView.setImage(backInWhite);
			} else {
				backImageView.setImage(backImage);
			}
		});

		nextButton.setDisable(true);

		String key = RegistrationConstants.REG_CONSENT + applicationContext.getApplicationLanguage();
		consentText = getValueFromApplicationContext(key);

		templateContent = new StringBuilder();

		if (getRegistrationDTOFromSession() != null && getRegistrationDTOFromSession().getSelectionListDTO() != null) {

			registrationNavlabel.setText(ApplicationContext.applicationLanguageBundle()
					.getString(RegistrationConstants.UIN_UPDATE_UINUPDATENAVLBL));
		}
		if (getRegistrationDTOFromSession() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
						.equals(RegistrationConstants.PACKET_TYPE_LOST)) {

			registrationNavlabel.setText(
					ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.LOSTUINLBL));
		}
	}

	@FXML
	public void goToPrevPage(ActionEvent event) {
		auditFactory.audit(AuditEvent.REG_PREVIEW_BACK, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, false);

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
				.filter(bio -> bio.getBiometricType().equalsIgnoreCase(RegistrationConstants.FINGERPRINT.toLowerCase()))
				.count();

		long irisCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
				.getBiometricExceptionDTO().stream()
				.filter(bio -> bio.getBiometricType().equalsIgnoreCase(RegistrationConstants.IRIS)).count();

		long fingerPrintExceptionCount = biomerticExceptionCount(RegistrationConstants.FINGERPRINT);

		long irisExceptionCount = biomerticExceptionCount(RegistrationConstants.IRIS);

		if (!RegistrationConstants.DISABLE
				.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.FACE_DISABLE_FLAG))) {
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.UIN_UPDATE_FACECAPTURE, true);
		} else if (irisCount > 0 || irisExceptionCount > 0) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_IRISCAPTURE, true);
		} else if (fingerPrintCount > 0 || fingerPrintExceptionCount > 0) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_FINGERPRINTCAPTURE, true);
		} else if (RegistrationConstants.ENABLE
				.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_DISABLE_FLAG))) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DOCUMENTSCAN, true);
		} else {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DEMOGRAPHICDETAIL, true);
		}
	}

	@FXML
	public void goToNextPage(ActionEvent event) {
		auditFactory.audit(AuditEvent.REG_PREVIEW_SUBMIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
		if (getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getConsentOfApplicant() != null) {
			if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, false);
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_OPERATORAUTHENTICATIONPANE, true);
				registrationController.showUINUpdateCurrentPage();
			} else {
				registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
						getPageDetails(RegistrationConstants.REGISTRATION_PREVIEW, RegistrationConstants.NEXT));
			}
			registrationController.goToAuthenticationPage();
		} else {
			nextButton.setDisable(false);
		}
	}

	public void setUpPreviewContent() {
		LOGGER.info("REGISTRATION - UI - REGISTRATION_PREVIEW_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Setting up preview content has been started");

		if (templateContent.length() == 0) {
			String platformLanguageCode = ApplicationContext.applicationLanguage();
			templateContent
					.append(templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE_PART_1, platformLanguageCode));
			templateContent
					.append(templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE_PART_2, platformLanguageCode));
			templateContent
					.append(templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE_PART_3, platformLanguageCode));
			templateContent
					.append(templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE_PART_4, platformLanguageCode));
		}
		String ackTemplateText = templateContent.toString();
		if(ApplicationContext.applicationLanguage().equalsIgnoreCase(ApplicationContext.localLanguage())) {
			ackTemplateText = ackTemplateText.replace("} / ${", "}  ${");
		}

		if (ackTemplateText != null && !ackTemplateText.isEmpty()) {
			templateGenerator.setConsentText(consentText);
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

		Element yes = document.getElementById(RegistrationConstants.REG_CONSENT_YES);
		Element no = document.getElementById(RegistrationConstants.REG_CONSENT_NO);
		((EventTarget) yes).addEventListener(RegistrationConstants.CLICK, event -> enableConsent(), false);
		((EventTarget) no).addEventListener(RegistrationConstants.CLICK, event -> disableConsent(), false);

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
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, false);
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DEMOGRAPHICDETAIL, true);
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

			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, false);
			if (!RegistrationConstants.DISABLE
					.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_DISABLE_FLAG))) {
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DOCUMENTSCAN, true);
			} else {
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, true);
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
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, false);
			
			long fingerPrintCount = getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.getFingerprintDetailsDTO().stream().count();

			long irisCount = getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.getIrisDetailsDTO().stream().count();
			if ((Boolean) SessionContext.userMap().get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION)) {
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_BIOMETRICEXCEPTION, true);
			} else if (fingerPrintCount > 0) {
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_FINGERPRINTCAPTURE, true);
			} else if (irisCount > 0) {

				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_IRISCAPTURE, true);
			} else if (!RegistrationConstants.DISABLE
					.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.FACE_DISABLE_FLAG))) {
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_FACECAPTURE, true);
			} else {
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, true);
			}
			registrationController.showUINUpdateCurrentPage();
		} else {
			if ((boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
				registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
						RegistrationConstants.GUARDIAN_BIOMETRIC);
			} else {
				if ((Boolean) SessionContext.userMap().get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION)) {
					registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
							RegistrationConstants.UIN_UPDATE_BIOMETRICEXCEPTION);
				} else if (RegistrationConstants.ENABLE.equalsIgnoreCase(
						getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_DISABLE_FLAG))) {
					registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
							RegistrationConstants.FINGERPRINT_CAPTURE);
				} else if (RegistrationConstants.ENABLE
						.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.IRIS_DISABLE_FLAG))) {
					registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
							RegistrationConstants.IRIS_CAPTURE);
				} else if (RegistrationConstants.ENABLE
						.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.FACE_DISABLE_FLAG))) {
					registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
							RegistrationConstants.FACE_CAPTURE);
				}
			}
		}
	}

	private void enableConsent() {
		getRegistrationDTOFromSession().getRegistrationMetaDataDTO().setConsentOfApplicant(RegistrationConstants.YES);
		nextButton.setDisable(false);
	}

	private void disableConsent() {
		getRegistrationDTOFromSession().getRegistrationMetaDataDTO().setConsentOfApplicant(RegistrationConstants.NO);
		nextButton.setDisable(false);
	}
}
