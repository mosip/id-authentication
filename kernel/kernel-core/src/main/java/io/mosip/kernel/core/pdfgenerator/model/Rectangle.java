package io.mosip.kernel.core.pdfgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rectangle model for pdf generator
 * 
 * @author Urvil Joshi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rectangle {

	/**
	 * The x coordinate of lower left point of sign rectangle.
	 */
	private float x;
	/**
	 * The y coordinate of lower left point of sign rectangle.
	 */
	private float y;
	/**
	 * The width value of sign rectangle.
	 */
	private float width;
	/**
	 * The height value of sign rectangle.
	 */
	private float height;

}