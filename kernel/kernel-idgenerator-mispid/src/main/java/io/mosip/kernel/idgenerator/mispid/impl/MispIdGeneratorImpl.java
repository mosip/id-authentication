package io.mosip.kernel.idgenerator.mispid.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.MispIdGenerator;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.idgenerator.mispid.constant.MispIdExceptionConstant;
import io.mosip.kernel.idgenerator.mispid.constant.MispIdPropertyConstant;
import io.mosip.kernel.idgenerator.mispid.entity.Misp;
import io.mosip.kernel.idgenerator.mispid.exception.MispIdException;
import io.mosip.kernel.idgenerator.mispid.repository.MispRepository;

/**
 * This service class contains methods for generating MISPID.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Component
public class MispIdGeneratorImpl implements MispIdGenerator<String> {

	/**
	 * Length of MispId.
	 */
	@Value("${mosip.kernel.mispid.length}")
	private int mispIdLength;
	/**
	 * The reference to MispRepository.
	 */
	@Autowired
	MispRepository mispRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idgenerator.spi.MispIdGenerator#generateId()
	 */
	@Override
	public String generateId() {

		int generatedId = 0;

		final int initialValue = MathUtils.getPow(Integer.parseInt(MispIdPropertyConstant.ID_BASE.getProperty()),
				mispIdLength - 1);

		Misp entity = null;

		try {

			entity = mispRepository.findLastMispId();

		} catch (DataAccessLayerException e) {
			throw new MispIdException(MispIdExceptionConstant.MISPID_FETCH_EXCEPTION.getErrorCode(),
					MispIdExceptionConstant.MISPID_FETCH_EXCEPTION.getErrorMessage(), e);
		}

		try {
			if (entity != null) {
				generatedId = entity.getMispId() + 1;
				Misp mispId = new Misp();
				mispId.setMispId(generatedId);
				mispId.setCreatedBy("SYSTEM");
				mispId.setUpdatedBy("SYSTEM");
				mispId.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				mispId.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				mispRepository.create(mispId);

			} else {
				entity = new Misp();
				entity.setMispId(initialValue);
				entity.setCreatedBy("SYSTEM");
				entity.setUpdatedBy("SYSTEM");
				LocalDateTime createdTime = LocalDateTime.now(ZoneId.of("UTC"));
				entity.setCreatedDateTime(createdTime);
				entity.setUpdatedDateTime(null);
				generatedId = initialValue;
				mispRepository.create(entity);
			}

		} catch (DataAccessLayerException e) {
			if (e.getCause().getClass() == EntityExistsException.class) {
				generateId();
			} else {
				throw new MispIdException(MispIdExceptionConstant.MISPID_INSERTION_EXCEPTION.getErrorCode(),
						MispIdExceptionConstant.MISPID_INSERTION_EXCEPTION.getErrorMessage(), e);
			}
		}

		return String.valueOf(generatedId);

	}

}
