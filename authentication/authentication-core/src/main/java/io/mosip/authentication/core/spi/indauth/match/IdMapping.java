package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public interface IdMapping {

	/**
	 * Method to get ID name
	 * 
	 * @return
	 */
	public String getIdname();

	/**
	 * Method to get Mapping Function
	 * 
	 * @return
	 */
	public Function<MappingConfig, List<String>> getMappingFunction();

	/**
	 * Method to get ID Mapping
	 * 
	 * @param name
	 * @param values
	 * @return
	 */
	public static Optional<IdMapping> getIdMapping(String name, IdMapping[] values) {
		return Stream.of(values).filter(m -> m.getIdname().equals(name)).findAny();
	}

}
