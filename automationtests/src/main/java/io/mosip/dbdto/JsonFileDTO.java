package io.mosip.dbdto;

import java.io.Serializable;

import lombok.Data;

@Data
public class JsonFileDTO implements Serializable {
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public JsonRequestDTO getRequest() {
		return request;
	}

	public void setRequest(JsonRequestDTO request) {
		this.request = request;
	}

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String version;
	
	private String requestTime;
	
	private String metadata;
	
	private JsonRequestDTO request;

}
