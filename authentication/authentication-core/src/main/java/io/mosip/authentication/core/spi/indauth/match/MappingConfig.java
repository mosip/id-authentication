package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public interface MappingConfig {

	/**
	 * List of value to hold name
	 * 
	 * @return
	 */
	public List<String> getName();

	/**
	 * List of value to hold DOB
	 * 
	 * @return
	 */
	public List<String> getDob();

	/**
	 * List of value to hold DOBtype
	 * 
	 * @return
	 */
	public List<String> getDobType();

	/**
	 * List of value to hold Age
	 * 
	 * @return
	 */
	public List<String> getAge();

	/**
	 * List of value to hold Gender
	 * 
	 * @return
	 */
	public List<String> getGender();

	/**
	 * List of value to hold Phone Number
	 * 
	 * @return
	 */
	public List<String> getPhoneNumber();

	/**
	 * List of value to hold Email ID
	 * 
	 * @return
	 */
	public List<String> getEmailId();

	/**
	 * List of value to hold Address Line 1
	 * 
	 * @return
	 */
	public List<String> getAddressLine1();

	/**
	 * List of value to hold Address Line 2
	 * 
	 * @return
	 */
	public List<String> getAddressLine2();

	/**
	 * List of value to hold Address Line 3
	 * 
	 * @return
	 */
	public List<String> getAddressLine3();

	/**
	 * List of value to hold Location 1
	 * 
	 * @return
	 */
	public List<String> getLocation1();

	/**
	 * List of value to hold Location 2
	 * 
	 * @return
	 */
	public List<String> getLocation2();

	/**
	 * List of value to hold Location 3
	 * 
	 * @return
	 */
	public List<String> getLocation3();

	/**
	 * List of value to hold Pincode
	 * 
	 * @return
	 */
	public List<String> getPinCode();

	/**
	 * List of value to hold Full Address
	 * 
	 * @return
	 */
	public List<String> getFullAddress();

	/**
	 * List of value to hold Otp
	 * 
	 * @return
	 */
	public List<String> getOtp();

	/**
	 * List of value to hold Pin
	 * 
	 * @return
	 */
	public List<String> getPin();

	/**
	 * List of value to hold IRIS
	 * 
	 * @return
	 */
	public List<String> getIris();

	/**
	 * List of value to hold Left Index
	 * 
	 * @return
	 */

	public List<String> getLeftIndex();

	/**
	 * List of value to hold Left Little
	 * 
	 * @return
	 */
	public List<String> getLeftLittle();

	/**
	 * List of value to hold Left Middle
	 * 
	 * @return
	 */
	public List<String> getLeftMiddle();

	/**
	 * List of value to hold Left Ring
	 * 
	 * @return
	 */
	public List<String> getLeftRing();

	/**
	 * List of value to hold Left Thumb
	 * 
	 * @return
	 */
	public List<String> getLeftThumb();

	/**
	 * List of value to hold Right Index
	 * 
	 * @return
	 */
	public List<String> getRightIndex();

	/**
	 * List of value to hold Right Little
	 * 
	 * @return
	 */
	public List<String> getRightLittle();

	/**
	 * List of value to hold Right Middle
	 * 
	 * @return
	 */
	public List<String> getRightMiddle();

	/**
	 * List of value to hold Right Ring
	 * 
	 * @return
	 */
	public List<String> getRightRing();

	/**
	 * List of value to hold Right Thumb
	 * 
	 * @return
	 */
	public List<String> getRightThumb();

	/**
	 * List of value to hold Finger print
	 * 
	 * @return
	 */
	public List<String> getFingerprint();

	/**
	 * List of value to hold Face
	 * 
	 * @return
	 */
	public List<String> getFace();
	
	
	public List<String> getLeftEye();
	
	public List<String> getRightEye();

}
