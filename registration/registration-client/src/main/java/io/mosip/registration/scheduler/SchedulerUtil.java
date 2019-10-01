package io.mosip.registration.scheduler;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.controller.device.WebCameraController;
import io.mosip.registration.controller.reg.PacketUploadController;
import io.mosip.registration.exception.RegBaseCheckedException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * The Class SchedulerUtil.
 *
 * @author Dinesh Ashokan
 */
@Component
public class SchedulerUtil extends BaseController {

	/** Instance of {@link Logger}. */
	private static final Logger LOGGER = AppConfig.getLogger(SchedulerUtil.class);

	/** The start time. */
	private static long startTime = System.currentTimeMillis();

	/** The refresh time. */
	private static long refreshTime;

	/** The session time out. */
	private static long sessionTimeOut;

	/** The timer. */
	private static Timer timer;

	private Timeline timeline;
	private Stage stage;
	private boolean isShowing;
	private PauseTransition delay;
	private int duration;

	@Autowired
	private WebCameraController webCameraController;

	@Autowired
	private ScanPopUpViewController scanPopUpViewController;

	@Autowired
	private PacketUploadController packetUploadController;

	/**
	 * Constructor to invoke scheduler method once login success.
	 *
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public void startSchedulerUtil() throws RegBaseCheckedException {
		LOGGER.info("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
				"Timer has been called " + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()));
		timer = new Timer("Timer");
		refreshTime = TimeUnit.SECONDS.toMillis(SessionContext.refreshedLoginTime());
		sessionTimeOut = TimeUnit.SECONDS.toMillis(SessionContext.idealTime());
		isShowing = false;
		duration = (int) ((sessionTimeOut - refreshTime) / 1000);
		startTimerForSession();
	}

	/**
	 * Scheduling the task for session timeout.
	 */
	private void startTimerForSession() {
		try {
			TimerTask task = new TimerTask() {
				public void run() {

					Platform.runLater(() -> {

						long endTime = System.currentTimeMillis();

						if (((endTime - startTime) >= refreshTime && (endTime - startTime) < sessionTimeOut)
								&& isShowing == false) {
							LOGGER.info("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
									"The time task reminder alert is called at interval of seconds "
											+ TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
							auditFactory.audit(AuditEvent.SCHEDULER_REFRESHED_TIMEOUT, Components.REFRESH_TIMEOUT,
									APPLICATION_NAME, AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());
							alert();
						}
					});
				}
			};
			timer.schedule(task, 1000, findTimeInterval(refreshTime, sessionTimeOut));
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * To find the scheduler duration to run the scheduler interval.
	 *
	 * @param refreshTime
	 *            the refresh time
	 * @param sessionTimeOut
	 *            the session time out
	 * @return the int
	 */
	private static int findTimeInterval(long refreshTime, long sessionTimeOut) {
		BigInteger b1 = BigInteger.valueOf(refreshTime);
		BigInteger b2 = BigInteger.valueOf(sessionTimeOut);
		BigInteger gcd = b1.gcd(b2);
		return ((int) ((gcd.intValue()) * 0.001));
	}

	/**
	 * To show the warning alert to user about session expire.
	 */
	private void alert() {
		IntegerProperty timeSeconds = new SimpleIntegerProperty(duration);

		if (!isShowing) {
			stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			isShowing = true;
			closeStage();
			GridPane root = new GridPane();
			Scene scene = new Scene(root, 420, 120);
			Label titleLbl = new Label();
			Label timerLabel = new Label();
			Label initialContent = new Label();
			Label middleContent = new Label();
			Label endContent = new Label();
			titleLbl.setText(RegistrationUIConstants.TIMEOUT_TITLE);
			initialContent.setText(RegistrationUIConstants.TIMEOUT_INITIAL);
			middleContent.setText(RegistrationUIConstants.TIMEOUT_MIDDLE);
			endContent.setText(RegistrationUIConstants.TIMEOUT_END);
			titleLbl.getStyleClass().addAll(RegistrationConstants.SCHEDULER_TITLE_STYLE);
			initialContent.getStyleClass().addAll(RegistrationConstants.SCHEDULER_CONTENT_STYLE);
			middleContent.getStyleClass().addAll(RegistrationConstants.SCHEDULER_CONTENT_STYLE);
			endContent.getStyleClass().addAll(RegistrationConstants.SCHEDULER_CONTENT_STYLE);
			timerLabel.getStyleClass().addAll(RegistrationConstants.SCHEDULER_TIMER_STYLE);
			timerLabel.textProperty().bind(timeSeconds.asString());
			VBox vbox = new VBox(20);
			HBox title = new HBox(20);
			HBox hbox = new HBox(20);
			HBox hboxContent = new HBox(20);
			hbox.setAlignment(Pos.CENTER);
			hboxContent.setAlignment(Pos.CENTER);
			title.getStyleClass().add(RegistrationConstants.SCHEDULER_TITLE_BORDER);
			hbox.setPrefWidth(scene.getWidth());
			hbox.setSpacing(3);
			hbox.getChildren().addAll(initialContent, timerLabel, middleContent);
			hboxContent.getChildren().add(endContent);
			title.getChildren().add(titleLbl);
			Button btn = new Button();
			btn.getStyleClass().addAll(RegistrationConstants.SCHEDULER_BTN_STYLE);
			btn.setText("OK");
			btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (stage.isShowing()) {
						stage.close();
						isShowing = false;
						delay.stop();
						setCurrentTimeToStartTime();
					}
				}
			});

			vbox.setSpacing(5);
			vbox.setAlignment(Pos.CENTER);
			vbox.setLayoutY(15);
			vbox.getChildren().addAll(title, hbox, hboxContent, btn);
			root.getStyleClass().add(RegistrationConstants.SCHEDULER_BORDER);
			root.getChildren().add(vbox);

			stage.setOnShowing((WindowEvent event) -> {
				if (timeline != null) {
					timeline.stop();
				}
				timeSeconds.set(duration);
				timeline = new Timeline();
				timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), new KeyValue(timeSeconds, 0)));
				timeline.play();
			});

			stage.setScene(scene);
			scene.setOnKeyPressed((KeyEvent event) -> {
				if (event.getCode() == KeyCode.ESCAPE) {
					stage.close();
				}
			});
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			scene.getStylesheets().add(classLoader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(fXComponents.getStage());
			stage.resizableProperty().set(false);
			stage.show();
		}
	}

	/**
	 * Sets the current time to start time when any event triggered to stage.
	 */
	public static void setCurrentTimeToStartTime() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * stop the scheduler.
	 */
	public static void stopScheduler() {
		if (timer != null) {
			timer.cancel();
		}
	}

	protected void closeStage() {
		delay = new PauseTransition(Duration.seconds((duration)));
		delay.setOnFinished(event -> stop());
		delay.play();
	}

	private void stop() {
		LOGGER.info("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
				"The time task for auto logout and login called ");
		auditFactory.audit(AuditEvent.SCHEDULER_SESSION_TIMEOUT, Components.SESSION_TIMEOUT, APPLICATION_NAME,
				AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());
		delay.stop();
		stage.close();
		// to stop scheduler
		stopScheduler();
		
		// close webcam window, if open.
		if (webCameraController.getWebCameraStage() != null && webCameraController.getWebCameraStage().isShowing()) {
			webCameraController.getWebCameraStage().close();
		}
		if (getAlertStage() != null && getAlertStage().isShowing()) {
			getAlertStage().close();
		}
		if (scanPopUpViewController.getPopupStage() != null && scanPopUpViewController.getPopupStage().isShowing()) {
			scanPopUpViewController.getPopupStage().close();
		}
		if (packetUploadController.getStage() != null && packetUploadController.getStage().isShowing()) {
			packetUploadController.getStage().close();
		}
		if (SessionContext.map() != null && SessionContext.map().get("alert")!=null) {
			Alert alret=(Alert)SessionContext.map().get("alert");
			alret.close();
		}
		if (SessionContext.map() != null && SessionContext.map().get("alertStage")!=null) {
			Stage alertStage=(Stage)SessionContext.map().get("alertStage");
			alertStage.close();
		}
		//Clear the Registration Data
		clearRegistrationData();
		// to clear the session object
		SessionContext.destroySession();
		// load login screen
		loadLoginScreen();
		isShowing = false;
	}

}
