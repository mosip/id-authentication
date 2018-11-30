package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

@Controller
public class ViewAckController extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private Logger LOGGER = AppConfig.getLogger(RegistrationApprovalController.class);

	private Timeline timeline;

	public void viewAck(String ackPath, Stage stage) {
		try (FileInputStream file = new FileInputStream(new File(ackPath))) {

			int startTime = 5;
			IntegerProperty timeSeconds = new SimpleIntegerProperty(startTime);

			Stage primaryStage = new Stage();
			autoCloseStage(primaryStage);
			Group root = new Group();
			Scene scene = new Scene(root, 800, 600);

			primaryStage.setTitle(RegistrationConstants.ACKNOWLEDGEMENT_FORM_TITLE);
			ImageView newimageView = new ImageView(new Image(file));
			HBox hbox = new HBox(newimageView);
			Label timerLabel = new Label();
			timerLabel.textProperty().bind(timeSeconds.asString());
			timerLabel.setTextFill(Color.RED);
			timerLabel.setStyle("-fx-font-size: 2em;");

			VBox vb = new VBox(20);
			vb.setAlignment(Pos.TOP_RIGHT);
			vb.setPrefWidth(scene.getWidth());
			vb.getChildren().addAll(timerLabel);

			root.getChildren().add(hbox);
			root.getChildren().add(vb);

			primaryStage.setOnShowing((WindowEvent event) -> {

				if (timeline != null) {
					timeline.stop();
				}
				timeSeconds.set(startTime);
				timeline = new Timeline();
				timeline.getKeyFrames()
						.add(new KeyFrame(Duration.seconds(startTime + 1), new KeyValue(timeSeconds, 0)));
				timeline.playFromStart();
			});

			primaryStage.setScene(scene);
			scene.setOnKeyPressed((KeyEvent event) -> {
				if (event.getCode() == KeyCode.ESCAPE) {
					primaryStage.close();
				}
			});
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(stage);
			primaryStage.resizableProperty().set(false);
			primaryStage.show();
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REGISTRATION_APPROVAL - VIEW_ACKNOWLEDGEMNT_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, ioException.getMessage());
		}
	}
}
