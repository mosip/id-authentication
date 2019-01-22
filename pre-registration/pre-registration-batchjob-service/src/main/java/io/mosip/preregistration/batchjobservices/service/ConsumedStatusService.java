package io.mosip.preregistration.batchjobservices.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjobservices.code.ErrorCodes;
import io.mosip.preregistration.batchjobservices.code.ErrorMessages;
import io.mosip.preregistration.batchjobservices.entity.ApplicantDemographic;
import io.mosip.preregistration.batchjobservices.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjobservices.exceptions.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjobservices.repository.DemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.ProcessedPreIdRepository;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */

@Component
public class ConsumedStatusService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumedStatusService.class);

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The Constant Status comments. */
	private static final String STATUS_COMMENTS = "Processed by registration processor";

	/** The Constant Status comments. */
	private static final String NEW_STATUS_COMMENTS = "Application consumed";
	/**
	 * The PreRegistration Processed PreId Repository.
	 */
	@Autowired
	@Qualifier("processedPreIdRepository")
	private ProcessedPreIdRepository preRegListRepo;

	@Autowired
	private BatchServiceDAO batchServiceDAO;

	/**
	 * The PreRegistration applicant Demographic repository.
	 */
	@Autowired
	@Qualifier("demographicRepository")
	private DemographicRepository demographicRepository;

	public MainResponseDTO<String> demographicConsumedStatus() {

		MainResponseDTO<String> response = new MainResponseDTO<>();

		List<ProcessedPreRegEntity> preRegList = new ArrayList<>();
		try {
			preRegList = batchServiceDAO.getAllConsumedPreIds(STATUS_COMMENTS);

			preRegList.forEach(iterate -> {
				String status = iterate.getStatusCode();
				String preRegId = iterate.getPreRegistrationId();

				ApplicantDemographic demographicEntity = batchServiceDAO.getApplicantDemographicDetails(preRegId);
				demographicEntity.setStatusCode(status);
				demographicRepository.save(demographicEntity);
				iterate.setStatusComments(NEW_STATUS_COMMENTS);

				LOGGER.info(LOGDISPLAY, "Update the status successfully into Applicant demographic table");

			});

		} catch (Exception e) {
			 new BatchServiceExceptionCatcher().handle(e);
		}
		response.setResTime(getCurrentResponseTime());
		response.setStatus(true);
		response.setResponse("Demographic status to consumed updated successfully");
		return response;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}

}
