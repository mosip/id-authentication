package io.mosip.authentication.common.service.integration.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used to construct the data to be encrypted
 * 
 * @author Sanjay Murali
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Class representing for Encryting Request")
public class EncryptDataRequestDto {

	/**
	 * The string applicationID
	 */
	@ApiModelProperty(notes = "Application id of decrypting module", example = "REGISTRATION", required = true)
	private String applicationId;

	/**
	 * The field for timestamp
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@ApiModelProperty(notes = "Timestamp", example = "2018-12-10T06:12:52.994Z", required = true)
	private LocalDateTime timeStamp;

	/**
	 * The string reference id
	 */
	@ApiModelProperty(notes = "Reference Id", example = "REF01")
	private String referenceId;
	
	/** 
	 * The salt
	 */
	@ApiModelProperty(notes = "Reference Id")
	private String salt;

	/**
	 * The string encryptedSymmetricKey
	 */
	@ApiModelProperty(notes = "Data to be encrypted", required = true)
	private String data;



}
