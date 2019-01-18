package io.mosip.preregistration.core.stateUtil;

import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.AppointmentBookException;
import io.mosip.preregistration.core.exception.AppointmentCancelException;
import io.mosip.preregistration.core.exception.AppointmentReBookException;

public class StateManager {

	public static boolean checkIsValidStatus(String status, String serviceType) {

		switch (serviceType) {
		case "book":
			if (status.equals(StatusCodes.PENDING_APPOINTMENT.getCode())||status.equals(StatusCodes.BOOKED.getCode())) {
				return true;
			} else {
				throw new AppointmentBookException(ErrorCodes.PRG_CORE_REQ_005.getCode(),
						ErrorMessages.APPOINTMENT_CANNOT_BE_BOOKED.getMessage()+"_FOR_"+status.toUpperCase());
			}
		case "cancel":
			if (status.equals(StatusCodes.BOOKED.getCode())
					|| status.equals(StatusCodes.EXPIRED.getCode())) {
				return true;
			} else {
				throw new AppointmentCancelException(ErrorCodes.PRG_CORE_REQ_006.getCode(),
						ErrorMessages.APPONIMENT_CANNOT_BE_CANCELED.getMessage()+"_FOR_"+status.toUpperCase());
			}
		case "rebook":
			if (status.equals(StatusCodes.BOOKED.getCode())) {
				return true;
			} else {
				throw new AppointmentReBookException(ErrorCodes.PRG_CORE_REQ_007.getCode(),
						ErrorMessages.APPONIMENT_CANNOT_BE_REBOOK.getMessage()+"_FOR_"+status.toUpperCase());
			}
		default:
			return false;
		}
	}
}
