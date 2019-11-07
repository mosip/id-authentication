package io.mosip.registration.mdm.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDetail {
	private String env;
	private String mosipProcess;
	private String type;
	private String timeout;
	private int count;
	private String requestedScore;
	private List<String> exception;
	
	public RequestDetail(String type, String timeout, int count, String requestedScore, List<String> exception) {
		super();
		this.env="Staging";
		this.mosipProcess="Registration";
		this.type = type;
		this.timeout = timeout;
		this.count = count;
		this.requestedScore = requestedScore;
		this.exception = exception;
	}
	
	public RequestDetail(String env, String mosipProcess, String type, String timeout, int count, String requestedScore, List<String> exception) {
		super();
		this.env=env;
		this.mosipProcess=mosipProcess;
		this.type = type;
		this.timeout = timeout;
		this.count = count;
		this.requestedScore = requestedScore;
		this.exception = exception;
	}
	
	

}
