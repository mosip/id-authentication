package io.mosip.preregistration.booking.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author M1046129
 *
 */
@Getter
@Setter
@ToString
public class MainListRequestDTO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6489834223858096784L;
	/**
	 * id
	 */
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	/**
	 * version
	 */
	@ApiModelProperty(value = "request version", position = 2)
	private String ver;
	/**
	 * reqTime
	 */
	@ApiModelProperty(value = "request time", position = 3)
	private Date reqTime;
	/**
	 * To accept preregid, regcenterid, timeslot and booked date time
	 */
	@ApiModelProperty(value = "list of request", position = 4)
	private List<T> request;
}
