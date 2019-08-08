package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new resident service re-print request dto.
 * 
 * @author Ranjitha
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UinCardRePrintRequestDto extends BaseRestRequestDTO implements Serializable{
	
	private static final long serialVersionUID = -7951024873226775006L;
	
	private UinCardRequestDto request;

}
