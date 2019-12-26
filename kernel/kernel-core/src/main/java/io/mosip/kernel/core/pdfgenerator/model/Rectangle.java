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
	 * The lower left x value of rectangle.
	 */
	private float llx;
	/**
	 * The lower left y value of rectangle.
	 */
	private float lly;
	/**
	 * The upper right x value of rectangle.
	 */
	private float urx;
	/**
	 * The upper right y value of rectangle.
	 */
	private float ury;

}