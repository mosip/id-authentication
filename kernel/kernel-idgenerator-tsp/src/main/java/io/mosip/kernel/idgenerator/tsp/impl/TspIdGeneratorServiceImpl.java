package io.mosip.kernel.idgenerator.tsp.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.idgenerator.tsp.constant.TspIdPropertyConstant;
import io.mosip.kernel.idgenerator.tsp.dto.TspResponseDTO;
import io.mosip.kernel.idgenerator.tsp.entity.Tsp;
import io.mosip.kernel.idgenerator.tsp.repository.TspRepository;

/**
 * This service class contains methods for generating TSPID.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class TspIdGeneratorServiceImpl implements TspIdGenerator<TspResponseDTO> {

	/**
	 * The reference to TspRepository.
	 */
	@Autowired
	TspRepository tspRepository;

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.idgenerator.spi.TspIdGenerator#generateId()
	 */
	@Override
	public TspResponseDTO generateId() {

		final int initialValue = Integer.parseInt(TspIdPropertyConstant.ID_START_VALUE.getProperty());

		Tsp entity = tspRepository.findMaxTspId();

		if (entity == null) {
			entity = new Tsp();
			entity.setId(1);
			entity.setTspId(initialValue);
			LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));
			entity.setCreatedDateTime(time);

		} else {

			entity.setTspId(entity.getTspId() + 1);
			LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));
			entity.setCreatedDateTime(time);

		}
		tspRepository.save(entity);

		TspResponseDTO tspDto = new TspResponseDTO();
		tspDto.setTspId(entity.getTspId());

		return tspDto;

	}

}
