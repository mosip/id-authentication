package io.mosip.kernel.datamapper.orika.test.model;

import java.util.Map;

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
public class PersonNameMap {

	private Map<String, String> nameMap;

}
