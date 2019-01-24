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
	@Qualifier("regAppointmentRepository")
	private RegAppointmentRepository regAppointmentRepository;

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
				throw new NoPreIdAvailableException(ErrorCodes.PRG_PAM_BAT_001.getCode(),
						ErrorMessages.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_CONSUMED_STATUS.getMessage());
			}

		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_BAT_004.getCode(),
					ErrorMessages.DEMOGRAPHIC_TABLE_NOT_ACCESSIBLE.getMessage());
		}
		return entity;
	}

	public List<ProcessedPreRegEntity> getAllConsumedPreIds(String statusComment) {
		List<ProcessedPreRegEntity> entityList = new ArrayList<>();
		try {
			entityList = processedPreIdRepository.findBystatusComments(statusComment);
			if (entityList.isEmpty() || entityList == null) {
				LOGGER.info("There are currently no Pre-Registration-Ids to update status to consumed");
				throw new NoPreIdAvailableException(ErrorCodes.PRG_PAM_BAT_001.getCode(),
						ErrorMessages.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_CONSUMED_STATUS.getMessage());
			}

		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_BAT_006.getCode(),
					ErrorMessages.Processed_Prereg_List_TABLE_NOT_ACCESSIBLE.getMessage());
		}
		return entityList;
	}

	public List<RegistrationBookingEntity> getAllOldDateBooking(LocalDate currentdate) {
		List<RegistrationBookingEntity> entityList = new ArrayList<>();
		try {
			entityList = regAppointmentRepository.findByRegDateBefore(currentdate);
			if (entityList.isEmpty() || entityList == null) {
				LOGGER.info("There are currently no Pre-Registration-Ids which is expired");
				throw new NoPreIdAvailableException(ErrorCodes.PRG_PAM_BAT_003.getCode(),
						ErrorMessages.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_EXPIRED_STATUS.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_BAT_005.getCode(),
					ErrorMessages.REG_APPOINTMENT_TABLE_NOT_ACCESSIBLE.getMessage());
		}
		return entityList;
	}

	public RegistrationBookingEntity getPreRegId(String preRegId) {
		RegistrationBookingEntity entity = null;
		try {
			entity = regAppointmentRepository.getPreRegId(preRegId);
			if (entity == null) {
				throw new NoPreIdAvailableException(ErrorCodes.PRG_PAM_BAT_003.getCode(),
						ErrorMessages.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_EXPIRED_STATUS.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_BAT_005.getCode(),
					ErrorMessages.REG_APPOINTMENT_TABLE_NOT_ACCESSIBLE.getMessage());
		}
		return entity;

	}
	
	public boolean updateApplicantDemographic(ApplicantDemographic applicantDemographic) {
		return demographicRepository.save(applicantDemographic)!=null;
	}
	
	public boolean updateProcessedList(ProcessedPreRegEntity entity) {
		 return processedPreIdRepository.save(entity)!=null;
	}
	
	public boolean updateBooking(RegistrationBookingEntity entity) {
		return regAppointmentRepository.save(entity)!=null;
	}
}
