package io.mosip.kernel.datamapper.orika.impl;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonNameList {

	private List<String> nameList;
	
}
