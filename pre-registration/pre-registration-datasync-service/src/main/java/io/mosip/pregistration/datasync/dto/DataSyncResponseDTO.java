package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataSyncResponseDTO<T> implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3384945682672832638L;

	/** The error details. */
	private List<ExceptionJSONInfo> err;

	private String status;

	private Date resTime;

	private T response;

}
