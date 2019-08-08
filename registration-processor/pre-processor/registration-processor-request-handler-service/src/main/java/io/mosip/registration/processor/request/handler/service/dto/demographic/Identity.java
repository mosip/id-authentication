package io.mosip.registration.processor.request.handler.service.dto.demographic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The base class for Identity
 *
 * @author Sowmya
 * @since 1.0.0
 */
@JsonInclude(value = Include.NON_EMPTY)
public class Identity {

	/** The ID schema version. */
	@JsonProperty("IDSchemaVersion")
	private double idSchemaVersion;

	/**
	 * @return the idSchemaVersion
	 */
	public double getIdSchemaVersion() {
		return idSchemaVersion;
	}

	/**
	 * @param idSchemaVersion
	 *            the idSchemaVersion to set
	 */
	public void setIdSchemaVersion(double idSchemaVersion) {
		this.idSchemaVersion = idSchemaVersion;
	}

}
