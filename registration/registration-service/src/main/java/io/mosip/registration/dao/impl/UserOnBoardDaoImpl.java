package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.derby.iapi.util.ByteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.UserOnBoardDao;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserBiometricId;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.UserBiometricRepository;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class UserOnBoardDaoImpl implements UserOnBoardDao {

	@Autowired
	private UserBiometricRepository userBiometricRepository;
	
	/**
	 * logger for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnBoardDaoImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.UserOnBoardDao#insert(io.mosip.registration.dto.
	 * biometric.BiometricDTO)
	 */
	@Override
	public String insert(BiometricDTO biometricDTO) {

		String response = RegistrationConstants.EMPTY;

		try {
			
			List<FingerprintDetailsDTO> fingerPrints = biometricDTO.getOperatorBiometricDTO()
					.getFingerprintDetailsDTO();

			fingerPrints.forEach(fingerPrintData -> {

				UserBiometric bioMetrics = new UserBiometric();
				UserBiometricId biometricId = new UserBiometricId();

				biometricId.setBioAttributeCode(fingerPrintData.getFingerprintImageName());
				biometricId.setBioTypeCode(fingerPrintData.getFingerType());
				biometricId.setUsrId(SessionContext.getInstance().getUserContext().getUserId());
				bioMetrics.setBioIsoImage(fingerPrintData.getFingerPrintISOImage());
				bioMetrics.setNumberOfRetry(fingerPrintData.getNumRetry());
				bioMetrics.setUserBiometricId(biometricId);
				Double qualitySocre = fingerPrintData.getQualityScore();
				bioMetrics.setQualityScore(qualitySocre.intValue());
				bioMetrics.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
				bioMetrics.setCrDtime(new Timestamp(System.currentTimeMillis()));
				bioMetrics.setIsActive(true);

				userBiometricRepository.save(bioMetrics);

			});

			response=RegistrationConstants.USER_ON_BOARDING_SUCCESS_RESPONSE;
			
		} catch (RuntimeException runtimeException) {
			
			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, runtimeException.getMessage());
			response = RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE;
			throw new RegBaseUncheckedException(RegistrationConstants.USER_ON_BOARDING_EXCEPTION + response,
					runtimeException.getMessage());
		}

		return response;
	}

}
