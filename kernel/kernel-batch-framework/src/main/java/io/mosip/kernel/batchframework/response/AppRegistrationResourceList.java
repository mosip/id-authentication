package io.mosip.kernel.batchframework.response;

import lombok.Data;

/**
 * Response class for batch job registration to server used in {@link Embedded}.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class AppRegistrationResourceList {
	/**
	 * job name
	 */
	private String name;
	/**
	 * job uri
	 */
	private String uri;

}
