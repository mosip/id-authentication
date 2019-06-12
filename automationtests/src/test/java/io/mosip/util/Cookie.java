package io.mosip.util;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class to handle cookie utility
 * 
 * @author Vignesh
 *
 */
public class Cookie {

	private static final Logger COOKIELOG = Logger.getLogger(Cookie.class);
	public static SimpleDateFormat cookieDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	public static long expiryMinute = 20;
	/**
	 * The method will return cookie for the value
	 * 
	 * @param filename
	 * @param urlPath
	 * @param cookieName
	 * @return cookieValue
	 */
	public static String getCookie(String filename, String urlPath,String cookieName) {
		JSONObject objectData = null;
		try {
			objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
		} catch (Exception e) {
			COOKIELOG.error("Exception Occured in posting the request to get cookie:" + e.getMessage());
		}
		return CommonLibrary.postRequestToGetCookie(urlPath, objectData.toJSONString(), MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON).getCookie(cookieName);
	}
	/**
	 * The method return status of time expiry for generated cookie time and current time
	 * 
	 * @param cookieStartDate
	 * @return true or false
	 */
	public static boolean isTimeExpired(String cookieStartDate) {
		Date d1 = null;
		Date d2 = null;
		try {
			d2 = cookieDateTimeFormat.parse(getCookieCurrentDateTime());
			d1 = cookieDateTimeFormat.parse(cookieStartDate);
			long diff = d2.getTime() - d1.getTime();
			long diffMinutes = diff / (60 * 1000);
			if (diffMinutes > expiryMinute)
				return true;
			else
				return false;
		} catch (Exception e) {
			COOKIELOG.error(
					"Exception occured in getting the time difference between currentime and cookie start time. Plesae check the date passed in 'dd/MM/yyyy HH:mm:ss' or not: "
							+ e.getMessage());
			return false;
		}
	}
	
	/**
	 * The method return current date time for the cookie
	 * 
	 * @return String
	 */
	public static String getCookieCurrentDateTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); 
		LocalDateTime currentDateTime = LocalDateTime.now();
		return dtf.format(currentDateTime).toString();
	}
}
