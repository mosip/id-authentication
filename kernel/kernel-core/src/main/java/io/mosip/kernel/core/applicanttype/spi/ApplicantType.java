package io.mosip.kernel.core.applicanttype.spi;

import java.util.Map;

import io.mosip.kernel.core.applicanttype.exception.InvalidApplicantArgumentException;

public interface ApplicantType {

	/**
	 * @param map contains attribute and its value
	 * @return the applicant code for given attribute combination
	 * @throws InvalidApplicantArgumentException if provided map or its attribute
	 *                                           value pairs is null
	 */
	public String getApplicantType(Map<String, Object> map) throws InvalidApplicantArgumentException;

}