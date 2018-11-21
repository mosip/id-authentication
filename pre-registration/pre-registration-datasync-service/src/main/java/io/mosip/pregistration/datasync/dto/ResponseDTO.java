package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author M1046129 - Jagadishwari
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseDTO<T> implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3384945682672832638L;

	/** The error details. */
	private List<ExceptionJSONInfo> err;

	private String status;

	private Timestamp resTime;

	private List<T> response;

}
