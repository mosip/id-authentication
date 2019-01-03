package io.mosip.registration.processor.packet.storage.dto;
	
import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Instantiates a new photograph dto.
 */
@Data

/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
@EqualsAndHashCode(callSuper=false)
public class PhotographDto {
	
	/** The pre reg id. */
	private String preRegId;
	
	/** The image name. */
	private String imageName;
	
	/** The quality score. */
	private BigDecimal qualityScore;
	
	/** The no of retry. */
	private Integer noOfRetry;
	
	/** The image store. */
	private byte[] imageStore;
	
	/** The has excp photograph. */
	private Boolean hasExcpPhotograph;
	
	/** The excp photo name. */
	private String excpPhotoName;
	
	/** The excp photo store. */
	private byte[] excpPhotoStore;
	
	/** The is active. */
	private boolean isActive;
	
	/** The cr by. */
	private String crBy = "MOSIP_SYSTEM";
	
	/** The reg id. */
	private String regId;

}
