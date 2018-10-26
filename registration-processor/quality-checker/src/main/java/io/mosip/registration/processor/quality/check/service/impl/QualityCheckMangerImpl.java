package io.mosip.registration.processor.quality.check.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;

@Service
public class QualityCheckMangerImpl implements QualityCheckManager<String,ApplicantInfoDto> {

	@Override
	public void assignQCUser(String applicantRegistrationId) {
		System.out.println("Assigning QC User");
	}

	@Override
	public void sendAndVerify(String qcUserId, Object... param) {
		System.out.println("Assigning QC User");
	}

	@Override
	public List<ApplicantInfoDto> getPacketsforQCUser(String qcuserId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	

}
