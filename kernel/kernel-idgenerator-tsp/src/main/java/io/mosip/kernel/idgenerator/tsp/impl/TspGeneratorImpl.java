package io.mosip.kernel.idgenerator.tsp.impl;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.idgenerator.tsp.dto.TspResponseDTO;

@Service
public class TspGeneratorImpl implements TspIdGenerator<TspResponseDTO>{

	@Override
	public TspResponseDTO generateId() {
		
		
		return null;
	}

	

}
