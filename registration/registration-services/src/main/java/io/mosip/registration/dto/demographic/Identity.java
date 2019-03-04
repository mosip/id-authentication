package io.mosip.registration.dto.demographic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The base class for Identity
 *
 * @author Balaji Sridharan
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
