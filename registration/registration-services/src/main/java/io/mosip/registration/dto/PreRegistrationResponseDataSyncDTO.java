package io.mosip.registration.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author M1046129
 *
 */
public class PreRegistrationResponseDataSyncDTO implements Serializable {

	private static final long serialVersionUID = 6402670047109104959L;

	private String transactionId;
	private List<String> preRegistrationIds;
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public List<String> getPreRegistrationIds() {
		return preRegistrationIds;
	}
	public void setPreRegistrationIds(List<String> preRegistrationIds) {
		this.preRegistrationIds = preRegistrationIds;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
