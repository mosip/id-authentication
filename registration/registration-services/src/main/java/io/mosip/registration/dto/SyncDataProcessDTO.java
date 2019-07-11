/**
 * 
 */
package io.mosip.registration.dto;

import lombok.Data;

/**
 * The DTO Class SyncData Process.
 *
 * @author YASWANTH S
 * @since 1.0.0
 */

@Data
public class SyncDataProcessDTO {

	public SyncDataProcessDTO(String jobId, String jobName, String jobStatus, String lastUpdatedTimes) {
		super();
		this.jobId = jobId;
		this.jobName = jobName;
		this.jobStatus = jobStatus;
		this.lastUpdatedTimes = lastUpdatedTimes;
	}

	private String jobId;
	private String jobName;
	private String jobStatus;
	private String lastUpdatedTimes;

}
