package io.mosip.preregistration.core.stateUtil;

import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.core.exception.AppointmentReBookingFailedException;

public class StateManager {

	public static boolean checkIsValidStatus(String status, String serviceType) {

		switch (serviceType) {
		case "book":
			if ((status.equals(StatusCodes.PENDING_APPOINTMENT.getCode())
					|| status.equals(StatusCodes.EXPIRED.getCode()))) {
				return true;
			} else {
				throw new AppointmentBookingFailedException();
			}

		case "cancel":
			if (status.equals(StatusCodes.BOOKED.getCode())
					|| status.equals(StatusCodes.EXPIRED.getCode())) {
				return true;
			} else {
				throw new AppointmentBookingFailedException();
			}

		case "rebook":
			if (status.equals(StatusCodes.BOOKED.getCode())) {
				return true;
			} else {
				throw new AppointmentReBookingFailedException();
			}

		default:
			return false;
		}
	}

}
