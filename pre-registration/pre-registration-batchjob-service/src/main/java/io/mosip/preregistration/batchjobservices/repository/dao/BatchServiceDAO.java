/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.repository.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.batchjobservices.code.ErrorCodes;
import io.mosip.preregistration.batchjobservices.code.ErrorMessages;
import io.mosip.preregistration.batchjobservices.entity.ApplicantDemographic;
import io.mosip.preregistration.batchjobservices.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.exceptions.NoPreIdAvailableException;
import io.mosip.preregistration.batchjobservices.repository.DemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.ProcessedPreIdRepository;
import io.mosip.preregistration.batchjobservices.repository.RegAppointmentRepository;
import io.mosip.preregistration.batchjobservices.service.ConsumedStatusService;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * This repository class is used to implement the JPA methods for Batch Service
 * application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class BatchServiceDAO {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumedStatusService.class);

	/**
	 * Autowired reference for {@link #demographicRepository}
	 */
	@Autowired
	@Qualifier("demographicRepository")
	private DemographicRepository demographicRepository;

	/**
	 * Autowired reference for {@link #appointmentRepository}
	 */
	@Autowired
	@Qualifier("appointmentRepository")
	private RegAppointmentRepository appointmentRepository;

	/**
	 * Autowired reference for {@link #processedPreIdRepository}
	 */
	@Autowired
	@Qualifier("processedPreIdRepository")
	private ProcessedPreIdRepository processedPreIdRepository;

	public ApplicantDemographic getApplicantDemographicDetails(String preRegId) {

		ApplicantDemographic entity = null;
		try {
			entity = demographicRepository.findBypreRegistrationId(preRegId);
			if (entity == null) {
				throw new NoPreIdAvailableException(ErrorCodes.PRG_PAM_BAT_001.toString(),
						ErrorMessages.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_CONSUMED_STATUS.toString());
			}

		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_BAT_004.toString(),
					ErrorMessages.DEMOGRAPHIC_TABLE_NOT_ACCESSIBLE.toString());
		}
		return entity;
	}

	public List<ProcessedPreRegEntity> getAllConsumedPreIds(String statusComment) {
		List<ProcessedPreRegEntity> entityList = new ArrayList<ProcessedPreRegEntity>();
		try {
			entityList = processedPreIdRepository.findBystatusComments(statusComment);
			if (entityList.isEmpty() || entityList == null) {
				LOGGER.info("There are currently no Pre-Registration-Ids to update status to consumed");
				throw new NoPreIdAvailableException(ErrorCodes.PRG_PAM_BAT_001.name(),
						ErrorMessages.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_CONSUMED_STATUS.name());
			}

		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_BAT_006.toString(),
					ErrorMessages.Processed_Prereg_List_TABLE_NOT_ACCESSIBLE.toString());
		}
		return entityList;
	}

	public List<RegistrationBookingEntity> getAllOldDateBooking(LocalDate currentdate) {
		List<RegistrationBookingEntity> entityList = new ArrayList<>();
		try {
			entityList = appointmentRepository.findByRegDateBefore(currentdate);
			if (entityList.isEmpty() || entityList == null) {
				LOGGER.info("There are currently no Pre-Registration-Ids which is expired");
				throw new NoPreIdAvailableException(ErrorCodes.PRG_PAM_BAT_003.name(),
						ErrorMessages.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_EXPIRED_STATUS.name());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_BAT_005.toString(),
					ErrorMessages.REG_APPOINTMENT_TABLE_NOT_ACCESSIBLE.toString());
		}
		return entityList;
	}

	public RegistrationBookingEntity gerPreRegId(String preRegId) {
		RegistrationBookingEntity entity = null;
		try {
			entity = appointmentRepository.getPreRegId(preRegId);
			if (entity == null) {
				throw new NoPreIdAvailableException(ErrorCodes.PRG_PAM_BAT_003.toString(),
						ErrorMessages.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_EXPIRED_STATUS.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_BAT_005.toString(),
					ErrorMessages.REG_APPOINTMENT_TABLE_NOT_ACCESSIBLE.toString());
		}
		return entity;

	}
}
