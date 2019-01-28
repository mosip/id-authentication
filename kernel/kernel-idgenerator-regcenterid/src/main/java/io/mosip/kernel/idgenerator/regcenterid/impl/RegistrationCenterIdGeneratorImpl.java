package io.mosip.kernel.idgenerator.regcenterid.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.transaction.Transactional;

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
@Transactional
public class RegistrationCenterIdGeneratorImpl implements RegistrationCenterIdGenerator<String> {

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
	public String generateRegistrationCenterId() {
		int generatedRCID;

		final int initialValue = MathUtils.getPow(RegistrationCenterIdConstant.ID_BASE.getValue(),
				registrationCenterIdLength - 1);

		RegistrationCenterId registrationCenterId = null;

		try {
			registrationCenterId = registrationCenterIdRepository.findLastRCID();
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new RegistrationCenterIdServiceException(
					RegistrationCenterIdConstant.REG_CEN_ID_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterIdConstant.REG_CEN_ID_FETCH_EXCEPTION.getErrorMessage(),
					dataAccessLayerException.getCause());
		}
		if (registrationCenterId == null) {
			registrationCenterId = new RegistrationCenterId();
			registrationCenterId.setRcid(initialValue);
			generatedRCID = initialValue;
			registrationCenterId.setCreatedBy("default@user");
			registrationCenterId.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
			registrationCenterId.setUpdatedBy("default@user");
			registrationCenterId.setUpdatedDateTime(null);
			registrationCenterIdRepository.save(registrationCenterId);
		} else {
			try {
				registrationCenterIdRepository.updateRCID(registrationCenterId.getRcid() + 1,
						registrationCenterId.getRcid(), LocalDateTime.now(ZoneId.of("UTC")));
				generatedRCID = registrationCenterId.getRcid() + 1;
			} catch (DataAccessLayerException e) {
				throw new RegistrationCenterIdServiceException(
						RegistrationCenterIdConstant.REG_CEN_ID_INSERT_EXCEPTION.getErrorCode(),
						RegistrationCenterIdConstant.REG_CEN_ID_INSERT_EXCEPTION.getErrorMessage(), e);
			}
		}
		return String.valueOf(generatedRCID);
	}
}
