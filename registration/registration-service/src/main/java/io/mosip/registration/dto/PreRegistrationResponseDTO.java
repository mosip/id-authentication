package io.mosip.registration.dto;

import java.sql.Timestamp;
import java.util.List;


/**
 * Pre Registration Response DTO
 * @author YASWANTH S
 *
 * @param <T>  is a ReseponseDataSyncDTO class type
 */
public class PreRegistrationResponseDTO<T> {

	/** The error details. */
	private List<Object> err;
	
	private String status;

	private Timestamp resTime;

	private List<T> response;

	public List<Object> getErr() {
		return err;
	}

	public void setErr(List<Object> err) {
		this.err = err;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getResTime() {
		return resTime;
	}

	public void setResTime(Timestamp resTime) {
		this.resTime = resTime;
	}

	public List<T> getResponse() {
		return response;
	}

	public void setResponse(List<T> response) {
		this.response = response;
	}

	

}
