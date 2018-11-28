package io.mosip.registration.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@Controller
public class FingerPrintScanController extends BaseController implements Initializable {

	@Autowired
	private FingerPrintCaptureController fpScanController;
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private ImageView fingerPrintScanImage;

	private Stage primarystage;

	private List<FingerprintDetailsDTO> fingerprintDetailsDTOs = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fingerprintDetailsDTOs = new ArrayList<>();

	}

	public void init(AnchorPane selectedPane, Stage stage,List<FingerprintDetailsDTO> detailsDTOs) {
		anchorPane = selectedPane;
		primarystage = stage;
		fingerprintDetailsDTOs = detailsDTOs;
	}

	public void scanFinger() {
		if (anchorPane.getId() == fpScanController.leftHandPalmPane.getId()) {

			readFingerPrints("D:\\FINGER PRINTS\\LEFT HAND");

			Image img = loadImage("src/main/resources/images/LeftPalm.png");
			fingerPrintScanImage.setImage(img);
			generateAlert("INFO", AlertType.INFORMATION, "Fingerprint captured successfully!");
			primarystage.close();
			fpScanController.leftHandPalmImageview.setImage(img);

		} else if (anchorPane.getId() == fpScanController.rightHandPalmPane.getId()) {

			readFingerPrints("D:\\FINGER PRINTS\\RIGHT HAND");

			Image img = loadImage("src/main/resources/images/rightPalm.jpg");
			fingerPrintScanImage.setImage(img);
			generateAlert("INFO", AlertType.INFORMATION, "Fingerprint captured successfully!");
			primarystage.close();
			fpScanController.rightHandPalmImageview.setImage(img);

		} else if (anchorPane.getId() == fpScanController.thumbPane.getId()) {

			readFingerPrints("D:\\FINGER PRINTS\\THUMB");

			Image img = loadImage("src/main/resources/images/thumb.jpg");
			fingerPrintScanImage.setImage(img);
			generateAlert("INFO", AlertType.INFORMATION, "Fingerprint captured successfully!");
			primarystage.close();
			fpScanController.thumbImageview.setImage(img);

		}

	}

	private void readFingerPrints(String path) {
		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			paths.filter(Files::isRegularFile)
			.peek(f -> System.out.println(f.getFileName()))
			.forEach(e -> {
				File file = e.getFileName().toFile();
				if (file.getName().equals("ISOTemplate.iso")) {
					try {
				    
						FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
						byte[] allBytes = Files.readAllBytes(e.toAbsolutePath());

						System.out.println(file+"-----"+e.toFile().getParentFile().getName()+"-----"+file.getParent());
						fingerprintDetailsDTO.setFingerPrint(allBytes);
						fingerprintDetailsDTO.setFingerType(e.toFile().getParentFile().getName());
						fingerprintDetailsDTO.setFingerprintImageName(e.toFile().getParentFile().getName());
						fingerprintDetailsDTO.setNumRetry(1);
						fingerprintDetailsDTO.setForceCaptured(false);
						fingerprintDetailsDTO.setQualityScore(90);

						fingerprintDetailsDTOs.add(fingerprintDetailsDTO);
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Image loadImage(String imgPath) {
		Image img = null;
		try (FileInputStream file = new FileInputStream(new File(imgPath))) {
			img = new Image(file);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return img;
	}

	/**
	 * event class to exit from authentication window. pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {
		primarystage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		primarystage.close();

	}
}
