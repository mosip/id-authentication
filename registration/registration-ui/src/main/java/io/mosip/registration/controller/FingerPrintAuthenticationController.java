package io.mosip.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.machinezoo.sourceafis.FingerprintTemplate;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.service.RegistrationApprovalService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

@Controller
public class FingerPrintAuthenticationController extends BaseController implements MFS100Event {

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

}
