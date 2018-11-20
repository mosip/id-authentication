package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class DeleteDto implements Serializable{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;
	
	/** The pre-registration-Id. */
	private String prId;

	/** The created by. */
	private String deletedBy;

	/** The create date time. */
	private Timestamp deletedDateTime;
}
