package io.mosip.authentication.core.spi.indauth.match;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The Interface IdMapping.
 *
 * @author Dinesh Karuppiah.T
 */

public interface CBEFFMapping {

	/**
	 * Method to get ID name.
	 *
	 * @return the idname
	 */
	public String getName();

	/**
	 * Method to get Mapping Function.
	 *
	 * @return the mapping function
	 */
	public Function<CBEFFMappingConfig, String> getMappingFunction();

	/**
	 * Method to get ID Mapping.
	 *
	 * @param name the name
	 * @param values the values
	 * @return the id mapping
	 */
	public static Optional<CBEFFMapping> getCbeffMapping(String name, CBEFFMapping[] values) {
		return Stream.of(values).filter(m -> m.getName().equals(name)).findAny();
	}
	
//	

}
