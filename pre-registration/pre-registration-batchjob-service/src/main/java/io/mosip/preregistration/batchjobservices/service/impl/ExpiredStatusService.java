package io.mosip.preregistration.batchjobservices.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
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
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.exceptions.NoPreIdAvailableException;
import io.mosip.preregistration.batchjobservices.repository.PreRegistartionExpiredStatusRepository;
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

	/** The Constant ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE. */
	private static final String REGISTRATION_APPOINTMENT_TABLE_NOT_ACCESSIBLE = "The resgistration appointment table is not accessible";

	/** The Constant old Status. */
	private static final String OLD_STATUS = "Booked";

	/** The Constant Status. */
	private static final String NEW_STATUS = "Expired";

	@Autowired
	@Qualifier("preRegistartionExpiredStatusRepository")
	private PreRegistartionExpiredStatusRepository expiredStatusRepository;

	/**
	 * @return Response dto
	 */
	public ResponseDto<String> bookedPreIds() {

		LocalDate currentDate = LocalDate.now();

		ResponseDto<String> response = new ResponseDto<>();

		List<RegistrationBookingEntity> bookedPreIdList = new ArrayList<>();
		bookedPreIdList = expiredStatusRepository.findByRegDateBefore(currentDate);

		if (!bookedPreIdList.isEmpty()) {

			bookedPreIdList.forEach(iterate -> {

				String status = iterate.getStatusCode();

				String preRegId = iterate.getBookingPK().getPreregistrationId();

				if (status.equalsIgnoreCase(OLD_STATUS)) {

					try {
						RegistrationBookingEntity entity = expiredStatusRepository.getPreRegId(preRegId);

						entity.setStatusCode(NEW_STATUS);

						expiredStatusRepository.save(entity);

						LOGGER.info(LOGDISPLAY, "Update the status successfully into Registration Appointment table");

					} catch (DataAccessLayerException e) {
						throw new TableNotAccessibleException(ErrorCode.PRG_PAM_BAT_004.toString(),
								ErrorMessage.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
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
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse("Registration appointment status updated to expired successfully");
		return response;
	}

}
