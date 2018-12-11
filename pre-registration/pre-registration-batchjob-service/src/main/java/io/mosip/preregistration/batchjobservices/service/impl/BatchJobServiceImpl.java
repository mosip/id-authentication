package io.mosip.preregistration.batchjobservices.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.preregistration.batchjobservices.dto.ResponseDto;
import io.mosip.preregistration.batchjobservices.entity.Applicant_demographic;
import io.mosip.preregistration.batchjobservices.entity.Processed_Prereg_List;
import io.mosip.preregistration.batchjobservices.repository.PreRegistrationDemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.PreRegistrationProcessedPreIdRepository;
import io.mosip.preregistration.batchjobservices.service.BatchJobService;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * @author M1043008
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
	
	/** The Constant isNew. */
	private static final boolean IS_NEW=true;

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

		List<Processed_Prereg_List> preRegList = new ArrayList<>();

		preRegList = preRegListRepo.findByisNew(IS_NEW);

		if (!preRegList.isEmpty()) {
			preRegList.forEach(iterate -> {
				String status = iterate.getStatusCode();
				String preRegId = iterate.getPrereg_id();

				try {
					Applicant_demographic applicant_demographic = preRegistrationDemographicRepository
							.findBypreRegistrationId(preRegId);

					applicant_demographic.setStatusCode(status);

					preRegistrationDemographicRepository.save(applicant_demographic);

					iterate.setNew(false);

					LOGGER.info(LOGDISPLAY, "Update the status successfully into Applicant demographic table");

				} catch (TablenotAccessibleException e) {

					LOGGER.error(LOGDISPLAY, APPLICANT_DEMOGRAPHIC_STATUS_TABLE_NOT_ACCESSIBLE, e);
				}
			});

		} else {
			LOGGER.info("There are currently no Pre-Registration-Ids to be moved");
		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse("Demographic status to consumed updated successfully");
		return response;

	}
	
}
