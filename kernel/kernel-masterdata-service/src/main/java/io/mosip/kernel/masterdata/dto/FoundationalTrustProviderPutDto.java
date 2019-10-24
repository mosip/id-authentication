/**
 * 
 */
package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoundationalTrustProviderPutDto {
	
	@NotEmpty(message="Id must not be blank or null")
	private String id;
	
	@NotEmpty(message="name must not be blank or null")
	private String name;
	
	@NotEmpty(message="address must not be blank or null")
	private String address;
	
	@NotEmpty(message="email must not be blank or null")
	private String email;
	
	@NotEmpty(message="contactNo must not be blank or null")
	private String contactNo;
	
	@NotEmpty(message="certAlias must not be blank or null")
	private String certAlias;
	
	private boolean isActive;

}
