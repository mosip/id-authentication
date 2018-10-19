package org.mosip.demo.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.mosip.demo.dto.OtpGeneratorRequestDto;
import org.mosip.demo.dto.OtpGeneratorResponseDto;
import org.mosip.demo.dto.OtpValidatorResponseDto;
import org.mosip.demo.dto.PersonDto;
import org.mosip.demo.entity.Enrollment;
import org.mosip.demo.entity.Person;
import org.mosip.kernel.core.mosipsecurity.exception.MosipInvalidDataException;
import org.mosip.kernel.core.mosipsecurity.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.exception.MosipJsonGenerationException;
import org.mosip.kernel.core.utils.exception.MosipJsonMappingException;
import org.mosip.kernel.core.utils.exception.MosipJsonParseException;

/**
 * Scheduling service interface with function to get all enrollment centers and add new schedule
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface SchedulingService {

	/**
	 * Function to fetch list of enrollment centers
	 * 
	 * @return List of enrollment centers
	 */
	List<Enrollment> getEnrolmentCenters();

	/**
	 * Function to add new enrollment schedule
	 * 
	 * @param personDto The person DTO object
	 * @return created person
	 */
	Person addSchedule(PersonDto personDto);

	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws MosipInvalidKeyException 
	 * @throws MosipInvalidDataException 
	 * @throws IOException 
	 * 
	 */
	boolean securitydemo() throws IOException, MosipInvalidDataException, MosipInvalidKeyException, NoSuchAlgorithmException;

	/**
	 * @return
	 * @throws MosipIOException 
	 * @throws MosipJsonMappingException 
	 * @throws MosipJsonGenerationException 
	 * @throws MosipJsonParseException 
	 */
	boolean jsonDemo() throws MosipJsonGenerationException, MosipJsonMappingException, MosipIOException, MosipJsonParseException;

	/**
	 * @return
	 * @throws MosipIOException 
	 */
	boolean zipDemo() throws MosipIOException;

	/**
	 * @return
	 */
	OtpGeneratorResponseDto getOtp(OtpGeneratorRequestDto otpGeneratorRequestDto);
	
	OtpValidatorResponseDto validateOtp(String key, String otp) throws MosipJsonParseException, MosipJsonMappingException, MosipIOException;

	/**
	 * @return
	 */
	boolean daoDemo();

}