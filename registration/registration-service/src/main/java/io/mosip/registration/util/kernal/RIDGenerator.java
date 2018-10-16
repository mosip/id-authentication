package io.mosip.registration.util.kernal;

import java.util.Date;

import io.mosip.kernel.core.util.DateUtils;

import io.mosip.registration.constants.RegConstants;

/**
 * Class to generate Registration ID - will be replaced by Kernel
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RIDGenerator {
	
	private static Integer idSequence = 0;
	
	private RIDGenerator() {
		
	}
	
	public static String nextRID() {
		StringBuilder rid = new StringBuilder();
		rid.append(RegConstants.AGENCY_CODE)
			.append(RegConstants.STATION_NUMBER)
			.append(String.format("%05d", ++idSequence))
			.append(DateUtils.formatDate(new Date(), RegConstants.RID_DATE_FORMAT));
		return rid.toString();
	}
}
