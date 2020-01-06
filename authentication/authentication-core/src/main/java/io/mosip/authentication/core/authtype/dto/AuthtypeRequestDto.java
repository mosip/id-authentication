package io.mosip.authentication.core.authtype.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Data
public class AuthtypeRequestDto {

	String individualId;
	String individualIdType;
	List<AuthtypeStatus> authtypes;

}
