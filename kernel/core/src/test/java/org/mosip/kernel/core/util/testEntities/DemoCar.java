package org.mosip.kernel.core.util.testEntities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * @author Sidhant Agarwal
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.NON_PRIVATE)
public class DemoCar {
	public int age;
	public long no;

}
