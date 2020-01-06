package io.mosip.registration.dto.json.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * This class contains the attributes to be displayed for Photograph object in
 * PacketMetaInfo JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
public class Photograph {

	@JsonProperty("BIRIndex")
	private String birIndex;
	private int numRetry;

}
