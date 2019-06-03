package io.mosip.registration.dto;

import java.util.Map;

/**
 * dto class for email and sms notification
 * 
 * @author Dinesh Ashokan
 *
 */
public class NotificationDTO {
	private Map<String, String> request;
	private String status;
	private String requesttime;

	/**
	 * @return the request
	 */
	public Map<String, String> getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(Map<String, String> request) {
		this.request = request;
	}

	/**
	 * @return the requesttime
	 */
	public String getRequesttime() {
		return requesttime;
	}

	/**
	 * @param requesttime the requesttime to set
	 */
	public void setRequesttime(String requesttime) {
		this.requesttime = requesttime;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
