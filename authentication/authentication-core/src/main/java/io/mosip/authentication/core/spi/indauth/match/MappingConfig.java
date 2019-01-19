package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface MappingConfig.
 *
 * @author Dinesh Karuppiah.T
 */

public interface MappingConfig {

	/**
	 * List of value to hold Full Name.
	 *
	 * @return the name
	 */
	public List<String> getFullName();

	/**
	 * List of value to hold DOB.
	 *
	 * @return the dob
	 */
	public List<String> getDob();

	/**
	 * List of value to hold DOBtype.
	 *
	 * @return the dob type
	 */
	public List<String> getDobType();

	/**
	 * List of value to hold Age.
	 *
	 * @return the age
	 */
	public List<String> getAge();

	/**
	 * List of value to hold Gender.
	 *
	 * @return the gender
	 */
	public List<String> getGender();

	/**
	 * List of value to hold Phone Number.
	 *
	 * @return the phone number
	 */
	public List<String> getPhoneNumber();

	/**
	 * List of value to hold Email ID.
	 *
	 * @return the email id
	 */
	public List<String> getEmailId();

	/**
	 * List of value to hold Address Line 1.
	 *
	 * @return the address line 1
	 */
	public List<String> getAddressLine1();

	/**
	 * List of value to hold Address Line 2.
	 *
	 * @return the address line 2
	 */
	public List<String> getAddressLine2();

	/**
	 * List of value to hold Address Line 3.
	 *
	 * @return the address line 3
	 */
	public List<String> getAddressLine3();

	/**
	 * List of value to hold Location 1.
	 *
	 * @return the location 1
	 */
	public List<String> getLocation1();

	/**
	 * List of value to hold Location 2.
	 *
	 * @return the location 2
	 */
	public List<String> getLocation2();

	/**
	 * List of value to hold Location 3.
	 *
	 * @return the location 3
	 */
	public List<String> getLocation3();

	/**
	 * List of value to hold Postalcode.
	 *
	 * @return the pin code
	 */
	public List<String> getPostalCode();

	/**
	 * List of value to hold Full Address.
	 *
	 * @return the full address
	 */
	public List<String> getFullAddress();

	/**
	 * List of value to hold Otp.
	 *
	 * @return the otp
	 */
	public List<String> getOtp();

	/**
	 * List of value to hold Pin.
	 *
	 * @return the pin
	 */
	public List<String> getPin();

	/**
	 * List of value to hold IRIS.
	 *
	 * @return the iris
	 */
	public List<String> getIris();

	/**
	 * List of value to hold Left Index.
	 *
	 * @return the left index
	 */

	public List<String> getLeftIndex();

	/**
	 * List of value to hold Left Little.
	 *
	 * @return the left little
	 */
	public List<String> getLeftLittle();

	/**
	 * List of value to hold Left Middle.
	 *
	 * @return the left middle
	 */
	public List<String> getLeftMiddle();

	/**
	 * List of value to hold Left Ring.
	 *
	 * @return the left ring
	 */
	public List<String> getLeftRing();

	/**
	 * List of value to hold Left Thumb.
	 *
	 * @return the left thumb
	 */
	public List<String> getLeftThumb();

	/**
	 * List of value to hold Right Index.
	 *
	 * @return the right index
	 */
	public List<String> getRightIndex();

	/**
	 * List of value to hold Right Little.
	 *
	 * @return the right little
	 */
	public List<String> getRightLittle();

	/**
	 * List of value to hold Right Middle.
	 *
	 * @return the right middle
	 */
	public List<String> getRightMiddle();

	/**
	 * List of value to hold Right Ring.
	 *
	 * @return the right ring
	 */
	public List<String> getRightRing();

	/**
	 * List of value to hold Right Thumb.
	 *
	 * @return the right thumb
	 */
	public List<String> getRightThumb();

	/**
	 * List of value to hold Finger print.
	 *
	 * @return the fingerprint
	 */
	public List<String> getFingerprint();

	/**
	 * List of value to hold Face.
	 *
	 * @return the face
	 */
	public List<String> getFace();

	/**
	 * Gets the left eye.
	 *
	 * @return the left eye
	 */
	public List<String> getLeftEye();

	/**
	 * Gets the right eye.
	 *
	 * @return the right eye
	 */
	public List<String> getRightEye();

}
