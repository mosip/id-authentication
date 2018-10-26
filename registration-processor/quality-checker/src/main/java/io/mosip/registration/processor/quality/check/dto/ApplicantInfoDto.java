package io.mosip.registration.processor.quality.check.dto;

import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.Demographic;
import io.mosip.registration.processor.core.packet.dto.Photograph;

public class ApplicantInfoDto extends Demographic{

	private BiometericData BiometericData;
	private Photograph applicantPhoto;
	
	
	public ApplicantInfoDto() {
		super();
	}


	public BiometericData getBiometericData() {
		return BiometericData;
	}


	public void setBiometericData(BiometericData biometericData) {
		BiometericData = biometericData;
	}


	public Photograph getApplicantPhoto() {
		return applicantPhoto;
	}


	public void setApplicantPhoto(Photograph applicantPhoto) {
		this.applicantPhoto = applicantPhoto;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((BiometericData == null) ? 0 : BiometericData.hashCode());
		result = prime * result + ((applicantPhoto == null) ? 0 : applicantPhoto.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicantInfoDto other = (ApplicantInfoDto) obj;
		if (BiometericData == null) {
			if (other.BiometericData != null)
				return false;
		} else if (!BiometericData.equals(other.BiometericData))
			return false;
		if (applicantPhoto == null) {
			if (other.applicantPhoto != null)
				return false;
		} else if (!applicantPhoto.equals(other.applicantPhoto))
			return false;
		return true;
	}
	
}
