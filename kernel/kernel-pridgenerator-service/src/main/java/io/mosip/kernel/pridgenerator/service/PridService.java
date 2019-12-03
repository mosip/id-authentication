package io.mosip.kernel.pridgenerator.service;

import io.mosip.kernel.pridgenerator.dto.PridFetchResponseDto;
import io.mosip.kernel.pridgenerator.entity.PridEntity;

public interface PridService {
	
	PridFetchResponseDto fetchPrid();
	
	long fetchPridCount(String status);

//	void expireAndRenew();

	boolean savePRID(PridEntity vid);

}
