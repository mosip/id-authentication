package io.mosip.registration.scheduler;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.exception.RegBaseCheckedException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

/**
 * @author M1047595
 *
 */
@Component
public class SchedulerUtil extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(SchedulerUtil.class);

	private static long startTime = System.currentTimeMillis();
	private static long refreshTime;
	private static long sessionTimeOut;
	private static Alert alert;
	private static Timer timer;
	private static Optional<ButtonType> res = Optional.empty();

	@FXML
	private static BorderPane content;

	/**
	 * Constructor to invoke scheduler method once login success
	 * 
	 * @throws IDISBaseCheckedException
	 * 
	 * @throws RegistrationBaseCheckedException
	 */
	public void startSchedulerUtil() throws RegBaseCheckedException {
		LOGGER.info("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
				"Timer has been called " + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()));
		alert = new Alert(AlertType.WARNING);		
		timer = new Timer("Timer");
		refreshTime = TimeUnit.SECONDS.toMillis(SessionContext.refreshedLoginTime());
		sessionTimeOut = TimeUnit.SECONDS.toMillis(SessionContext.idealTime());
		startTimerForSession();
	}

	/**
	 * Scheduling the task for session timeout
	 * 
	 * @throws IDISBaseCheckedException
	 * 
	 * @throws RegistrationBaseCheckedException
	 */
	private void startTimerForSession() {
		try {
			TimerTask task = new TimerTask() {
				public void run() {

					Platform.runLater(() -> {

						long endTime = System.currentTimeMillis();

						if ((endTime - startTime) >= refreshTime && (endTime - startTime) < sessionTimeOut) {
							LOGGER.info("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
									"The time task alert is called at interval of seconds "
											+ TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
							alert();
							if (res.isPresent())
								if (res.get().getText().equals("OK")) {
									startTime = System.currentTimeMillis();
									alert.close();
									res = Optional.empty();
								}
						} else if ((endTime - startTime) >= sessionTimeOut) {
							LOGGER.info("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
									"The time task login called at interval of seconds "
											+ TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
							alert.close();
							stopScheduler();
							// to clear the session object
							SessionContext.destroySession();
							// load login screen
							loadLoginScreen();
						}
					});
				}
			};
			timer.schedule(task, 1000, findPeriod(refreshTime, sessionTimeOut));
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * To find the scheduler duration to run the scheduler period
	 * 
	 * @param refreshTime
	 * @param sessionTimeOut
	 * @return
	 */
	private static int findPeriod(long refreshTime, long sessionTimeOut) {
		BigInteger b1 = BigInteger.valueOf(refreshTime);
		BigInteger b2 = BigInteger.valueOf(sessionTimeOut);
		BigInteger gcd = b1.gcd(b2);
		int schedulerTime = (int) ((gcd.intValue()) * 0.001);
		return schedulerTime;
	}

	/**
	 * To show the warning alert to user about session expire
	 */
	private static void alert() {
		alert.setTitle("TIMEOUT");
		alert.setHeaderText("You've been quite for a while.");
		alert.setContentText("Would you like to continue, please click ok.");
		if (!alert.isShowing()) {
			res = alert.showAndWait();
		}
	}

	public static void setCurrentTimeToStartTime() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * stop the scheduler
	 */
	public static void stopScheduler() {
		if (timer != null) {
			timer.cancel();
		}
	}

	private void loadLoginScreen() {
		try {
			Parent root = load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));
			getStage().setScene(getScene(root));
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		}
	}
}
