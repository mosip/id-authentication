package io.mosip.registration.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.mosip.registration.controller.BaseController;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

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
	
	/**
	 * Mapping dates to corresponding fields
	 */
	public void initialize() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy hh:mm:ss");
		updateDate.setText(sdf.format(new Date()));
		syncDate.setText(sdf.format(new Date()));
		downloadDate.setText(sdf.format(new Date()));
		scanDate.setText(sdf.format(new Date()));
	}
	
}
