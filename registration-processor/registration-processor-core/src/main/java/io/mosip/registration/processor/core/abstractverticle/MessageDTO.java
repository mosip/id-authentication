package io.mosip.registration.processor.core.abstractverticle;

import java.io.Serializable;	

/**
 * This class contains parameters for communication between MOSIP stages.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
public class MessageDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new message DTO.
	 */
	public MessageDTO() {
		super();
	}

	/** The rid. */
	private String rid;
	
	/** The is valid. */
	private Boolean isValid;
	
	/** The internal error. */
	private Boolean internalError;

	/** The message bus address. */
	private MessageBusAddress messageBusAddress;

	/** The retry count. */
	private Integer retryCount;

	/**
	 * Gets the rid.
	 *
	 * @return the rid
	 */
	public String getRid() {
		return rid;
	}

	/**
	 * Sets the rid.
	 *
	 * @param rid the new rid
	 */
	public void setRid(String rid) {
		this.rid = rid;
	}

	/**
	 * Gets the checks if is valid.
	 *
	 * @return the checks if is valid
	 */
	public Boolean getIsValid() {
		return isValid;
	}

	/**
	 * Sets the checks if is valid.
	 *
	 * @param isValid the new checks if is valid
	 */
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * Gets the internal error.
	 *
	 * @return the internal error
	 */
	public Boolean getInternalError() {
		return internalError;
	}

	/**
	 * Sets the internal error.
	 *
	 * @param internalError the new internal error
	 */
	public void setInternalError(Boolean internalError) {
		this.internalError = internalError;
	}

	/**
	 * Gets the retry count.
	 *
	 * @return the retry count
	 */
	public Integer getRetryCount() {
		return retryCount;
	}

	/**
	 * Sets the retry count.
	 *
	 * @param retryCount the new retry count
	 */
	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * Sets the message bus address.
	 *
	 * @param messageBusAddress the new message bus address
	 */
	public void setMessageBusAddress(MessageBusAddress messageBusAddress) {
		this.messageBusAddress = messageBusAddress;
	}

	/**
	 * Gets the message bus address.
	 *
	 * @return the message bus address
	 */
	public MessageBusAddress getMessageBusAddress() {
		return messageBusAddress;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageDTO [rid=" + rid + ", isValid=" + isValid + ", internalError=" + internalError
				+ ", messageBusAddress=" + messageBusAddress + ", retryCount=" + retryCount + "]";
	}

}
