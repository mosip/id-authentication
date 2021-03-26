package io.mosip.authentication.core.spi.indauth.match;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * The Class DynamicIdMapping.
 */
public class DynamicIdMapping implements IdMapping {
	
	private String idName;
	private List<String> mappings;
	
	
	public DynamicIdMapping(String idName, List<String> mappings) {
		super();
		this.idName = idName;
		this.mappings = mappings;
	}

	@Override
	public String getIdname() {
		return idName;
	}

	/**
	 * Gets the mapping function.
	 *
	 * @return the mapping function
	 */
	@Override
	public BiFunction<MappingConfig, MatchType, List<String>> getMappingFunction() {
		return (cfg, matchType) -> mappings;
	}

	/**
	 * Gets the sub id mappings.
	 *
	 * @return the sub id mappings
	 */
	@Override
	public Set<IdMapping> getSubIdMappings() {
		return Collections.emptySet();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public String getType() {
		return null;
	}

	/**
	 * Gets the sub type.
	 *
	 * @return the sub type
	 */
	@Override
	public String getSubType() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idName == null) ? 0 : idName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DynamicIdMapping other = (DynamicIdMapping) obj;
		if (idName == null) {
			if (other.idName != null)
				return false;
		} else if (!idName.equals(other.idName))
			return false;
		return true;
	}
	
	

}
