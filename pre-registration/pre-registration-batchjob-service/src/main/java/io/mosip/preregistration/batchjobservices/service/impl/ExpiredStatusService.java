package io.mosip.preregistration.batchjobservices.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjobservices.code.ErrorCode;
import io.mosip.preregistration.batchjobservices.code.ErrorMessage;
import io.mosip.preregistration.batchjobservices.code.StatusCodes;
import io.mosip.preregistration.batchjobservices.entity.ApplicantDemographic;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.exceptions.NoPreIdAvailableException;
import io.mosip.preregistration.batchjobservices.repository.PreRegistartionExpiredStatusRepository;
import io.mosip.preregistration.batchjobservices.repository.PreRegistrationDemographicRepository;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class ExpiredStatusService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobServiceImpl.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	@Qualifier("preRegistartionExpiredStatusRepository")
	private PreRegistartionExpiredStatusRepository expiredStatusRepository;

	@Autowired
	@Qualifier("preRegistrationDemographicRepository")
	private PreRegistrationDemographicRepository demographicRepository;

	/**
	 * @return Response dto
	 */
	public MainResponseDTO<String> bookedPreIds() {

		LocalDate currentDate = LocalDate.now();
		MainResponseDTO<String> response = new MainResponseDTO<>();
		List<RegistrationBookingEntity> bookedPreIdList = new ArrayList<>();
		
		try {
			bookedPreIdList = expiredStatusRepository.findByRegDateBefore(currentDate);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TableNotAccessibleException(ErrorCode.PRG_PAM_BAT_005.toString(),
					ErrorMessage.REG_APPOINTMENT_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		}
		if (!bookedPreIdList.isEmpty() && bookedPreIdList!=null) {
			bookedPreIdList.forEach(iterate -> {
				String status = iterate.getStatusCode();
				String preRegId = iterate.getBookingPK().getPreregistrationId();
				if (status.equals(StatusCodes.BOOKED.getCode()) || status.equals(StatusCodes.CANCELED.getCode())) {
					try {
						RegistrationBookingEntity entity = expiredStatusRepository.getPreRegId(preRegId);
						ApplicantDemographic demographicEntity = demographicRepository
								.findBypreRegistrationId(preRegId);
						if (entity != null && demographicEntity != null) {
							entity.setStatusCode(StatusCodes.EXPIRED.getCode());
							demographicEntity.setStatusCode(StatusCodes.EXPIRED.getCode());
							expiredStatusRepository.save(entity);
							demographicRepository.save(demographicEntity);

							LOGGER.info(LOGDISPLAY,
									"Update the status successfully into Registration Appointment table and Demographic table");
						} else {
							throw new NoPreIdAvailableException(ErrorCode.PRG_PAM_BAT_003.toString(),
									ErrorMessage.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_EXPIRED_STATUS.toString());
						}

					} catch (DataAccessLayerException e) {
						throw new TableNotAccessibleException(ErrorCode.PRG_PAM_BAT_004.toString(),
								ErrorMessage.DEMOGRAPHIC_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
					}

				} else {
					LOGGER.info("The status of the PreId is already expired");
				}
			});
		} else {

			LOGGER.info("There are currently no Pre-Registration-Ids which is expired");
			throw new NoPreIdAvailableException(ErrorCode.PRG_PAM_BAT_003.name(),
					ErrorMessage.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_EXPIRED_STATUS.name());
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
