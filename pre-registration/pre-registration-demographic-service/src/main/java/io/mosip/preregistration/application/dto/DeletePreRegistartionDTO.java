/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the values for request parameters when
 * performing deletion operarion.
 * 
 * @author Tapaswini Bahera
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DeletePreRegistartionDTO implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The pre-registration-Id. */
	private String prId;

	/** The created by. */
	private String deletedBy;

	/** The create date time. */
	private Date deletedDateTime;
}
