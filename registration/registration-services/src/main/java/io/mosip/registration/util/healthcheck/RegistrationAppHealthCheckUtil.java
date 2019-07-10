package io.mosip.registration.util.healthcheck;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.util.restclient.RestClientUtil;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.software.os.windows.WindowsOperatingSystem;

/**
 * Registration Health Checker Utility.
 *
 * @author Sivasankar Thalavai
 * @since 1.0.0
 */
public class RegistrationAppHealthCheckUtil {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationAppHealthCheckUtil.class);

	/** The system info. */
	private static SystemInfo systemInfo;
	
	/** The operating system. */
	private static OperatingSystem operatingSystem;

	static {
		systemInfo = new SystemInfo();
		operatingSystem = systemInfo.getOperatingSystem();
	}

	/**
	 * Instantiates a new registration app health check util.
	 */
	private RegistrationAppHealthCheckUtil() {

	}

	/**
	 * This method checks the Internet connectivity across the application.
	 * 
	 * <p>
	 * Creates a {@link HttpURLConnection} and opens a communications link to the
	 * resource referenced by this URL. If the connection is established
	 * successfully, this method will return true which indicates Internet Access
	 * available, otherwise, it will return false, indicating Internet Access not
	 * available.
	 * </p>
	 *
	 * @return true, if is network available and false, if it is not available.
	 */
	public static boolean isNetworkAvailable() {
		LOGGER.info("REGISTRATION - REGISTRATION APP HEALTHCHECK UTIL - ISNETWORKAVAILABLE", APPLICATION_NAME,
				APPLICATION_ID, "Registration Network Checker had been called.");
		return checkServiceAvailability(System.getProperty(RegistrationConstants.REG_HEALTH_CHECK_URL_PROPERTY));
	}

	/**
	 * This method checks the service availability.
	 * 
	 * <p>
	 * Creates a {@link HttpURLConnection} and opens a communications link to the
	 * resource referenced by this URL. If the connection is established
	 * successfully, this method will return true which indicates Internet Access
	 * available, otherwise, it will return false, indicating Internet Access not
	 * available.
	 * </p>
	 *
	 * @param serviceUrl
	 *            the service url which is required to open the communications link.
	 * @return true, if connection is available and false, if it is not available.
	 */
	public static boolean checkServiceAvailability(String serviceUrl) {
		boolean isNWAvailable = false;
		try {
			RestClientUtil.turnOffSslChecking();
			// acceptAnySSLCerticficate();
			// System.setProperty("java.net.useSystemProxies", "true");
			URL url = new URL(serviceUrl);
			// List<Proxy> proxyList = ProxySelector.getDefault().select(new
			// URI(url.toString()));
			// Proxy proxy = proxyList.get(0);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);
			connection.connect();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				isNWAvailable = true;
				LOGGER.info("REGISTRATION - REGISTRATION APP HEALTHCHECKUTIL - ISNETWORKAVAILABLE", APPLICATION_NAME,
						APPLICATION_ID, "Internet Access Available." + "====>" + connection.getResponseCode());
			} else {
				isNWAvailable = false;
				LOGGER.info("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISNETWORKAVAILABLE", APPLICATION_NAME,
						APPLICATION_ID, "Internet Access Not Available." + "====>" + connection.getResponseCode());
			}
		} catch (Exception exception) {
			LOGGER.error("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISNETWORKAVAILABLE" + isNWAvailable,
					APPLICATION_NAME, APPLICATION_ID, "No Internet Access." + ExceptionUtils.getStackTrace(exception));
		}
		return isNWAvailable;
	}

	/**
	 * This method checks for the Disk Space Availability.
	 * 
	 * <p>
	 * Gets the {@link FileSystem} of the {@link OperatingSystem} that is currently
	 * used. Gets all the {@link OSFileStore} from the fileSystem and takes the
	 * FileStore that matches the current directory and then checks whether the
	 * usable space of the fileStore is greater than required disk space threshold.
	 * If it is greater, then it indicates that the required disk space is
	 * available, else, the required space is not available.
	 * </p>
	 *
	 * @return true, if is disk space available and false, if space is not
	 *         available.
	 */
	public static boolean isDiskSpaceAvailable() {
		LOGGER.info("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE", APPLICATION_NAME,
				APPLICATION_ID, "Registration Disk Space Checker had been called.");
		boolean isSpaceAvailable = false;
		FileSystem fileSystem = operatingSystem.getFileSystem();
		String currentDirectory = System.getProperty("user.dir").substring(0, 3);
		OSFileStore[] fileStores = fileSystem.getFileStores();
		Long diskSpaceThreshold = 80000L;
		for (OSFileStore fs : fileStores) {
			if (currentDirectory.equalsIgnoreCase(fs.getMount())) {
				if (fs.getUsableSpace() > diskSpaceThreshold) {
					isSpaceAvailable = true;
					LOGGER.info("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE",
							APPLICATION_NAME, APPLICATION_ID, "Required Disk Space Available.");
				} else {
					LOGGER.info("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE",
							APPLICATION_NAME, APPLICATION_ID, "Required Disk Space Not Available.");
				}
			}
		}
		LOGGER.info("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE", APPLICATION_NAME,
				APPLICATION_ID, "Registration Disk Space Checker had been ended.");
		return isSpaceAvailable;
	}
	
	/**
	 * This method is used to accept any SSL certificate.
	 * 
	 * <p>
	 * Installs the all-trusting {@link TrustManager} by creating a null
	 * implementation which is treated as a successful validation and removing all
	 * other implementations.
	 * </p>
	 *
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws KeyManagementException
	 *             the key management exception
	 */
	public static void acceptAnySSLCerticficate() throws NoSuchAlgorithmException, KeyManagementException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}
	
	/** The Constant UNQUESTIONING_TRUST_MANAGER. */
	public static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
		 */
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
		 */
		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

	} };

	/**
	 * This method checks if the Operating System is windows.
	 *
	 * @return true, if the OS is windows
	 */
	public static boolean isWindows() {
		return operatingSystem instanceof WindowsOperatingSystem;

	}

	/**
	 * This method checks if the Operating System is Linux.
	 *
	 * @return true, if the OS is Linux
	 */
	public static boolean isLinux() {
		return operatingSystem instanceof LinuxOperatingSystem;
	}
}
