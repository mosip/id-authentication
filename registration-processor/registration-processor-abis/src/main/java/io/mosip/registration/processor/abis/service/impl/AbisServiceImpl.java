package io.mosip.registration.processor.abis.service.impl;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.abis.dto.CandidateListDto;
import io.mosip.registration.processor.abis.dto.CandidatesDto;
import io.mosip.registration.processor.abis.dto.IdentityRequestDto;
import io.mosip.registration.processor.abis.dto.IdentityResponceDto;

@Service
public class AbisServiceImpl {

	public IdentityResponceDto deDupeCheck(IdentityRequestDto identityRequest) {
		CandidateListDto cd=new CandidateListDto();
		CandidatesDto[] candidatesDto=new CandidatesDto[10];
		IdentityResponceDto identityResponceDto=new IdentityResponceDto();
		for(int i=0;i<10;i++) {
			candidatesDto[i]=new CandidatesDto();
		candidatesDto[i].setReferenceId(i+"0bd41f8-31b2-46ac-ac9c-3534fc1b220e");
		candidatesDto[i].setScaledScore(i+10+"");
		}
		cd.setCount("10");
		cd.setCandidates(candidatesDto);
		identityResponceDto.setCandidateList(cd);
		identityResponceDto.setId("identity");
		identityResponceDto.setRequestId("80bd41f8-31b2-46ac-ac9c-3534fc1b220e");
		identityResponceDto.setReturnValue("1");
		identityResponceDto.setTimestamp("1539777717");
		return identityResponceDto;
		
	}
}
