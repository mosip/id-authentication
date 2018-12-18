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

	public String getIdname();

	public Function<MappingConfig, List<String>> getMappingFunction();
	
	public static Optional<IdMapping> getIdMapping(String name, IdMapping[] values) {
		return Stream.of(values).filter(m -> m.getIdname().equals(name)).findAny();
	}

}
