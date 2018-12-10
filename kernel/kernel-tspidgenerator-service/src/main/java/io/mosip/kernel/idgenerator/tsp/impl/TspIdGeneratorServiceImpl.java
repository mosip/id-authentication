package io.mosip.kernel.idgenerator.tsp.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.idgenerator.tsp.constant.TspIdExceptionConstant;
import io.mosip.kernel.idgenerator.tsp.constant.TspIdPropertyConstant;
import io.mosip.kernel.idgenerator.tsp.dto.TspResponseDTO;
import io.mosip.kernel.idgenerator.tsp.entity.Tsp;
import io.mosip.kernel.idgenerator.tsp.exception.TspIdServiceException;
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
	 * Length of TspId.
	 */
	@Value("${mosip.kernel.tsp.length}")
	private int tspIdLength;

	/**
	 * The reference to TspRepository.
	 */
	@Autowired
	TspRepository tspRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idgenerator.spi.TspIdGenerator#generateId()
	 */
	@Override
	public TspResponseDTO generateId() {

		final long initialValue = MathUtils.getPow(Integer.parseInt(TspIdPropertyConstant.ID_BASE.getProperty()),
				tspIdLength - 1);

		Tsp entity = null;
		
		try {

			entity = tspRepository.findMaxTspId();

		} catch (DataAccessLayerException e) {
			throw new TspIdServiceException(TspIdExceptionConstant.TSPID_FETCH_EXCEPTION.getErrorCode(),
					TspIdExceptionConstant.TSPID_FETCH_EXCEPTION.getErrorMessage(), e);
		}

		if (entity == null) {
			entity = new Tsp();
			entity.setTspId(initialValue);
			entity.setCreatedBy("admin");
			LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));
			entity.setCreatedDateTime(time);

		} else {
			long lastGeneratedId = entity.getTspId();
			entity = new Tsp();
			entity.setTspId(lastGeneratedId + 1);
			entity.setCreatedBy("admin");
			LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));
			entity.setCreatedDateTime(time);

		}
		try {

			tspRepository.save(entity);

		} catch (DataAccessLayerException e) {
			throw new TspIdServiceException(TspIdExceptionConstant.TSPID_INSERTION_EXCEPTION.getErrorCode(),
					TspIdExceptionConstant.TSPID_INSERTION_EXCEPTION.getErrorMessage(), e);
		}

		TspResponseDTO tspDto = new TspResponseDTO();
		tspDto.setTspId(entity.getTspId());

		return tspDto;

	}

}
