package io.mosip.registration.processor.core.packet.dto.packetvalidator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
/**
 * 
 * @author Girish Yarru
 *
 * @param <T>
 */
@Data
@ToString
public class MainRequestDTO<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -889792322042255010L;


	/**
	 * Id
	 */
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	/**
	 * version
	 */
	@ApiModelProperty(value = "request ver", position = 2)
	private String ver;
	/**
	 * Request Date Time
	 */
	@ApiModelProperty(value = "request time", position = 3)
	private Date reqTime;
	/**
	 * Request Object
	 */
	@ApiModelProperty(value = "request", position = 4)
	private T request;
}
