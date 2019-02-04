package io.mosip.authentication.core.spi.indauth.match;

/**
 * The Interface MappingConfig.
 *
 * @author Dinesh Karuppiah.T
 */

public interface CBEFFMappingConfig {

	/**
	 * List of value to hold Left Index.
	 *
	 * @return the left index
	 */

	public String getLeftIndex();

	/**
	 * List of value to hold Left Little.
	 *
	 * @return the left little
	 */
	public String getLeftLittle();

	/**
	 * List of value to hold Left Middle.
	 *
	 * @return the left middle
	 */
	public String getLeftMiddle();

	/**
	 * List of value to hold Left Ring.
	 *
	 * @return the left ring
	 */
	public String getLeftRing();

	/**
	 * List of value to hold Left Thumb.
	 *
	 * @return the left thumb
	 */
	public String getLeftThumb();

	/**
	 * List of value to hold Right Index.
	 *
	 * @return the right index
	 */
	public String getRightIndex();

	/**
	 * List of value to hold Right Little.
	 *
	 * @return the right little
	 */
	public String getRightLittle();

	/**
	 * List of value to hold Right Middle.
	 *
	 * @return the right middle
	 */
	public String getRightMiddle();

	/**
	 * List of value to hold Right Ring.
	 *
	 * @return the right ring
	 */
	public String getRightRing();

	/**
	 * List of value to hold Right Thumb.
	 *
	 * @return the right thumb
	 */
	public String getRightThumb();

	/**
	 * Gets the left eye.
	 *
	 * @return the left eye
	 */
	public String getLeftEye();

	/**
	 * Gets the right eye.
	 *
	 * @return the right eye
	 */
	public String getRightEye();

	/**
	 * Gets the right face.
	 *
	 * @return the right eye
	 */
	public String getFace();

}
