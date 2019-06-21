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
		String command = "ipconfig /all";
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()));
		while (true) {
			String line = bufferedReader.readLine();
			if (line != null) {
				Pattern englishPattern = Pattern.compile(".*Physical Address.*: (.*)");
				Pattern frenchPattern = Pattern.compile(".*Adresse physique.*: (.*)");
				Pattern arabicPattern = Pattern.compile(".*العنوان الفعلي.*: (.*)");

				Matcher englishMatcher = englishPattern.matcher(line);
				if (englishMatcher.matches()) {
					windowsMachineId = englishMatcher.group(1);
					break;
				}

				Matcher frenchMatcher = frenchPattern.matcher(line);
				if (frenchMatcher.matches()) {
					windowsMachineId = frenchMatcher.group(1);
					break;
				}

				Matcher arabicMatcher = arabicPattern.matcher(line);
				if (arabicMatcher.matches()) {
					windowsMachineId = arabicMatcher.group(1);
					break;
				}
			}
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