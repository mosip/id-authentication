package io.mosip.registration.controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.machinezoo.sourceafis.FingerprintTemplate;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.service.RegistrationApprovalService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@Controller
public class FingerPrintAuthenticationController extends BaseController implements MFS100Event,Initializable {

	@Autowired
	private RegistrationApprovalController registrationApprovalController;
	@Autowired
	private RegistrationApprovalService registrationApprovalService;
	@FXML
	private AnchorPane authenticateRootPane;
	@FXML
	private AnchorPane authenticateRootSubPane;
	@FXML
	private AnchorPane leftPalmAnchorPane;
	@FXML
	private AnchorPane rightPalmAnchorPane;
	@FXML
	private ImageView leftPalmImageView;
	@FXML
	private ImageView rightPalmImageView;
	@FXML
	private Button scanBtn;

	@FXML
	private ComboBox<String> deviceCmbBox;
	
	/**
	 * Stage
	 */
	private Stage primarystage;
	
	private List<Map<String, String>> authmapList;

	@Value("${QUALITY_SCORE}")
	private int qualityScore;

	@Value("${CAPTURE_TIME_OUT}")
	private int captureTimeOut;

	private MFS100 fpDevice = new MFS100(this, "");

	private String minutia = "";

	public String scanFingerPrint() {

		if (fpDevice.Init() != 0) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.DEVICE_INFO_MESSAGE, RegistrationConstants.DEVICE_FP_NOT_FOUND);
		} else {

			if (fpDevice.StartCapture(qualityScore, captureTimeOut, false) != 0) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.DEVICE_INFO_MESSAGE,
						fpDevice.GetLastError());
			}
		}
		return minutia;
	}

	@Override
	public void OnCaptureCompleted(boolean status, int errorCode, String errorMsg, FingerData fingerData) {
		if (fingerData != null) {
			FingerprintTemplate fingerprintTemplate = new FingerprintTemplate().convert(fingerData.ISOTemplate());
			minutia = fingerprintTemplate.serialize();

		}

	}

	@Override
	public void OnPreview(FingerData arg0) {

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		deviceCmbBox.getItems().clear();
		deviceCmbBox.setItems(FXCollections.observableArrayList(RegistrationConstants.ONBOARD_DEVICE_TYPES));
		
	}
	
	public void initData(Stage stage,List<Map<String, String>> approvalmapList) {
		authmapList=approvalmapList;
		primarystage=stage;
	}
	
	public void authenticate(ActionEvent event) {
		for (Map<String, String> map : authmapList) {
			registrationApprovalService.updateRegistration(map.get("registrationID"), map.get("statusComment"),
					map.get("statusCode"));
		}
		generateAlert(RegistrationConstants.STATUS, AlertType.INFORMATION, "Submitted Successfully");
		authmapList.clear();
		primarystage.close();
	}
	
}
