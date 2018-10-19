/**
 * 
 */
package org.mosip.demo.masterdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentCenter {

	String centerName;
	String centerId;
	String centerLocation;

}
