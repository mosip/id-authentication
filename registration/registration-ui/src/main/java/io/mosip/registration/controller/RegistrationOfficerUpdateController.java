package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.ui.constants.RegistrationUIConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Displaying date values for the corresponding fields");

		SimpleDateFormat sdf = new SimpleDateFormat(RegistrationUIConstants.DATE_FORMAT);
		updateDate.setText(sdf.format(new Date()));
		syncDate.setText(sdf.format(new Date()));
		downloadDate.setText(sdf.format(new Date()));
		scanDate.setText(sdf.format(new Date()));
	}
}
