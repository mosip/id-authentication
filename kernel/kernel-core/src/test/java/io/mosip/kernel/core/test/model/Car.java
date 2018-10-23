package io.mosip.kernel.core.test.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * @author Sidhant Agarwal
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Car {

	public String color;
	public String type;

	public Car(String color, String type) {
		super();
		this.color = color;
		this.type = type;
	}

	public Car() {
		this.color = " ";
		this.type = " ";
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
