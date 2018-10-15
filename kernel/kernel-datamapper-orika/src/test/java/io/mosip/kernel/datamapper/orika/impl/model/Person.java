package io.mosip.kernel.datamapper.orika.impl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Neha
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

	private String name;
	private String nickName;
	private int age;
	
}
