package io.mosip.registration.processor.core.packet.dto.packetvalidator;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author Girish Yarru
 *
 */
@Data
@ToString
public class ReverseDatasyncReponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2428377488380750510L;

	/**
	 * transactionId
	 */
	private String transactionId;

	/**
	 * Count Of Stored PreRegIds
	 */
	private String countOfStoredPreRegIds;

	/**
	 * Already Stored PreRegIds
	 */
	private String alreadyStoredPreRegIds;
}
