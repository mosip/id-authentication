package io.mosip.registration.processor.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class provides Server IP and Name.
 *
 * @author Kiran Raj M1048860
 */
public class ServerUtil {

	/** The server instance. */
	private static ServerUtil serverInstance = null;

	/**
	 * Instantiates a new server util.
	 */
	private ServerUtil() {
		super();
	}

	/**
	 * This method return singleton instance.
	 *
	 * @return The ServerUtil object
	 */
	public static synchronized ServerUtil getServerUtilInstance() {

		if (serverInstance == null) {
			serverInstance = new ServerUtil();
			return serverInstance;
		} else {
			return serverInstance;
		}

	}

	/**
	 * This method return ServerIp.
	 *
	 * @return The ServerIp
	 *
	 */
	public String getServerIp() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	/**
	 * This method return Server Host Name.
	 *
	 * @return The ServerName
	 *
	 */
	public String getServerName() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName();
	}

}
