package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Class for Sync Data
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

	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

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
