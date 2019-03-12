package io.mosip.registration.processor.stages.demodedupe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
//remove the class when auth is fixed
@Component
public class BiometricValidation {
	
	@Autowired
	private PacketInfoDao packetInfoDao;
	
	public boolean validateBiometric(String duplicateUin, String regId) throws ApisResourceAccessException {
		/*
		 * authRequestDTO.setIdvId(duplicateUin);
		 * authRequestDTO.setAuthType(authTypeDTO); request.setIdentity(identityDTO);
		 * authRequestDTO.setRequest(request);
		 * 
		 * AuthResponseDTO authResponseDTO = (AuthResponseDTO)
		 * restClientService.postApi(ApiName.AUTHINTERNAL, "", "", authRequestDTO,
		 * AuthResponseDTO.class); return authResponseDTO != null &&
		 * authResponseDTO.getStatus() != null &&
		 * authResponseDTO.getStatus().equalsIgnoreCase("y");
		 */
		
		boolean isValid= false;	
		List<DemographicInfoDto> applicantDemoDto = packetInfoDao.findDemoById(regId);
		for (DemographicInfoDto demoDto : applicantDemoDto) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(demoDto.getDob());
			int year = calendar.get(Calendar.YEAR);
			if(year%2==0) {
				isValid=true;
			}
			
		}				
		return isValid;
	}
}
