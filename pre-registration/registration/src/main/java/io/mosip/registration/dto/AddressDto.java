package io.mosip.registration.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class AddressDto implements Serializable {

	

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;
	
	/**	The addrLine1 */
	private String addrLine1;

	/**	The addrLine2 */
	private String addrLine2;

	/**	The addrLine3 */
	private String addrLine3;
	
	/**	The locationCode */
	private String locationCode;

	
	@Override
	public String toString() {
		return "AddressDto [addrLine1=" + addrLine1 + ", addrLine2=" + addrLine2 + ", addrLine3=" + addrLine3
				+ ", locationCode=" + locationCode + "]";
	}
}
