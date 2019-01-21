package io.mosip.kernel.idgenerator.regcenterid.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.RegistrationCenterIdGenerator;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.idgenerator.regcenterid.constant.RegistrationCenterIdConstant;
import io.mosip.kernel.idgenerator.regcenterid.entity.RegistrationCenterId;
import io.mosip.kernel.idgenerator.regcenterid.exception.RegistrationCenterIdServiceException;
import io.mosip.kernel.idgenerator.regcenterid.repository.RegistrationCenterIdRepository;

/**
 * Implementation class for {@link RegistrationCenterIdGenerator}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Component
public class RegistrationCenterIdGeneratorImpl implements RegistrationCenterIdGenerator<Integer> {

	/**
	 * The length of registration center id.
	 */
	@Value("${mosip.kernel.registrationcenterid.length}")
	private int registrationCenterIdLength;

	/**
	 * Autowired reference for {@link RegistrationCenterIdRepository}.
	 */
	@Autowired
	RegistrationCenterIdRepository registrationCenterIdRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idgenerator.spi.RegistrationCenterIdGenerator#
	 * generateRegistrationCenterId()
	 */
	@Override
	public Integer generateRegistrationCenterId() {
		final int initialValue = MathUtils.getPow(RegistrationCenterIdConstant.ID_BASE.getValue(),
				registrationCenterIdLength - 1);
		RegistrationCenterId registrationCenterId = null;
		try {
			registrationCenterId = registrationCenterIdRepository.findMaxRegistrationCenterId();
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new RegistrationCenterIdServiceException(
					RegistrationCenterIdConstant.REG_CEN_ID_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterIdConstant.REG_CEN_ID_FETCH_EXCEPTION.getErrorMessage(),
					dataAccessLayerException.getCause());
		}
		if (registrationCenterId == null) {
			registrationCenterId = new RegistrationCenterId();
			registrationCenterId.setRcid(initialValue);
		} else {
			int lastGeneratedId = registrationCenterId.getRcid();
			registrationCenterId = new RegistrationCenterId();
			registrationCenterId.setRcid(lastGeneratedId + 1);
		}
		try {
			registrationCenterIdRepository.save(registrationCenterId);
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new RegistrationCenterIdServiceException(
					RegistrationCenterIdConstant.REG_CEN_ID_INSERT_EXCEPTION.getErrorCode(),
					RegistrationCenterIdConstant.REG_CEN_ID_INSERT_EXCEPTION.getErrorMessage(),
					dataAccessLayerException.getCause());
		}
		return registrationCenterId.getRcid();
	}
}
