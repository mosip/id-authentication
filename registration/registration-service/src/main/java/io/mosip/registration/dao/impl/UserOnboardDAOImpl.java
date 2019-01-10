package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.derby.iapi.util.ByteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserBiometricId;
import io.mosip.registration.entity.mastersync.MasterApplication;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.UserBiometricRepository;
import io.mosip.registration.util.mastersync.MetaDataUtils;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class UserOnboardDAOImpl implements UserOnboardDAO {

	@Autowired
	private UserBiometricRepository userBiometricRepository;
	
	/**
	 * logger for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnboardDAOImpl.class);

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
						
			List<UserBiometric> bioMetricsList = new ArrayList<>(); 
			
			List<FingerprintDetailsDTO> fingerPrint=biometricDTO.getOperatorBiometricDTO()
					.getFingerprintDetailsDTO().stream().flatMap(o -> o.getSegmentedFingerprints().stream())
					.collect(Collectors.toList());
			
			fingerPrint.forEach(fingerPrintData -> {

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
				
				bioMetricsList.add(bioMetrics);

			});
			
			biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO().forEach(iries->{
				
				UserBiometric bioMetrics = new UserBiometric();
				UserBiometricId biometricId = new UserBiometricId();

				biometricId.setBioAttributeCode(iries.getIrisImageName());
				biometricId.setBioTypeCode(iries.getIrisType());
				biometricId.setUsrId(SessionContext.getInstance().getUserContext().getUserId());
				bioMetrics.setBioIsoImage(iries.getIris());
				bioMetrics.setNumberOfRetry(iries.getNumOfIrisRetry());
				bioMetrics.setUserBiometricId(biometricId);
				Double qualitySocre = iries.getQualityScore();
				bioMetrics.setQualityScore(qualitySocre.intValue());
				bioMetrics.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
				bioMetrics.setCrDtime(new Timestamp(System.currentTimeMillis()));
				bioMetrics.setIsActive(true);
				
				bioMetricsList.add(bioMetrics);
				
			});
			
			biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO();

			UserBiometric bioMetrics = new UserBiometric();
			UserBiometricId biometricId = new UserBiometricId();

			biometricId.setBioAttributeCode("photo");
			biometricId.setBioTypeCode("photo");
			biometricId.setUsrId(SessionContext.getInstance().getUserContext().getUserId());
			bioMetrics.setBioIsoImage(biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO().getFace());
			bioMetrics.setNumberOfRetry(biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO().getNumOfRetries());
			bioMetrics.setUserBiometricId(biometricId);
			Double qualitySocre = biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO().getQualityScore();
			bioMetrics.setQualityScore(qualitySocre.intValue());
			bioMetrics.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
			bioMetrics.setCrDtime(new Timestamp(System.currentTimeMillis()));
			bioMetrics.setIsActive(true);
			
			bioMetricsList.add(bioMetrics);
			
			userBiometricRepository.saveAll(bioMetricsList);
			

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
