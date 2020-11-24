package io.mosip.authentication.core.partner.dto;

import java.util.Objects;

/**
 * Partner DTO
 * 
 * @author Loganathan Sekar
 *
 */
public class PartnerDTO {
	
	private String partnerId;
	private String partnerApiKey;
	private String mispLicenseKey;
	private String partnerName;
	private String policyId;
	private String status;
	private String certificateData;
	
	public PartnerDTO() {
	}
	
	public PartnerDTO(String partnerId, String partnerApiKey, String mispLicenseKey) {
		super();
		this.partnerId = partnerId;
		this.partnerApiKey = partnerApiKey;
		this.mispLicenseKey = mispLicenseKey;
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

	public String getMispLicenseKey() {
		return mispLicenseKey;
	}

	public void setMispLicenseKey(String mispLicenseKey) {
		this.mispLicenseKey = mispLicenseKey;
	}

	@Override
	public int hashCode() {
		return Objects.hash(mispLicenseKey, partnerApiKey, partnerId);
	}
	
	public String getCertificateData() {
		return certificateData;
	}

	public void setCertificateData(String certificateData) {
		this.certificateData = certificateData;
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
		return Objects.equals(mispLicenseKey, other.mispLicenseKey)
				&& Objects.equals(partnerApiKey, other.partnerApiKey) && Objects.equals(partnerId, other.partnerId);
	}

	@Override
	public String toString() {
		return "PartnerDTO [partnerId=" + partnerId + ", partnerApiKey=" + partnerApiKey + ", mispLicenseKey="
				+ mispLicenseKey + ", partnerName=" + partnerName + ", policyId=" + policyId + ", status=" + status
				+ "]";
	}
	
}
