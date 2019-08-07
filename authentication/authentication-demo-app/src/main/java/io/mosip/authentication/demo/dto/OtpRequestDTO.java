package io.mosip.authentication.demo.dto;

import java.util.List;

import lombok.Data;

/**
 * The Class OtpRequestDTO.
 * 
 * @author Sanjay Murali
 */
@Data
public class OtpRequestDTO {

	/** Variable to hold id */
	private String id;

	/** Variable to hold version */
	private String version;

	/** Variable to hold Transaction ID */
	private String transactionID;

	/** Variable to hold Request time */
	private String requestTime;

	/** Variable to hold individualID */
	private String individualId;

	/** Variable to hold partnerID */
	private String individualIdType;

	private List<String> otpChannel;

}