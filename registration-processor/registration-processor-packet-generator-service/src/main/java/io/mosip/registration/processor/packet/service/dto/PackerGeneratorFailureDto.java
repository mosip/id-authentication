package io.mosip.registration.processor.packet.service.dto;

public class PackerGeneratorFailureDto extends PacketGeneratorResDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5397672054150780651L;

	/** The errorCode. */
	private String errorCode;

	public PackerGeneratorFailureDto() {
		super();

	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
