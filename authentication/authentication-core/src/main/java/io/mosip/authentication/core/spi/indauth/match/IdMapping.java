package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * The Interface IdMapping.
 *
 * @author Dinesh Karuppiah.T
 */

public interface IdMapping {

	/**
	 * Method to get ID name.
	 *
	 * @return the idname
	 */
	public String getIdname();

	/**
	 * Method to get Mapping Function.
	 *
	 * @return the mapping function
	 */
	public BiFunction<MappingConfig, MatchType, List<String>> getMappingFunction();

	/**
	 * Method to get ID Mapping.
	 *
	 * @param name the name
	 * @param values the values
	 * @param idMappingConfig the id mapping config
	 * @return the id mapping
	 */
	public static Optional<IdMapping> getIdMapping(String name, IdMapping[] values, MappingConfig idMappingConfig) {
		Optional<IdMapping> idMappingOpt = Stream.of(values)
			.filter(m -> m.getIdname().equals(name)).findAny();
		if(idMappingOpt.isEmpty()) {
			Map<String, List<String>> dynamicAttributes = idMappingConfig.getDynamicAttributes();
			return dynamicAttributes.entrySet()
						.stream()
						.filter(entry -> entry.getKey().equals(name))
						.<IdMapping>map(entry -> new DynamicIdMapping(name, entry.getValue()))
						.findAny();
		} else {
			return idMappingOpt;
		}
		
	}
	
	/**
	 * Gets the sub id mappings.
	 *
	 * @return the sub id mappings
	 */
	public Set<IdMapping> getSubIdMappings();
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType();
	
	/**
	 * Gets the sub type.
	 *
	 * @return the sub type
	 */
	public String getSubType();
	

}
