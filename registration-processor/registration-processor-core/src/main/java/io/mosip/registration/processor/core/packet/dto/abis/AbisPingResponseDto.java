package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

/**
 * The Class AbisPingResponseDto.
 * @author M1048860 Kiran Raj
 */
public class AbisPingResponseDto  extends AbisCommonResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1344142383925376038L;
	/** The jobs count. */
	private int jobsCount;

	
	/**
	 * Gets the jobs count.
	 *
	 * @return the jobs count
	 */
	public int getJobsCount() {
		return jobsCount;
	}

	/**
	 * Sets the jobs count.
	 *
	 * @param jobsCount the new jobs count
	 */
	public void setJobsCount(int jobsCount) {
		this.jobsCount = jobsCount;
	}

}
