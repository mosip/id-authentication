package io.mosip.registration.util.healthcheck;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_MAC_ADDRESS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;

/**
 * Registration System Properties Checker
 * 
 * @author Sivasankar Thalavai
 * @since 1.0.0
 */
public class RegistrationSystemPropertiesChecker {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationSystemPropertiesChecker.class);
	private static final String MAC_PATTERN = "([a-zA-Z0-9][a-zA-Z0-9]-" + "[a-zA-Z0-9][a-zA-Z0-9]-"
			+ "[a-zA-Z0-9][a-zA-Z0-9]-" + "[a-zA-Z0-9][a-zA-Z0-9]-" + "[a-zA-Z0-9][a-zA-Z0-9]-"
			+ "[a-zA-Z0-9][a-zA-Z0-9])";
	private static final String ETHERNET = "Ethernet";

	private RegistrationSystemPropertiesChecker() {

	}

	/**
	 * This method is used to get Ethernet MAC Address.
	 * 
	 * <p>
	 * Based on the Operating System, the command gets executed and the MAC Address
	 * is fetched from the result of execution of the command.
	 * </p>
	 * 
	 * @return machine ID
	 */
	public static String getMachineId() {
		String machineId = "";
		if (System.getProperty("os.name").equals("Linux")) {
			try {
				machineId = getLinuxMacAddress();
			} catch (IOException exIoException) {
				LOGGER.error(LOG_REG_MAC_ADDRESS, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						exIoException.getMessage() + ExceptionUtils.getStackTrace(exIoException));
			}
		} else {
			try {
				machineId = getWindowsMacAddress();
			} catch (IOException exIoException) {
				LOGGER.error(LOG_REG_MAC_ADDRESS, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						exIoException.getMessage() + ExceptionUtils.getStackTrace(exIoException));
			}
		}
		return machineId;
	}

	private static String getWindowsMacAddress() throws IOException {
		String windowsMachineId = "";
		LOGGER.info(LOG_REG_MAC_ADDRESS, RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Enternig to find the windows mac address");

		Process process = Runtime.getRuntime().exec("getmac /fo csv /v");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
			String line;
			while (null != (line = in.readLine())) {
				String[] lineSplitter = line.replaceAll("\"", "").split(",");
				if (lineSplitter[0].equals(ETHERNET)) {
					Pattern pattern = Pattern.compile(MAC_PATTERN);
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						LOGGER.info(LOG_REG_MAC_ADDRESS, RegistrationConstants.APPLICATION_NAME,
								RegistrationConstants.APPLICATION_ID, "MAC Address : " + matcher.group(0));
						windowsMachineId = matcher.group(0);
						break;
					}
				}
			}
			LOGGER.info(LOG_REG_MAC_ADDRESS, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Leaving the find windows mac address method ");
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_MAC_ADDRESS, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		}

		return windowsMachineId;
	}

	private static String getLinuxMacAddress() throws IOException {
		String linuxMachineId = "";
		List<String> devices = new ArrayList<>();
		Pattern pattern = Pattern.compile("^ *(.*):");
		try (BufferedReader in = new BufferedReader(new FileReader("/proc/net/dev"))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				Matcher m = pattern.matcher(line);
				if (m.find()) {
					devices.add(m.group(1));
				}
			}
			for (String device : devices) {
				try (FileReader reader1 = new FileReader("/sys/class/net/" + device + "/address")) {
					if (!device.equals("lo")) {
						BufferedReader in1 = new BufferedReader(reader1);
						linuxMachineId = in1.readLine();
						in1.close();
					}
				} catch (IOException exIoException) {
					LOGGER.error(LOG_REG_MAC_ADDRESS, RegistrationConstants.APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID,
							exIoException.getMessage() + ExceptionUtils.getStackTrace(exIoException));
				}
			}
		} catch (IOException exIoException) {
			LOGGER.error(LOG_REG_MAC_ADDRESS, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exIoException.getMessage() + ExceptionUtils.getStackTrace(exIoException));
		}
		return linuxMachineId;
	}
}