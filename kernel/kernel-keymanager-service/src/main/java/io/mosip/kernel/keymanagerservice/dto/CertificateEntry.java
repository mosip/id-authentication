package io.mosip.kernel.keymanagerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**Certificate Key Entry from SoftHsm
 * 
 * @author Urvil Joshi
 *
 * @param <C> Certificate Type
 * @param <P> PrivateKey Type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateEntry<C,P> {

	private C[] chain;
	
	private P privateKey;
	
	
	
}
