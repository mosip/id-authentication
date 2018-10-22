package io.mosip.registration.processor.core.abstractverticle;

import java.io.Serializable;

/**
 * This class contains parameters for communication between MOSIP stages
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 *
 */
public class MessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MessageDTO() {
		super();
	}

	private String rid;
	private Boolean isValid;
	private Boolean internalError;

	private MessageBusAddress messageBusAddress;

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

	public void setMessageBusAddress(MessageBusAddress messageBusAddress) {
		this.messageBusAddress = messageBusAddress;
	}

	public MessageBusAddress getMessageBusAddress() {
		return messageBusAddress;
	}

	@Override
	public String toString() {
		return "MessageDTO [rid=" + rid + ", isValid=" + isValid + ", internalError=" + internalError
				+ ", messageBusAddress=" + messageBusAddress + ", retryCount=" + retryCount + "]";
	}

}
