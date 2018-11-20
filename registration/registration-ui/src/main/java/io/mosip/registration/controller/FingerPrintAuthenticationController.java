package io.mosip.registration.controller;

import java.io.IOException;
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
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.biometric.FingerprintProvider;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@Controller
public class FingerPrintAuthenticationController extends BaseController implements MFS100Event, Initializable {

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
	private ProgressIndicator scanProgress;

	@FXML
	private ComboBox<String> deviceCmbBox;

	@Autowired
	LoginService loginService;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	private FingerprintProvider fingerprintProvider = new FingerprintProvider();

	/**
	 * Stage
	 */
	private Stage primarystage;

	@Value("${QUALITY_SCORE}")
	private int qualityScore;

	@Value("${CAPTURE_TIME_OUT}")
	private int captureTimeOut;

	private MFS100 fpDevice = new MFS100(this, "");

	private String minutia = "";

	@Autowired
	BaseController baseController;

	public FingerPrintAuthenticationController() {
		System.out.println("Inside FingerPrintAuthenticationController constructor.......");
	}

	public void init(BaseController parentControllerObj) throws IOException {
		baseController = parentControllerObj;
	}

	private FingerData fingerDataContent;
	private String errorMessage;

	public void scanFinger(ActionEvent event) {
		primarystage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		if (fpDevice.Init() != 0) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.DEVICE_INFO_MESSAGE, RegistrationConstants.DEVICE_FP_NOT_FOUND);
		} else {
			fingerDataContent=null;
			errorMessage="";
			int scanoutput = fpDevice.StartCapture(qualityScore, captureTimeOut, false);
			int count = 0;

			if (scanoutput != 0) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.DEVICE_INFO_MESSAGE,
						fpDevice.GetLastError());
			}
			
			while (count < 10) {
				if (fingerDataContent != null || errorMessage != "") {
					break;
				} else {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				count++;
			}
			fingerPrintCheck(fingerDataContent,errorMessage);
			
		}
	}

	@Override
	public void OnCaptureCompleted(boolean status, int errorCode, String errorMsg, FingerData fingerData) {
		fingerDataContent = fingerData;
		errorMessage = errorMsg;
		if (fingerData != null) {
			FingerprintTemplate fingerprintTemplate = new FingerprintTemplate().convert(fingerData.ISOTemplate());
			minutia = fingerprintTemplate.serialize();

		}
	}
	
	private void fingerPrintCheck(FingerData fingerDataContent,String errorMessage) {
		if (fingerDataContent != null) {
			FingerprintTemplate fingerprintTemplate = new FingerprintTemplate().convert(fingerDataContent.ISOTemplate());
			minutia = fingerprintTemplate.serialize();
			validateFingerPrint(minutia);
			primarystage.close();
		}
	}

	/**
	 * Validate the Scanned Finger print
	 * 
	 * @param minutia
	 * @param detail
	 */
	public void validateFingerPrint(String minutia) {// String minutia, RegistrationUserDetail detail) {
		RegistrationUserDetail detail = loginService.getUserDetail("mosip");

		if (validateBiometric(minutia, detail)) {
			generateAlert("Info", AlertType.INFORMATION, RegistrationConstants.FINGER_PRINT_MATCH);
			baseController.getFingerPrintStatus();
		} else {
			generateAlert("Info", AlertType.INFORMATION, RegistrationConstants.FINGER_PRINT_MATCH);
			baseController.getFingerPrintStatus();
		}
	}

	/**
	 * Compare the scanned finger print with the database
	 * 
	 * @param minutia
	 * @param registrationUserDetail
	 * @return
	 */
	private boolean validateBiometric(String minutia, RegistrationUserDetail registrationUserDetail) {

		return registrationUserDetail.getUserBiometric().stream()
				.anyMatch(bio -> fingerprintProvider.scoreCalculator(minutia, bio.getBioMinutia()) > fingerPrintScore);
	}

	@Override
	public void OnPreview(FingerData fingerData) {
		fingerData.FingerImage();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		deviceCmbBox.getItems().clear();
		deviceCmbBox.setItems(FXCollections.observableArrayList(RegistrationConstants.ONBOARD_DEVICE_TYPES));

	}

	public void initData(Stage stage, List<Map<String, String>> approvalmapList) {
		/*
		 * authmapList = approvalmapList; primarystage = stage;
		 */}

	public void authenticate(ActionEvent event) {
		/*
		 * for (Map<String, String> map : authmapList) {
		 * registrationApprovalService.updateRegistration(map.get("registrationID"),
		 * map.get("statusComment"), map.get("statusCode")); }
		 * generateAlert(RegistrationConstants.STATUS, AlertType.INFORMATION,
		 * "Submitted Successfully"); authmapList.clear(); primarystage.close();
		 */}

}
