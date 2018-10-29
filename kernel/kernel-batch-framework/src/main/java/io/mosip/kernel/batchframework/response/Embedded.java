package io.mosip.kernel.batchframework.response;

import java.util.List;

import lombok.Data;

/**
 * Response class for batch job registration to server.
 * 
 * @author Ritesh sinha
 * @since 1.0.0
 */
@Data
public class Embedded {

	/**
	 * List of app registered.
	 */
	private List<AppRegistrationResourceList> list;

}
