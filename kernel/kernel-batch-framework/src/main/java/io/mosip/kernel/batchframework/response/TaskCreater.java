package io.mosip.kernel.batchframework.response;

import lombok.Data;

/**
 * Response class for batch job task launch on server.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class TaskCreater {

	/**
	 * Job name.
	 */
	private String name;

	/**
	 * Job status.
	 */
	private String status;

}
