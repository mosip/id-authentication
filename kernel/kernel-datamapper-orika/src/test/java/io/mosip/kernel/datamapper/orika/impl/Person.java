package io.mosip.kernel.datamapper.orika.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

	private String name;
	private String nickName;
	private int age;
	
}
