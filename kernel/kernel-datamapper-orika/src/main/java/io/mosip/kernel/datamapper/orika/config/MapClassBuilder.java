package io.mosip.kernel.datamapper.orika.config;

import java.util.List;

import io.mosip.kernel.core.spi.datamapper.model.IncludeDataField;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

/**
 * ClassMapBuilder provides a fluent API which can be used to define a mapping
 * from one class to another.
 *
 * @author Neha
 *
 */

public class MapClassBuilder<S, D> {

	private MapperFactory mapperFactory = null;

	private S source;
	private D destination;
	private Class<D> destinationClass;
	private List<IncludeDataField> includeFields;
	private List<String> excludeFields;
	private boolean applyDefault;

	/**
	 * Constructor for MapClassBuilder having
	 * 
	 * @param mapClassNull
	 *            Configure whether to map nulls in generated mapper code at global
	 *            level
	 */
	public MapClassBuilder(boolean mapClassNull) {
		mapperFactory = new DefaultMapperFactory.Builder().mapNulls(mapClassNull).build();
	}

	/**
	 * Constructs a new MapClassBuilder instance initialized with the provided types
	 * which can be used to configure/customize the mapping between the two types.
	 * 
	 * @param source
	 *            the Object instance representing the "source" side of the mapping
	 * @param destination
	 *            the Class instance representing the "destination" side of the
	 *            mapping
	 * @param includeDataField
	 *            the Class instance representing the "source field", "destination
	 *            field" and "mapNull" configuration
	 * @param excludeFields
	 *            this represent the "source field"
	 * @param applyDefault
	 *            this represents the "default" configuration of the mapping
	 * @return a MapClassBuilder instance for defining mapping between the provided
	 *         types
	 */
	public MapClassBuilder<S, D> mapClass(S source, Class<D> destinationClass, List<IncludeDataField> includeDataField,
			List<String> excludeFields, boolean applyDefault) {
		this.source = source;
		this.destination = null;
		this.destinationClass = destinationClass;
		this.includeFields = includeDataField;
		this.excludeFields = excludeFields;
		this.applyDefault = applyDefault;
		return this;
	}

	/**
	 * Constructs a new MapClassBuilder instance initialized with the provided types
	 * which can be used to configure/customize the mapping between the two types.
	 * 
	 * @param source
	 *            the Object instance representing the "source" side of the mapping
	 * @param destination
	 *            the Object instance representing the "destination" side of the
	 *            mapping
	 * @param includeDataField
	 *            the Class instance representing the "source field", "destination
	 *            field" and "mapNull" configuration
	 * @param excludeFields
	 *            this represent the "source field"
	 * @param applyDefault
	 *            this represents the "default" configuration of the mapping
	 * @return a MapClassBuilder instance for defining mapping between the provided
	 *         types
	 */
	public MapClassBuilder<S, D> mapClass(S source, D destination, List<IncludeDataField> includeDataField,
			List<String> excludeFields, boolean applyDefault) {
		this.source = source;
		this.destination = destination;
		this.includeFields = includeDataField;
		this.excludeFields = excludeFields;
		this.applyDefault = applyDefault;
		return this;
	}

	/**
	 * Registers the ClassMap defined by this builder
	 * 
	 * @return {@link MapperFacade}
	 */
	public MapperFacade configure() {

		ClassMapBuilder<?, ?> classMapBuilder;

		if (this.destination != null) {
			classMapBuilder = mapperFactory.classMap(this.source.getClass(), this.destination.getClass());
		} else {
			classMapBuilder = mapperFactory.classMap(this.source.getClass(), this.destinationClass);
		}

		if (excludeFields != null && !(excludeFields.isEmpty())) {
			for (String excludedField : excludeFields) {
				classMapBuilder.exclude(excludedField);
			}
		}

		if (includeFields != null && !(includeFields.isEmpty())) {
			for (IncludeDataField includedField : includeFields) {
				classMapBuilder.mapNulls(includedField.isMapIncludeFieldNull()).field(includedField.getSourceField(),
						includedField.getDestinationField());
			}
		}

		if (this.applyDefault) {
			classMapBuilder.byDefault().register();
		} else {
			classMapBuilder.register();
		}

		return mapperFactory.getMapperFacade();
	}
}
