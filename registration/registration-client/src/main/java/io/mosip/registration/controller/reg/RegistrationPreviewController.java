package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebView;

@Controller
public class RegistrationPreviewController extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationPreviewController.class);

	@FXML
	private WebView webView;

	@FXML
	private CheckBox consentOfApplicant;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Autowired
	private TemplateGenerator templateGenerator;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private RegistrationController registrationController;

	@FXML
	public void goToPrevPage(ActionEvent event) {
		auditFactory.audit(AuditEvent.REG_PREVIEW_BACK, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
				getPageDetails(RegistrationConstants.REGISTRATION_PREVIEW, RegistrationConstants.PREVIOUS));
	}

	@FXML
	public void goToNextPage(ActionEvent event) {
		auditFactory.audit(AuditEvent.REG_PREVIEW_SUBMIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		if (consentOfApplicant.isSelected()) {
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
	}

	public void setUpPreviewContent() {
		try {
			String ackTemplateText = templateService.getHtmlTemplate(ACKNOWLEDGEMENT_TEMPLATE);
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
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error("REGISTRATION - UI - PREVIEW", APPLICATION_NAME, APPLICATION_ID,
					regBaseCheckedException.getMessage());
		}
	}

	private void listenToButton(Document document) {
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
		if (element.equals(RegistrationConstants.MODIFY_DEMO_INFO)) {
			modifyDemographicInfo();
		} else if (element.equals(RegistrationConstants.MODIFY_DOCUMENTS)) {
			modifyDocuments();
		} else if (element.equals(RegistrationConstants.MODIFY_BIOMETRICS)) {
			modifyBiometrics();
		}
	}

	public void modifyDemographicInfo() {
		auditFactory.audit(AuditEvent.REG_PREVIEW_DEMO_EDIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
				RegistrationConstants.DEMOGRAPHIC_DETAIL);
	}

	public void modifyDocuments() {
		auditFactory.audit(AuditEvent.REG_PREVIEW_DOC_EDIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
				RegistrationConstants.DOCUMENT_SCAN);
	}

	public void modifyBiometrics() {
		auditFactory.audit(AuditEvent.REG_PREVIEW_BIO_EDIT, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW,
				RegistrationConstants.FINGERPRINT_CAPTURE);
	}
}
