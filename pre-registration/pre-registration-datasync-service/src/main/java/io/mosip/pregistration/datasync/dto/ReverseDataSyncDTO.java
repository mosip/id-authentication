package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DataSync entity
 * 
 * @author M1046129 - Jagadishwari
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ReverseDataSyncDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3615477037570600620L;

	private String id;
	private String ver;
	private Date reqTime;
	private ReverseDataSyncRequestDTO request;
}
