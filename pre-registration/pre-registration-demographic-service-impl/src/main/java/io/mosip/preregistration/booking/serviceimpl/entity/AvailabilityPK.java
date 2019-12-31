/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.serviceimpl.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
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
public class AvailabilityPK implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4022968783477038513L;
	
	/** Registration_Center_Id. */
	@Column(name = "regcntr_id")
	private String regcntrId;
	
	/** Availability_Date. */
	@Column(name = "availability_date")
	private LocalDate regDate;
	
	/**Slot_From_Time. */
	@Column(name = "slot_from_time")
	private LocalTime fromTime;
	
	

}
