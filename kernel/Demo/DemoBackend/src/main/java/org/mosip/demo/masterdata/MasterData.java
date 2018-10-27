/**
 * 
 */
package org.mosip.demo.masterdata;

import java.util.List;

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
public class MasterData {

	String version;

	List<DemographicField> demographicFields;

	List<EnrollmentCenter> enrollmentCenters;

	List<BlacklistedWord> blacklistedWords;

}
