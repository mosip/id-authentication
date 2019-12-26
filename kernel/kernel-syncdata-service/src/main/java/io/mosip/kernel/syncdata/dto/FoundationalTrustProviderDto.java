/**
 * 
 */
package io.mosip.kernel.syncdata.dto;

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
public class FoundationalTrustProviderDto {
	
	private String id;
	
	private String name;
	
	private String address;
	
	private String email;
	
	private String contactNo;
	
	private String certAlias;

}
