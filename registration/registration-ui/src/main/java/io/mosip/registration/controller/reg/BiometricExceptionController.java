package io.mosip.registration.controller.reg;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

@Controller
public class BiometricExceptionController extends BaseController implements Initializable {

	@FXML
	private ImageView leftEye;

	@FXML
	private ImageView rightEye;

	@FXML
	private Label leftLittle;
	@FXML
	private Label leftIndex;
	@FXML
	private Label leftMiddle;
	@FXML
	private Label leftRing;
	@FXML
	private Label leftThumb;
	@FXML
	private Label rightIndex;
	@FXML
	private Label rightLittle;
	@FXML
	private Label rightMiddle;
	@FXML
	private Label rightRing;
	@FXML
	private Label rightThumb;
	@FXML
	private Pane leftHandPane;
	@FXML
	private Pane rightHandPane;
	@FXML
	private Pane leftEyePane;
	@FXML
	private Pane rightEyePane;
	@FXML
	private Button previousBtn;
	@FXML
	private ImageView homePageImg;
	@FXML
	private Text homePageLbl;
	@FXML
	private AnchorPane biometricException;
	@FXML
	private AnchorPane biometricExceptionLayout;

	@Autowired
	private RegistrationController registrationController;
	
	@Autowired
	private UserOnboardController userOnboardController;

	private List<String> fingerList = new ArrayList<>();
	private List<String> irisList = new ArrayList<>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fingerList.clear();
		irisList.clear();
		fingerExceptionListener(leftLittle);
		fingerExceptionListener(leftIndex);
		fingerExceptionListener(leftMiddle);
		fingerExceptionListener(leftRing);
		fingerExceptionListener(leftThumb);
		fingerExceptionListener(rightIndex);
		fingerExceptionListener(rightLittle);
		fingerExceptionListener(rightMiddle);
		fingerExceptionListener(rightRing);
		fingerExceptionListener(rightThumb);
		irisExceptionListener(leftEye);
		irisExceptionListener(rightEye);
		if ((boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {
			previousBtn.setVisible(false);			
		}else {
			previousBtn.setVisible(true);
			homePageLbl.setVisible(false);
			homePageImg.setVisible(false);
			biometricExceptionLayout.getStyleClass().add("removeBorderStyle");
		}
	}

	private void fingerExceptionListener(Label fingerLabel) {
		SimpleBooleanProperty toggleFunctionForFinger = new SimpleBooleanProperty(false);
		toggleFunctionForFinger.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ImageView image;
				if (fingerLabel.getId().contains("left")) {
					image = (ImageView) leftHandPane.lookup("#" + fingerLabel.getId() + "Img");
				} else {
					image = (ImageView) rightHandPane.lookup("#" + fingerLabel.getId() + "Img");
				}
				if (newValue) {
					fingerList.add(fingerLabel.getId());
					image.setVisible(true);
				} else {
					if (fingerList.indexOf(fingerLabel.getId()) >= 0) {
						fingerList.remove(fingerLabel.getId());
					}
					image.setVisible(false);
				}
				if (fingerList.stream().anyMatch(fingerType -> fingerType.contains("left"))) {
					leftHandPane.setStyle("-fx-border-color: black");
				} else {
					leftHandPane.setStyle("-fx-border-color: white");
				}
				if (fingerList.stream().anyMatch(fingerType -> fingerType.contains("right"))) {
					rightHandPane.setStyle("-fx-border-color: black");
				} else {
					rightHandPane.setStyle("-fx-border-color: white");
				}
			}
		});

		fingerLabel.setOnMouseClicked((event) -> {
			toggleFunctionForFinger.set(!toggleFunctionForFinger.get());
		});
	}

	private void irisExceptionListener(ImageView irisImage) {
		SimpleBooleanProperty toggleFunctionForIris = new SimpleBooleanProperty(false);
		Pane irisPane = (Pane) biometricException.lookup("#" + irisImage.getId() + "Pane");
		toggleFunctionForIris.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					irisList.add(irisImage.getId());
					irisPane.setStyle("-fx-border-color: black");
				} else {
					if (irisList.indexOf(irisImage.getId()) >= 0) {
						irisList.remove(irisImage.getId());
					}
					irisPane.setStyle("-fx-border-color: white");
				}
			}
		});
		irisImage.setOnMouseClicked((event) -> {
			toggleFunctionForIris.set(!toggleFunctionForIris.get());
		});
	}

	public void goToNextPage() {
		if ((boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {
			userOnboardController.loadFingerPrint(fingerList, irisList);
		} else {
			exceptionDTOCreation();
			if (fingerList.isEmpty() && irisList.isEmpty()) {
				generateAlert(RegistrationConstants.ALERT_INFORMATION,
						RegistrationUIConstants.BIOMETRIC_EXCEPTION_ALERT);
			} else {
				registrationController.toggleBiometricExceptionVisibility(false);
				registrationController.toggleFingerprintCaptureVisibility(true);
			}
		}
	}

	private void exceptionDTOCreation() {
		List<String> bioList = new ArrayList<>();
		bioList.addAll(fingerList);
		bioList.addAll(irisList);
		if (!bioList.isEmpty()) {
			RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.REGISTRATION_DATA);
			List<BiometricExceptionDTO> biometricExceptionList = registrationDTO.getBiometricDTO()
					.getApplicantBiometricDTO().getBiometricExceptionDTO();
			biometricExceptionList.clear();
			bioList.forEach(bioType -> {
				BiometricExceptionDTO biometricExceptionDTO = new BiometricExceptionDTO();
				if (bioType.contains("iris")) {
					biometricExceptionDTO.setBiometricType("iris");
				} else {
					biometricExceptionDTO.setBiometricType("fingerprint");
				}
				biometricExceptionDTO.setMissingBiometric(bioType);
				biometricExceptionList.add(biometricExceptionDTO);
			});
		}
	}

	public void goToPreviousPage() {
		exceptionDTOCreation();
		if (fingerList.isEmpty() && irisList.isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.BIOMETRIC_EXCEPTION_ALERT);
		} else {
			registrationController.getDemoGraphicTitlePane().setExpanded(true);
		}
	}

}
