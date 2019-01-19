package io.mosip.registration.util.healthcheck;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Registration System Properties Checker
 * 
 * @author Sivasankar Thalavai
 * @since 1.0.0
 */
public class RegistrationSystemPropertiesChecker {

	private RegistrationSystemPropertiesChecker() {

	}

	/**
	 * Get Ethernet MAC Address
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getMachineId() {
		String machineId = "";
		if (System.getProperty("os.name").equals("Linux")) {
			try {
				machineId = getLinuxMacAddress();
			} catch (IOException e) {

			}
		} else {
			try {
				machineId = getWindowsMacAddress();
			} catch (IOException e) {

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
				Pattern p = Pattern.compile(".*Physical Address.*: (.*)");
				Matcher m = p.matcher(line);
				if (m.matches()) {
					windowsMachineId = m.group(1);
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
					if (device.equals("eno1")) {
						BufferedReader in1 = new BufferedReader(reader1);
						linuxMachineId = in1.readLine();
						in1.close();
					}
				} catch (IOException e) {

				}
			}
		} catch (IOException e) {

		}
		return linuxMachineId;
	}
}