package io.mosip.registration.util.healthcheck;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.util.reader.PropertyFileReader;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

/**
 * Registration Health Checker Utility
 * 
 * @author Sivasankar Thalavai
 * @since 1.0.0
 */
@Component
public class RegistrationAppHealthCheckUtil {

	private static MosipLogger LOGGER;

	private static SystemInfo systemInfo;
	private static OperatingSystem operatingSystem;

	static {
		systemInfo = new SystemInfo();
		operatingSystem = systemInfo.getOperatingSystem();
	}

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	private RegistrationAppHealthCheckUtil() {

	}

	/**
	 * Checks the Internet connectivity
	 * 
	 * @return
	 * @throws URISyntaxException
	 */
	public static boolean isNetworkAvailable() {
		LOGGER.debug("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISNETWORKAVAILABLE",
				APPLICATION_NAME, APPLICATION_ID,
				"Registration Network Checker had been called.");
		boolean isNWAvailable = false;
		try {
			HttpURLConnection connection = null;
			System.setProperty("java.net.useSystemProxies", "true");
			int timeout = Integer.parseInt(PropertyFileReader.getPropertyValue("ONLINE_CONNECT_URL_TIMEOUT"));
			URL url = new URL(PropertyFileReader.getPropertyValue("ONLINE_CONNECT_URL"));
			List<Proxy> proxyList = ProxySelector.getDefault().select(new URI(url.toString()));
			Proxy proxy = proxyList.get(0);
			connection = (HttpURLConnection) url.openConnection(proxy);
			connection.setConnectTimeout(timeout);
			connection.connect();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				isNWAvailable = true;
			}
			LOGGER.debug("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISNETWORKAVAILABLE",
					APPLICATION_NAME, APPLICATION_ID, "Internet Access Available.");
		} catch (IOException ioException) {
			LOGGER.debug("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISNETWORKAVAILABLE",
					APPLICATION_NAME, APPLICATION_ID, "No Internet Access.");
		} catch (URISyntaxException e) {

		}
		return isNWAvailable;
	}

	/**
	 * Checks the Disk Space Availability
	 * 
	 * @return
	 */
	public static boolean isDiskSpaceAvailable() {
		LOGGER.debug("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE",
				APPLICATION_NAME, APPLICATION_ID,
				"Registration Disk Space Checker had been called.");
		boolean isSpaceAvailable = false;
		FileSystem fileSystem = operatingSystem.getFileSystem();
		String currentDirectory = System.getProperty("user.dir").substring(0, 3);
		OSFileStore[] fileStores = fileSystem.getFileStores();
		Long diskSpaceThreshold = Long.valueOf(PropertyFileReader.getPropertyValue("DISK_SPACE"));
		for (OSFileStore fs : fileStores) {
			if (currentDirectory.equalsIgnoreCase(fs.getMount())) {
				if (fs.getUsableSpace() > diskSpaceThreshold) {
					isSpaceAvailable = true;
					LOGGER.debug("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE",
							APPLICATION_NAME, APPLICATION_ID,
							"Required Disk Space Available.");
				} else {
					LOGGER.debug("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE",
							APPLICATION_NAME, APPLICATION_ID,
							"Required Disk Space Not Available.");
				}
			}
		}
		LOGGER.debug("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE",
				APPLICATION_NAME, APPLICATION_ID,
				"Registration Disk Space Checker had been ended.");
		return isSpaceAvailable;
	}
}
