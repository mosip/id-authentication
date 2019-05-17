/**
 * 
 */
package io.mosip.dbdto;
	
import java.math.BigInteger;

import lombok.Data;

/**
 * The Class SyncRegistrationDto.
 *
 * @author M1047487
 * @author Girish Yarru
 */
@Data
public class SyncRegistrationDto  {

	private String langCode;
	private String registrationId;
	private String registrationType;
	private String packetHashValue;
	private BigInteger packetSize;
	private String supervisorStatus;
	private String supervisorComment;
}