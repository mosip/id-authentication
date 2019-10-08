/**
 * 
 */
package io.mosip.kernel.masterdata.dto.getresponse;

import java.io.Serializable;

import io.mosip.kernel.masterdata.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FoundationalTrustProviderResDto  extends BaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7789639319339045489L;

	private String name;
	
	private String address;
	
	private String email;
	
	private String contactNo;
	
	private String certAlias;

}
