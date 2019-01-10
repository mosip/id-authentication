package io.mosip.preregistration.datasync.dto;

import java.io.Serializable;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author M1046129
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class PreRegistrationIdsDTO implements Serializable {

	private static final long serialVersionUID = 6402670047109104959L;
	@ApiModelProperty(value = "Transaction ID", position = 1)
	private String transactionId;
	@ApiModelProperty(value = "Count Of PreRegIds", position = 2)
	private String countOfPreRegIds;
	@ApiModelProperty(value = "Pre-Registration Ids", position = 3)
	private Map<String,String> preRegistrationIds;
}
