package io.mosip.registration.util.healthcheck;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * Registration System Properties Checker
 * 
 * @author Sivasankar Thalavai
 * @since 1.0.0
 */
@Service
public class RegistrationSystemPropertiesChecker {

	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	private RegistrationSystemPropertiesChecker() {

	}

	/**
	 * Get Ethernet MAC Address
	 * 
	 * @return
	 */
	public static String getMachineId() {
		LOGGER.debug("REGISTRATION - REGISTRATIONSYSTEMPROPERTIESCHECKER - GETMACHINEID",
				APPLICATION_NAME, APPLICATION_ID,
				"Registration Get Machine Id had been called.");
		StringBuilder machineId = new StringBuilder();
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			NetworkInterface networkIf = NetworkInterface.getByInetAddress(inetAddress);
			byte[] mac = networkIf.getHardwareAddress();
			for (int i = 0; i < mac.length; i++) {
				machineId.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
		} catch (SocketException socketException) {
			LOGGER.debug("REGISTRATION - REGISTRATIONSYSTEMPROPERTIESCHECKER - GETMACHINEID",
					APPLICATION_NAME, APPLICATION_ID, "Socket Exception.");
		} catch (UnknownHostException e) {
			LOGGER.debug("REGISTRATION - REGISTRATIONSYSTEMPROPERTIESCHECKER - GETMACHINEID",
					APPLICATION_NAME, APPLICATION_ID, "Unknown Host Exception.");
		}
		return machineId.toString();
	}
}
