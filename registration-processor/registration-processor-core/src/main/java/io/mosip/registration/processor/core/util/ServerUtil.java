package io.mosip.registration.processor.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides Server IP and Name.
 *
 * @author Kiran Raj M1048860
 */
public class ServerUtil {

	/** The server instance. */
	private static ServerUtil serverInstance = null;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerUtil.class);
	
	/** The host not found. */
	private String noHost  = "HOST_NOT_FOUND";

	/**
	 * 
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
	public String getServerIp() {

		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LOGGER.error(noHost, e.getMessage());
			return "UNKNOWN-HOST";
		}

	}

	/**
	 * This method return Server Host Name.
	 *
	 * @return The ServerName
	 *
	 */
	public String getServerName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			LOGGER.error(noHost, e.getMessage());
			return "UNKNOWN-HOST";
		}
	}

}
