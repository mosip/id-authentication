/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This entity class defines the database table details for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationBookingPK implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4604149554069906933L;

	/**
	 * Pre registration Id
	 */
	@Column(name="prereg_id")
	private String preregistrationId;
	
	/**
	 * Booking date and time
	 */
	@Column(name="booking_dtimes")
	private LocalDateTime bookingDateTime;
	
}
