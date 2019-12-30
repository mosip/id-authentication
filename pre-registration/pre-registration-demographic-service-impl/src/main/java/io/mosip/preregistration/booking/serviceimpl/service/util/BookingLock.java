/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.serviceimpl.service.util;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class provides the locking object.
 * 
 * @author Rudra Tripathy
 * @since 1.0.0
 *
 */
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingLock {

	String registrationCenter;
	String date;
	String timeslot;

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass()) {
			return false;
		}

		BookingLock other = (BookingLock) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (registrationCenter == null) {
			if (other.registrationCenter != null)
				return false;
		} else if (!registrationCenter.equals(other.registrationCenter)) {
			return false;
		}
		if (timeslot == null) {
			if (other.timeslot != null)
				return false;
		} else if (!timeslot.equals(other.timeslot)) {
			return false;
		}

		return true;
	}

}
