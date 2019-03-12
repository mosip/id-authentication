package io.mosip.kernel.idgenerator.tspid.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.idgenerator.tspid.constant.TspIdExceptionConstant;
import io.mosip.kernel.idgenerator.tspid.constant.TspIdPropertyConstant;
import io.mosip.kernel.idgenerator.tspid.entity.Tsp;
import io.mosip.kernel.idgenerator.tspid.exception.TspIdException;
import io.mosip.kernel.idgenerator.tspid.repository.TspRepository;

/**
 * This service class contains methods for generating TSPID.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
public class TspIdGeneratorImpl implements TspIdGenerator<String> {

	/**
	 * Length of TspId.
	 */
	@Value("${mosip.kernel.tspid.length}")
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
	public String generateId() {

		int generatedId = 0;

		final int initialValue = MathUtils.getPow(Integer.parseInt(TspIdPropertyConstant.ID_BASE.getProperty()),
				tspIdLength - 1);

		Tsp entity = null;

		try {

			entity = tspRepository.findLastTspId();

		} catch (DataAccessLayerException e) {
			throw new TspIdException(TspIdExceptionConstant.TSPID_FETCH_EXCEPTION.getErrorCode(),
					TspIdExceptionConstant.TSPID_FETCH_EXCEPTION.getErrorMessage(), e);
		}

		try {
			if (entity != null) {
				generatedId = entity.getTspId() + 1;
				Tsp tspId = new Tsp();
				tspId.setTspId(generatedId);
				tspId.setCreatedBy("default@user");
				tspId.setUpdatedBy("default@user");
				tspId.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				tspId.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				tspRepository.create(tspId);

			} else {
				entity = new Tsp();
				entity.setTspId(initialValue);
				entity.setCreatedBy("default@user");
				entity.setUpdatedBy("default@user");
				LocalDateTime createdTime = LocalDateTime.now(ZoneId.of("UTC"));
				entity.setCreatedDateTime(createdTime);
				entity.setUpdatedDateTime(null);
				generatedId = initialValue;
				tspRepository.create(entity);
			}

		} catch (DataAccessLayerException e) {
			if (e.getCause().getClass() == EntityExistsException.class) {
				generateId();
			} else {
				throw new TspIdException(TspIdExceptionConstant.TSPID_INSERTION_EXCEPTION.getErrorCode(),
						TspIdExceptionConstant.TSPID_INSERTION_EXCEPTION.getErrorMessage(), e);
			}
		}

		return String.valueOf(generatedId);

	}

}
