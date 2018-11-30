package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Class for Registration Officer Update
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Controller
public class RegistrationOfficerUpdateController extends BaseController {

	@FXML
	private Label scanDate;

	@FXML
	private Label updateDate;

	@FXML
	private Label syncDate;

	@FXML
	private Label downloadDate;

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationOfficerUpdateController.class);

	/**
	 * Mapping dates to corresponding fields
	 */
	public void initialize() {

		LOGGER.debug("REGISTRATION - DISPLAY_DATE - REGISTRATION_OFFICER_UPDATE_CONTROLLER",
				APPLICATION_NAME, APPLICATION_ID,
				"Displaying date values for the corresponding fields");

		SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.DATE_FORMAT);
		updateDate.setText(sdf.format(new Date()));
		syncDate.setText(sdf.format(new Date()));
		downloadDate.setText(sdf.format(new Date()));
		scanDate.setText(sdf.format(new Date()));
	}

}
