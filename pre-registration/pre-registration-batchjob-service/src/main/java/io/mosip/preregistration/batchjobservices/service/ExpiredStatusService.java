package io.mosip.preregistration.batchjobservices.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjobservices.entity.ApplicantDemographic;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.exceptions.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjobservices.repository.DemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.RegAppointmentRepository;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class ExpiredStatusService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumedStatusService.class);

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	@Qualifier("regAppointmentRepository")
	private RegAppointmentRepository regAppointmentRepository;

	@Autowired
	@Qualifier("demographicRepository")
	private DemographicRepository demographicRepository;

	@Autowired
	private BatchServiceDAO batchServiceDAO;

	/**
	 * @return Response dto
	 */
	public MainResponseDTO<String> bookedPreIds() {

		LocalDate currentDate = LocalDate.now();
		MainResponseDTO<String> response = new MainResponseDTO<>();
		List<RegistrationBookingEntity> bookedPreIdList = new ArrayList<>();

		try {
			bookedPreIdList = batchServiceDAO.getAllOldDateBooking(currentDate);

			bookedPreIdList.forEach(iterate -> {
				String status = iterate.getStatusCode();
				String preRegId = iterate.getBookingPK().getPreregistrationId();
				if (status.equals(StatusCodes.BOOKED.getCode()) || status.equals(StatusCodes.CANCELED.getCode())) {

					RegistrationBookingEntity entity = batchServiceDAO.gerPreRegId(preRegId);
					ApplicantDemographic demographicEntity = batchServiceDAO.getApplicantDemographicDetails(preRegId);
					entity.setStatusCode(StatusCodes.EXPIRED.getCode());
					demographicEntity.setStatusCode(StatusCodes.EXPIRED.getCode());
					regAppointmentRepository.save(entity);
					demographicRepository.save(demographicEntity);

					LOGGER.info(LOGDISPLAY,
							"Update the status successfully into Registration Appointment table and Demographic table");

				} else {
					LOGGER.info("The status of the PreId is already expired");
				}
			});
			
		} catch (Exception e) {
			new BatchServiceExceptionCatcher().handle(e);
		}
		response.setResTime(getCurrentResponseTime());
		response.setStatus(true);
		response.setResponse("Registration appointment status updated to expired successfully");
		return response;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}

}
