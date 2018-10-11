package io.mosip.registration.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	private String prId;

	private String groupId;

	private Boolean isPrimary;
	
	/** The created by. */
	private String createdBy;

	/** The create date time. */
	private LocalDateTime createDateTime;

	/** The updated by. */
	private String updatedBy;

	/** The update date time. */
	private LocalDateTime updateDateTime;


}
