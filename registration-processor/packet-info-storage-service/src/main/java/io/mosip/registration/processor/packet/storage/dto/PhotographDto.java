package io.mosip.registration.processor.packet.storage.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class PhotographDto {
	private String preRegId;
	private String imageName;
	private BigDecimal qualityScore;
	private Integer noOfRetry;
	private byte[] imageStore;
	private Boolean hasExcpPhotograph;
	private String excpPhotoName;
	private byte[] excpPhotoStore;
	private boolean isActive;
	private String crBy = "MOSIP_SYSTEM";
	private String regId;

}
