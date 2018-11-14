package io.mosip.registration.processor.core.code;

/**
 * The Class RestUriConstant.
 * @author Rishabh Keshari
 */
public class RestUriConstant {

	/** The uri. */
	private String uri = null;
	
	/** The Constant authuri. */
	public static final RestUriConstant authuri = new RestUriConstant("");
	
	/** The Constant regstatus. */
	public static final RestUriConstant regstatus = new RestUriConstant("http://104.211.220.190:8080/v0.1/registration-processor/registration-status/registrationstatus");
	
	/** The Constant regsync. */
	public static final RestUriConstant regsync = new RestUriConstant("http://104.211.220.190:8080/v0.1/registration-processor/registration-status/sync");
	
	/**
	 * Instantiates a new rest uri constant.
	 *
	 * @param uri the uri
	 */
	public RestUriConstant(String uri) {
		this.uri = uri;
	}
	
	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	

}
