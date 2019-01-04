package io.mosip.preregistration.batchjobservices.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.batchjobservices.code.ErrorCode;
import io.mosip.preregistration.batchjobservices.code.ErrorMessage;
import io.mosip.preregistration.batchjobservices.dto.ResponseDto;
import io.mosip.preregistration.batchjobservices.entity.ApplicantDemographic;
import io.mosip.preregistration.batchjobservices.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjobservices.exceptions.NoPreIdAvailableException;
import io.mosip.preregistration.batchjobservices.repository.PreRegistrationDemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.PreRegistrationProcessedPreIdRepository;
import io.mosip.preregistration.batchjobservices.service.BatchJobService;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */

@Component
public class BatchJobServiceImpl implements BatchJobService {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobServiceImpl.class);
	
	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";
	
	/** The Constant ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE. */
	private static final String APPLICANT_DEMOGRAPHIC_STATUS_TABLE_NOT_ACCESSIBLE = "The applicant demographic table is not accessible";
	
	/** The Constant Status. */
	private static final String STATUS = "Consumed";
	
	/** The Constant Status comments. */
	private static final String STATUS_COMMENTS = "Processed by registration processor";

	/** The Constant Status comments. */
	private static final String NEW_STATUS_COMMENTS = "Application consumed";
	/**
	 * The PreRegistration Processed PreId Repository.
	 */
	@Autowired
	@Qualifier("preRegProcessedRepository")
	private PreRegistrationProcessedPreIdRepository preRegListRepo;

	/**
	 * The PreRegistration applicant Demographic repository.
	 */
	@Autowired
	@Qualifier("preRegistrationDemographicRepository")
	private PreRegistrationDemographicRepository preRegistrationDemographicRepository;

	
	/* (non-Javadoc)
	 * @see io.mosip.preregistration.batchjobservices.service.BatchJobService#demographicConsumedStatus()
	 */
	@Override
	public ResponseDto<String> demographicConsumedStatus() {
		
		ResponseDto<String> response = new ResponseDto<>();

		List<ProcessedPreRegEntity> preRegList = new ArrayList<>();

		preRegList = preRegListRepo.findBystatusComments(STATUS_COMMENTS);

		if (!preRegList.isEmpty()) {
			preRegList.forEach(iterate -> {
				String status = iterate.getStatusCode();
				String preRegId = iterate.getPreRegistrationId();

				try {
					ApplicantDemographic applicant_demographic = preRegistrationDemographicRepository
							.findBypreRegistrationId(preRegId);

					applicant_demographic.setStatusCode(status);

					preRegistrationDemographicRepository.save(applicant_demographic);

					iterate.setStatusComments(NEW_STATUS_COMMENTS);

					LOGGER.info(LOGDISPLAY, "Update the status successfully into Applicant demographic table");

				} catch (DataAccessLayerException e) {
					throw new TablenotAccessibleException(ErrorCode.PRG_PAM_BAT_004.toString(),
							ErrorMessage.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
				}
			});

		} else {

			LOGGER.info("There are currently no Pre-Registration-Ids to update status to consumed");
			throw new NoPreIdAvailableException(ErrorCode.PRG_PAM_BAT_001.name(),ErrorMessage.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_CONSUMED_STATUS.name());
		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse("Demographic status to consumed updated successfully");
		return response;

	}
	
}
