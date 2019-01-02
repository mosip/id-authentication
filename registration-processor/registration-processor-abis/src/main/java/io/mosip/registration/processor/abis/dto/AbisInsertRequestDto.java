package io.mosip.registration.processor.abis.dto;

public class AbisInsertRequestDto {
private String id;
private String ver;
private String requestId;
private String timestamp;
private String referenceId;
private String referenceURL;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getVer() {
	return ver;
}
public void setVer(String ver) {
	this.ver = ver;
}
public String getRequestId() {
	return requestId;
}
public void setRequestId(String requestId) {
	this.requestId = requestId;
}
public String getTimestamp() {
	return timestamp;
}
public void setTimestamp(String timestamp) {
	this.timestamp = timestamp;
}
public String getReferenceId() {
	return referenceId;
}
public void setReferenceId(String referenceId) {
	this.referenceId = referenceId;
}
public String getReferenceURL() {
	return referenceURL;
}
public void setReferenceURL(String referenceURL) {
	this.referenceURL = referenceURL;
}
}
