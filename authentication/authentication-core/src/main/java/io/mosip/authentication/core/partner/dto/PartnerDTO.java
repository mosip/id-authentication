package io.mosip.authentication.core.partner.dto;

/**
 * Partner DTO
 * 
 * @author Loganathan Sekar
 *
 */
public class PartnerDTO {
	
	private String partnerId;
	private String partnerApiKey;
	private String partnerName;
	private String policyId;
	private String status;
	
	public PartnerDTO() {
	}
	
	public PartnerDTO(String partnerId, String partnerApiKey) {
		super();
		this.partnerId = partnerId;
		this.partnerApiKey = partnerApiKey;
	}



	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getPartnerApiKey() {
		return partnerApiKey;
	}
	public void setPartnerApiKey(String partnerApiKey) {
		this.partnerApiKey = partnerApiKey;
	}
	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((partnerApiKey == null) ? 0 : partnerApiKey.hashCode());
		result = prime * result + ((partnerId == null) ? 0 : partnerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartnerDTO other = (PartnerDTO) obj;
		if (partnerApiKey == null) {
			if (other.partnerApiKey != null)
				return false;
		} else if (!partnerApiKey.equals(other.partnerApiKey))
			return false;
		if (partnerId == null) {
			if (other.partnerId != null)
				return false;
		} else if (!partnerId.equals(other.partnerId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PartnerDTO [partnerId=" + partnerId + ", partnerApiKey=" + partnerApiKey + ", partnerName="
				+ partnerName + ", policyId=" + policyId + ", status=" + status + "]";
	}
	
	
	
}
