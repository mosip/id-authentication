package io.mosip.registration.scheduler;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

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
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.exception.RegBaseCheckedException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

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
	
	/** The alert. */
	private static Alert alert;
	
	/** The timer. */
	private static Timer timer;
	
	
	private static Optional<ButtonType> res = Optional.empty();

	/**
	 * Constructor to invoke scheduler method once login success.
	 *
	 * @throws RegBaseCheckedException the reg base checked exception
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
	 * Scheduling the task for session timeout.
	 */
	private void startTimerForSession() {
		try {
			TimerTask task = new TimerTask() {
				public void run() {

					Platform.runLater(() -> {

						long endTime = System.currentTimeMillis();

						if ((endTime - startTime) >= refreshTime && (endTime - startTime) < sessionTimeOut) {
							LOGGER.info("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
									"The time task remainder alert is called at interval of seconds "
											+ TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
							auditFactory.audit(AuditEvent.SCHEDULER_REFRESHED_TIMEOUT, Components.REFRESH_TIMEOUT, APPLICATION_NAME,
									AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());
							alert();
							if (res.isPresent())
								if (res.get().getText().equals("OK")) {
									startTime = System.currentTimeMillis();
									alert.close();
									res = Optional.empty();
								}
						} else if ((endTime - startTime) >= sessionTimeOut) {
							LOGGER.info("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
									"The time task auto logout login called at interval of seconds "
											+ TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
							auditFactory.audit(AuditEvent.SCHEDULER_SESSION_TIMEOUT, Components.SESSION_TIMEOUT, APPLICATION_NAME,
									AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());
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
			timer.schedule(task, 1000, findTimeInterval(refreshTime, sessionTimeOut));
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - UI", APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * To find the scheduler duration to run the scheduler interval.
	 *
	 * @param refreshTime the refresh time
	 * @param sessionTimeOut the session time out
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
	private static void alert() {
		alert.setTitle(RegistrationUIConstants.TIMEOUT_TITLE);
		alert.setHeaderText(RegistrationUIConstants.TIMEOUT_HEADER);
		alert.setContentText(RegistrationUIConstants.TIMEOUT_CONTENT);
		if (!alert.isShowing()) {
			res = alert.showAndWait();
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
}
