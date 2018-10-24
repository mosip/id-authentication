package io.mosip.registration.processor.core.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * This class provides Server IP and Name
 * 
 * @author Kiran Raj M1048860
 * 
 */
public class ServerUtil {

	private ServerUtil(){
		
	}
	private static ServerUtil serverInstance=null;
	
	
	/**
	 * This method return singleton instance 
	 * 
	 * 
	 * @return The ServerUtil object
	 * 
	 */
	public synchronized static ServerUtil getServerUtilInstance() {
		
		if(serverInstance==null) {
			serverInstance=new ServerUtil();
			return serverInstance;
		}else {
			return serverInstance;
		}
		
	}
	
	public String serverIp;
	public String serverName;
	/**
	 * This method return ServerIp
	 * 
	 * 
	 * @return The ServerIp 
	 * 
	 */
	public String getServerIp() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
	
	/**
	 * This method return Server Host Name
	 * 
	 * 
	 * @return The ServerName 
	 * 
	 */
	public String getServerName() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName();
	}
	
}
