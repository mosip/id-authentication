/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.exception.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.GenericUtil;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class ExpiredStatusService {

	/** The Constant LOGGER. */
	private Logger log = LoggerConfiguration.logConfig(ExpiredStatusService.class);

	
	
	@Value("${version}")
	String versionUrl;

	@Value("${mosip.preregistration.batchjob.service.expired.id}")
	String idUrl;

	@Autowired
	private BatchServiceDAO batchServiceDAO;

	/**
	 * @return Response dto
	 */
	
	public MainResponseDTO<String> expireAppointments() {

		LocalDate currentDate = LocalDate.now();
		MainResponseDTO<String> response = new MainResponseDTO<>();
		response.setId(idUrl);
		response.setVersion(versionUrl);
		List<RegistrationBookingEntity> bookedPreIdList = null;
		try {
			bookedPreIdList = batchServiceDAO.getAllOldDateBooking(currentDate);

			bookedPreIdList.forEach(iterate -> {
				String preRegId = iterate.getBookingPK().getPreregistrationId();
				DemographicEntity demographicEntity = batchServiceDAO.getApplicantDemographicDetails(preRegId);
				if (demographicEntity != null) {

					if (demographicEntity.getStatusCode().equals(StatusCodes.BOOKED.getCode())) {
						demographicEntity.setStatusCode(StatusCodes.EXPIRED.getCode());
						batchServiceDAO.updateApplicantDemographic(demographicEntity);
					}
					log.info("sessionId", "idType", "id", "Update the status successfully into Registration Appointment table and Demographic table for Pre-RegistrationId: "+preRegId);
				}
			});

		} catch (Exception e) {
			new BatchServiceExceptionCatcher().handle(e,response);
		}
		response.setResponsetime(GenericUtil.getCurrentResponseTime());
		response.setResponse("Registration appointment status updated to expired successfully");
		return response;
	}



}
