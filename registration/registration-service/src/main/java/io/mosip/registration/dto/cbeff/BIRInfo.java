/**
 * 
 */
package io.mosip.registration.dto.cbeff;

import java.util.Date;

import io.mosip.registration.dto.cbeff.jaxbclasses.BIRInfoType;

/**
 * @author Ramadurai Pandian
 *
 */
public class BIRInfo {

	private String creator;
	private String index;
	private byte[] payload;
	private Boolean integrity;
	private Date creationDate;
	private Date notValidBefore;
	private Date notValidAfter;

	public String getCreator() {
		return creator;
	}

	public String getIndex() {
		return index;
	}

	public byte[] getPayload() {
		return payload;
	}

	public Boolean isIntegrity() {
		return integrity;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getNotValidBefore() {
		return notValidBefore;
	}

	public Date getNotValidAfter() {
		return notValidAfter;
	}

	public BIRInfo(BIRInfoBuilder bIRInfoBuilder) {
		this.creator = bIRInfoBuilder.creator;
		this.index = bIRInfoBuilder.index;
		this.payload = bIRInfoBuilder.payload;
		this.integrity = bIRInfoBuilder.integrity;
		this.creationDate = bIRInfoBuilder.creationDate;
		this.notValidBefore = bIRInfoBuilder.notValidBefore;
		this.notValidAfter = bIRInfoBuilder.notValidAfter;
	}

	public static class BIRInfoBuilder {
		private String creator;
		private String index;
		private byte[] payload;
		private Boolean integrity;
		private Date creationDate;
		private Date notValidBefore;
		private Date notValidAfter;

		public BIRInfoBuilder withCreator(String creator) {
			this.creator = creator;
			return this;
		}

		public BIRInfoBuilder withIndex(String index) {
			this.index = index;
			return this;
		}

		public BIRInfoBuilder withPayload(byte[] payload) {
			this.payload = payload;
			return this;
		}

		public BIRInfoBuilder withIntegrity(Boolean integrity) {
			this.integrity = integrity;
			return this;
		}

		public BIRInfoBuilder withCreationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public BIRInfoBuilder withNotValidBefore(Date notValidBefore) {
			this.notValidBefore = notValidBefore;
			return this;
		}

		public BIRInfoBuilder withNotValidAfter(Date notValidAfter) {
			this.notValidAfter = notValidAfter;
			return this;
		}

		public BIRInfo build() {
			return new BIRInfo(this);
		}

	}

	public BIRInfoType toBIRInfo() {
		BIRInfoType bIRInfoType = new BIRInfoType();
		if (getCreator() != null && getCreator().length() > 0) {
			bIRInfoType.setCreator(getCreator());
		}
		if (isIntegrity() != null) {
			bIRInfoType.setIntegrity(isIntegrity());
			;
		}
		if (getCreator() != null && getCreator().length() > 0) {
			bIRInfoType.setCreator(getCreator());
		}
		if (getPayload() != null && getPayload().length > 0) {
			bIRInfoType.setPayload(getPayload());
		}
		if (getCreationDate() != null) {
			bIRInfoType.setCreationDate(getCreationDate());
		}
		if (getNotValidAfter() != null) {
			bIRInfoType.setNotValidAfter(getNotValidAfter());
		}
		if (getNotValidBefore() != null) {
			bIRInfoType.setNotValidBefore(getNotValidBefore());
		}

		return bIRInfoType;
	}

}
