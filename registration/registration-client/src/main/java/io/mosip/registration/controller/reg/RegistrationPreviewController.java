package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.registration.config.AppConfig;
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
import netscape.javascript.JSObject;

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

	@Autowired
	private RegistrationPreviewController previewController;
			
	@FXML
	public void goToPrevPage(ActionEvent event) {
		registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW, getPageDetails(RegistrationConstants.REGISTRATION_PREVIEW,RegistrationConstants.PREVIOUS));
	}

	@FXML
	public void goToNextPage(ActionEvent event) {
		if (consentOfApplicant.isSelected()) {
			getRegistrationDTOFromSession().getRegistrationMetaDataDTO()
					.setConsentOfApplicant(RegistrationConstants.CONCENT_OF_APPLICANT_SELECTED);
		} else {
			getRegistrationDTOFromSession().getRegistrationMetaDataDTO()
					.setConsentOfApplicant(RegistrationConstants.CONCENT_OF_APPLICANT_UNSELECTED);
		}
		
		if(getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			SessionContext.map().put("registrationPreview", false);
			SessionContext.map().put("operatorAuthenticationPane", true);
			registrationController.showUINUpdateCurrentPage();
		} else {
			registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW, getPageDetails(RegistrationConstants.REGISTRATION_PREVIEW,RegistrationConstants.NEXT));
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
				JSObject window = (JSObject) webView.getEngine()
						.executeScript(RegistrationConstants.TEMPLATE_JS_OBJECT);
				window.setMember(RegistrationConstants.TEMPLATE_REGISTRATION, previewController);
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

	public void modifyDemographicInfo() {
		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW, RegistrationConstants.DEMOGRAPHIC_DETAIL);
		

	}

	public void modifyDocuments() {
		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW, RegistrationConstants.DOCUMENT_SCAN);
	}

	public void modifyBiometrics() {
		SessionContext.map().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
		registrationController.showCurrentPage(RegistrationConstants.REGISTRATION_PREVIEW, RegistrationConstants.FINGERPRINT_CAPTURE);
	}
}
