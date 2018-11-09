package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.dto;

import java.io.Serializable;

import org.springframework.http.HttpMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestDetails.
 * @author Rishabh Keshari
 */
public class RequestDetails implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5549629875402269382L;
	
	
	/** The request method type. */
	private HttpMethod requestMethodType;
	
	/** The request url. */
	private String requestUrl;
	
	
	/**
	 * Instantiates a new request details.
	 *
	 * @param url the url
	 * @param requestType the request type
	 */
	public RequestDetails(String url, HttpMethod requestType) {
		super();
		this.requestUrl = url;
		this.requestMethodType = requestType;
	}
	
	
	/**
	 * Gets the request url.
	 *
	 * @return the request url
	 */
	public String getRequestUrl() {
		return requestUrl;
	}
	
	/**
	 * Gets the request method type.
	 *
	 * @return the request method type
	 */
	public HttpMethod getRequestMethodType() {
		return requestMethodType;
	}


	/**
	 * Sets the request method type.
	 *
	 * @param requestMethodType the new request method type
	 */
	public void setRequestMethodType(HttpMethod requestMethodType) {
		this.requestMethodType = requestMethodType;
	}


	/**
	 * Sets the request url.
	 *
	 * @param requestUrl the new request url
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestDetails [url=" + requestUrl + ", requestType=" + requestMethodType + "]";
	}

}
