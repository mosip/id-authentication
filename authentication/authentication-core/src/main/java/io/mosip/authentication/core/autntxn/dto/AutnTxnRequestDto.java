package io.mosip.authentication.core.autntxn.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Data
public class AutnTxnRequestDto {
	String individualId;
	String individualIdType;
	Integer pageStart;
	Integer pageFetch;
	List<AutnTxnDto> authtxnlist;
}
