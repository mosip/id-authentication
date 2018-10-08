package io.mosip.registration.processor.core.abstractverticle;

/**
 * @author Pranav Kumar
 *
 */
public class MessageDTO {

	private String rid;
	private Boolean isValid;
	private Integer retry;

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

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}

	@Override
	public String toString() {
		return "MessageDTO [rid=" + rid + ", isValid=" + isValid + ", retry=" + retry + "]";
	}
	

}
