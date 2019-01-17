package io.mosip.kernel.core.datamapper.spi;

import java.util.List;

import io.mosip.kernel.core.datamapper.model.IncludeDataField;

/**
 * DataMapperBuilder interface for building a configured {@link DataMapper}
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 * 
 * @see DataMapper
 */
public interface DataMapperBuilder<S, D> {

	/**
	 * Configure map null in mapping
	 * 
	 * @param mapNulls
	 *            map null true or not
	 * @return {@link DataMapperBuilder}
	 */
	DataMapperBuilder<S, D> mapNulls(boolean mapNulls);

	/**
	 * Configure byDefault in mapping
	 * 
	 * @param byDefault
	 *            byDefault true or not
	 * @return {@link DataMapperBuilder}
	 */
	DataMapperBuilder<S, D> byDefault(boolean byDefault);

	/**
	 * Configure included field in mapping
	 * 
	 * @param includeFields
	 *            list of included fields
	 * @return {@link DataMapperBuilder}
	 */
	DataMapperBuilder<S, D> includeFields(List<IncludeDataField> includeFields);

	/**
	 * Configure excluded fields in mapping
	 * 
	 * @param excludeFields
	 *            list of excluded fields
	 * @return {@link DataMapperBuilder}
	 */
	DataMapperBuilder<S, D> excludeFields(List<String> excludeFields);

	/**
	 * Build a Configured {@link DataMapper} instance
	 * 
	 * @return {@link DataMapper}
	 */
	DataMapper<S, D> build();

}