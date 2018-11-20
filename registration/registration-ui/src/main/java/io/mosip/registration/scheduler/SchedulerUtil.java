package io.mosip.registration.scheduler;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_SHEDULER_ARG_EXCEPTION;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_SHEDULER_STATE_EXCEPTION;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.LoginController;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * @author M1047595
 *
 */
@Component
public class SchedulerUtil {

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

	@Autowired
	AuditFactory auditFactory;

	/**
	 * Constructor to invoke scheduler method once login success
	 * 
	 * @throws IDISBaseCheckedException
	 * 
	 * @throws RegistrationBaseCheckedException
	 */
	public void startSchedulerUtil() throws RegBaseCheckedException {
		alert = new Alert(AlertType.WARNING);
		LOGGER.debug("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
				"Timer has been called " + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()));
		timer = new Timer("Timer");
		refreshTime = TimeUnit.SECONDS.toMillis(SessionContext.getInstance().getRefreshedLoginTime());
		sessionTimeOut = TimeUnit.SECONDS.toMillis(SessionContext.getInstance().getIdealTime());
		startTimerForSession();
	}

	/**
	 * Scheduling the task for session timeout
	 * 
	 * @throws IDISBaseCheckedException
	 * 
	 * @throws RegistrationBaseCheckedException
	 */
	private static void startTimerForSession() throws RegBaseCheckedException {
		try {
			TimerTask task = new TimerTask() {
				public void run() {

					Platform.runLater(() -> {

						long endTime = System.currentTimeMillis();

						if ((endTime - startTime) >= refreshTime && (endTime - startTime) < sessionTimeOut) {
							LOGGER.debug("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
									"The time task alert is called at interval of seconds "
											+ TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
							alert();
							if (res.isPresent())
								if(res.get().getText().equals("OK")) {
									startTime = System.currentTimeMillis();
									alert.close();
									res = Optional.empty();
								}
						} else if ((endTime - startTime) >= sessionTimeOut) {
							LOGGER.debug("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
									"The time task login called at interval of seconds "
											+ TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
							alert.close();
							stopScheduler();
							// to clear the session object
							SessionContext.destroySession();
							try {
								content = BaseController.load(getClass().getResource("/fxml/RegistrationLogin.fxml"));
								String loginModeFXMLpath = "/fxml/LoginWithCredentials.fxml";
								AnchorPane loginType = BaseController.load(getClass().getResource(loginModeFXMLpath));
								content.setCenter(loginType);
							} catch (IOException ioException) {
								LOGGER.error("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
							}
							
							LoginController.getScene().setRoot(content);
						}
					});
				}
			};
			timer.schedule(task, 1000, findPeriod(refreshTime, sessionTimeOut));
		}catch (IllegalArgumentException illegalArgumentException) {
			throw new RegBaseCheckedException(REG_UI_SHEDULER_ARG_EXCEPTION.getErrorCode(),
					REG_UI_SHEDULER_ARG_EXCEPTION.getErrorMessage());
		} catch (IllegalStateException illegalStateException) {
			throw new RegBaseCheckedException(REG_UI_SHEDULER_STATE_EXCEPTION.getErrorCode(),
					REG_UI_SHEDULER_STATE_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.REG_UI_SHEDULER_RUNTIME_EXCEPTION,
			runtimeException.getMessage());
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
	
}
