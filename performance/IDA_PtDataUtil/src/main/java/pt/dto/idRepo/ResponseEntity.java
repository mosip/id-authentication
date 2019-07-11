package pt.dto.idRepo;

public class ResponseEntity {

	private String id;
	private String timestamp;
	private String registrationId;
	private String status;
	private ResponseData response;

	public ResponseEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ResponseData getResponse() {
		return response;
	}

	public void setResponse(ResponseData response) {
		this.response = response;
	}
}
