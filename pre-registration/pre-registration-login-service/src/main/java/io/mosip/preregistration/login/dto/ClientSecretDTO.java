package io.mosip.preregistration.login.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Akshay Jain
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientSecretDTO {

	private String clientId;
	private String secretKey;
	private String appId;
}
