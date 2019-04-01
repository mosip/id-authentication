/**
 * 
 */
package io.mosip.registration.dto;

import javafx.beans.property.SimpleStringProperty;
import lombok.Data;

/**
 * The DTO Class SyncData Process.
 *
 * @author YASWANTH S
 * @since 1.0.0
 */

@Data
public class SyncDataProcessDTO {

	private SimpleStringProperty jobId;
	private SimpleStringProperty jobName;
	private SimpleStringProperty jobStatus;
	private SimpleStringProperty lastUpdatedTimes;

	/**
	 * Zero Parameter DTO
	 */
	public SyncDataProcessDTO() {
		super();
	}

	/**
	 * @param jobId
	 *            ID
	 * @param jobName
	 *            Name
	 * @param jobStatus
	 *            status
	 * @param lastUpdatedTimes
	 *            last updated times
	 */
	public SyncDataProcessDTO(String jobId, String jobName, String jobStatus, String lastUpdatedTimes) {
		super();
		this.jobId = new SimpleStringProperty(jobId);
		this.jobName = new SimpleStringProperty(jobName);
		this.jobStatus = new SimpleStringProperty(jobStatus);
		this.lastUpdatedTimes = new SimpleStringProperty(lastUpdatedTimes);
	}

	public String getJobId() {
		return jobId.get();
	}

	public void setJobId(String jobId) {
		this.jobId.set(jobId);
	}

	public String getJobName() {
		return jobName.get();
	}

	public void setJobName(String jobName) {
		this.jobName.set(jobName);
	}

	public String getJobStatus() {
		return jobStatus.get();
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus.set(jobStatus);
	}

	public String getLastUpdatedTimes() {
		return lastUpdatedTimes.get();
	}

	public void setLastUpdatedTimes(String lastUpdatedTimes) {
		this.lastUpdatedTimes.set(lastUpdatedTimes);
	}
}
