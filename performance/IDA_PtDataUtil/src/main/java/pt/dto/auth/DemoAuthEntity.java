package pt.dto.auth;

public class DemoAuthEntity {

	private String id;
	private String version;
	private String timestamp; // "2019-01-18T05:23:25.288" format in UTC timezone
	private String registrationId;
	private IdentityRequest request;

	public DemoAuthEntity() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public IdentityRequest getRequest() {
		return request;
	}

	public void setRequest(IdentityRequest request) {
		this.request = request;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
