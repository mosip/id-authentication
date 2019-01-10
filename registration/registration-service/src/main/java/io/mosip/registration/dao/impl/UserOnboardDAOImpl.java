package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_LOGGER_TITLE;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_USER_MACHINE_MAP_CENTER_MACHINE_CODE;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_USER_MACHINE_MAP_MACHINE_MASTER_CODE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.CenterMachine;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserBiometricId;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.UserBiometricRepository;

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
	 * centerMachineRepository instance creation using autowired annotation
	 */
	@Autowired
	private CenterMachineRepository centerMachineRepository;

	/**
	 * machineMasterRepository instance creation using autowired annotation
	 */
	@Autowired
	private MachineMasterRepository machineMasterRepository;
	
	/**
	 * machineMapping instance creation using autowired annotation
	 */
	@Autowired
	private MachineMappingDAO machineMappingDAO;
	
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

			List<FingerprintDetailsDTO> fingerPrint = biometricDTO.getOperatorBiometricDTO().getFingerprintDetailsDTO()
					.stream().flatMap(o -> o.getSegmentedFingerprints().stream()).collect(Collectors.toList());

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

			biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO().forEach(iries -> {

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

			biometricId.setBioAttributeCode(RegistrationConstants.APPLICANT_PHOTOGRAPH_NAME);
			biometricId.setBioTypeCode(RegistrationConstants.APPLICANT_PHOTOGRAPH_NAME);
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

			// find user
			UserMachineMapping user = new UserMachineMapping();
			UserMachineMappingID userID = new UserMachineMappingID();
			userID.setUserID(SessionContext.getInstance().getUserContext().getUserId());
			userID.setCentreID(SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
					.getRegistrationCenterId());
			userID.setMachineID(SessionContext.getInstance().getMapObject().get(RegistrationConstants.USER_STATION_ID).toString());

			user.setUserMachineMappingId(userID);
			user.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
			user.setCrDtime(new Timestamp(System.currentTimeMillis()));
			user.setUpdBy(SessionContext.getInstance().getUserContext().getUserId());
			user.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
			user.setIsActive(true);

			machineMappingDAO.save(user);

			response = RegistrationConstants.USER_ON_BOARDING_SUCCESS_RESPONSE;

		} catch (RuntimeException runtimeException) {
			
			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, runtimeException.getMessage());
			response = RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE;
			throw new RegBaseUncheckedException(RegistrationConstants.USER_ON_BOARDING_EXCEPTION + response,
					runtimeException.getMessage());
		}

		return response;
	}

	/*
	 * (non-Javadoc) Getting station id based on mac address
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#getStationID(java.lang.String)
	 */
	@Override
	public String getStationID(String macAddress) throws RegBaseCheckedException {

		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getStationID() macAddress --> " + macAddress);

		try {
			MachineMaster machineMaster = machineMasterRepository.findByMacAddress(macAddress);

			if (machineMaster != null && machineMaster.getId() != null) {
				return machineMaster.getId();
			} else {
				throw new RegBaseCheckedException(REG_USER_MACHINE_MAP_MACHINE_MASTER_CODE.getErrorCode(),
						REG_USER_MACHINE_MAP_MACHINE_MASTER_CODE.getErrorMessage());
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_STATIONID_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}

	/*
	 * (non-Javadoc) Getting center id based on station id
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#getCenterID(java.lang.String)
	 */
	@Override
	public String getCenterID(String stationID) throws RegBaseCheckedException {

		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getCenterID() stationID --> " + stationID);

		try {
			CenterMachine centerMachine = centerMachineRepository.findByCenterMachineIdId(stationID);
			if (centerMachine != null && centerMachine.getCenterMachineId().getCentreId() != null) {
				return centerMachine.getCenterMachineId().getCentreId();
			} else {
				throw new RegBaseCheckedException(REG_USER_MACHINE_MAP_CENTER_MACHINE_CODE.getErrorCode(),
						REG_USER_MACHINE_MAP_CENTER_MACHINE_CODE.getErrorMessage());
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_CENTERID_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}

}
