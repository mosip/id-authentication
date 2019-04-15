package io.mosip.kernel.datamapper.orika.test.model;

import java.util.List;

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
public class PersonNameList {

	private List<String> nameList;

}
