package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * Class for Home Page
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Component
public class HomeController extends BaseController implements Initializable {
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(HomeController.class);

	@FXML
	private GridPane mainBox;
	@FXML
	public GridPane homeContent;
	@FXML
	public GridPane onboard;

	/**
	 * Building Home screen on Login success
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		try {

			LOGGER.info("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT", APPLICATION_NAME, APPLICATION_ID,
					"Constructing Registration Home Page");

			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)
					&& !(boolean) SessionContext.map()
							.get(RegistrationConstants.ONBOARD_USER_UPDATE)) {
				auditFactory.audit(AuditEvent.NAV_ON_BOARD_USER, Components.NAVIGATION, APPLICATION_NAME,
						AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

				onboard.setVisible(true);
				SessionContext.map().remove(RegistrationConstants.USER_ONBOARD_DATA);
				SessionContext.map().remove(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION);
				SessionContext.map().remove(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);
			} else {
				auditFactory.audit(AuditEvent.NAV_HOME, Components.NAVIGATION, SessionContext.userContext().getUserId(),
						AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

				if ((boolean) SessionContext.map()
						.get(RegistrationConstants.ONBOARD_USER_UPDATE)) {
				}
				homeContent.setVisible(true);
			}
			
			getScene(mainBox);

		} catch (RuntimeException ioException) {

			LOGGER.error(LoggerConstants.LOG_REG_HOME, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		}
	}

	/**
	 * @return the mainBox
	 */
	public GridPane getMainBox() {
		return mainBox;
	}

	/**
	 * @param mainBox the mainBox to set
	 */
	public void setMainBox(GridPane mainBox) {
		this.mainBox = mainBox;
	}
}
