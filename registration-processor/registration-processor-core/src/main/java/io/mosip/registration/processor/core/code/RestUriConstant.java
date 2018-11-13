package io.mosip.registration.processor.core.code;

public class RestUriConstant {

	private String uri = null;
	public static final RestUriConstant authuri = new RestUriConstant("");
	public static final RestUriConstant regstatus = new RestUriConstant("");
	
	public RestUriConstant(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}
	

}
