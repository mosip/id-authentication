package io.mosip.registration.processor.packet.service.dto;

/**
 * @author Sowmya The Class PackerGeneratorFailureDto.
 */
public class PackerGeneratorFailureDto extends PacketGeneratorResDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5397672054150780651L;

	/** The errorCode. */
	private String errorCode;

	/**
	 * Instantiates a new packer generator failure dto.
	 */
	public PackerGeneratorFailureDto() {
		super();

	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code.
	 *
	 * @param errorCode
	 *            the new error code
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
