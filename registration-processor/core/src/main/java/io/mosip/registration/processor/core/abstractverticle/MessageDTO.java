package io.mosip.registration.processor.core.abstractverticle;

/**
 * This class contains parameters for communication between MOSIP stages
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 *
 */
public class MessageDTO {

	private String rid;
	private Boolean isValid;
	private Boolean internalError;
	

	private MessageBusAddress address;
	

	private Integer retryCount;

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public Boolean getInternalError() {
		return internalError;
	}

	public void setInternalError(Boolean internalError) {
		this.internalError = internalError;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public void setAddress(MessageBusAddress address) {
		this.address = address;
	}
	public MessageBusAddress getAddress() {
		return address;
	}
	@Override
	public String toString() {
		return "MessageDTO [rid=" + rid + ", isValid=" + isValid + 
				", address=" + address + ", retryCount=" + retryCount + ", internalError=" + internalError +"]";
	}

}
