package io.mosip.kernel.datamapper.orika.config;

import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.core.spi.datamapper.DataMapper;
import io.mosip.kernel.datamapper.orika.fieldmapper.ExcludeDataField;
import io.mosip.kernel.datamapper.orika.fieldmapper.IncludeDataField;
import io.mosip.kernel.datamapper.orika.impl.DataMapperImpl;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
/**
 * /**
 * ClassMapBuilder provides a fluent API which can be used to define 
 * a mapping from one class to another.
 *
 * @author Neha
 *
 */
public class MapClassBuilder {

	private MapperFactory mapperFactory = null;

	private Class<?> source;
	private Class<?> destination;
	private List<IncludeDataField> includeFields;
	private List<ExcludeDataField> excludeFields;

	private boolean mapClassNull = true;

	/**
	 * Constructor for MapClassBuilder
	 * 
	 */
	public MapClassBuilder() {
		includeFields = new ArrayList<>();
		excludeFields = new ArrayList<>();
		mapperFactory = new DefaultMapperFactory.Builder().build();
	}

	/**
	 * Constructor for MapClassBuilder having
	 * 
	 * @param mapClassNull
	 * 			Configure whether to map nulls in generated mapper code at global level
	 */
	public MapClassBuilder(boolean mapClassNull) {
		this();
		this.mapClassNull = mapClassNull;
		mapperFactory = new DefaultMapperFactory.Builder().mapNulls(this.mapClassNull).build();
	}

	/**
     * Constructs a new MapClassBuilder instance initialized with the provided
     * types which can be used to configure/customize the mapping between the
     * two types.<br>
     * <br>
     * The returned MapClassBuilder instance, after being fully configured,
     * should finally be registered with the factory using the
     * <code>registerClassMap</code> method.
     * 
     * @param source
     *            the Class instance representing the "source" side of the mapping
     * @param destination
     *            the Class instance representing the "destination" side of the mapping
     * @return a MapClassBuilder instance for defining mapping between the provided types
     */
	public MapClassBuilder mapClass(Class<?> source, Class<?> destination) {
		this.source = source;
		this.destination = destination;
		return this;
	}

	/**
     * Map a field in both directions
     * 
     * @param sourceField
     *            field name of Source class
     * @param destinationField
     *            field name of Destination class
     * @return this MapClassBuilder
     */
	public MapClassBuilder mapFieldInclude(String sourceField, String destinationField) {
		includeFields.add(new IncludeDataField(sourceField, destinationField, true));
		return this;
	}

	/**
     * Map a field in both directions
     * 
     * @param sourceField
     *            field name of Source class
     * @param destinationField
     *            field name of Destination class
     * @param mapIncludeFieldNull
     * 			  Configure whether to map nulls in generated mapper code
     * @return this MapClassBuilder
     */
	public MapClassBuilder mapFieldInclude(String sourceField, String destinationField,
			boolean mapIncludeFieldNull) {
		if (!mapIncludeFieldNull) {
			includeFields.add(new IncludeDataField(sourceField, destinationField, mapIncludeFieldNull));
		} else {
			this.mapFieldInclude(sourceField, destinationField);
		}
		return this;
	}

	/**
     * Exclude the specified field from mapping
     * 
     * @param fieldName the name of the field/property to exclude
     * @return this MapClassBuilder
     */
	public MapClassBuilder mapFieldExclude(String sourceField) {
		excludeFields.add(new ExcludeDataField(sourceField, true));
		return this;
	}
	
	/**
     * Exclude the specified field from mapping
     * 
     * @param fieldName the name of the field/property to exclude
     * @param mapExcludeFieldNull
     * 			  Configure whether to map nulls in generated mapper code
     * @return this MapClassBuilder
     */
	public MapClassBuilder mapFieldExclude(String sourceField, boolean mapExcludeFieldNull) {
		if (!mapExcludeFieldNull) {
			excludeFields.add(new ExcludeDataField(sourceField, mapExcludeFieldNull));
		} else {
			this.mapFieldExclude(sourceField);
		}
		return this;
	}

	/**
     * Registers the ClassMap defined by this builder
     * 
     * @return {@link DataMapperImpl}
     */
	@SuppressWarnings("rawtypes")
	public DataMapper configure() {

		ClassMapBuilder<?, ?> builder = mapperFactory.classMap(this.source, this.destination);

		for (ExcludeDataField excludedField : excludeFields) {
			builder.mapNulls(excludedField.isMapExcludeFieldNull()).exclude(excludedField.getSourceField());
		}

		for (IncludeDataField includedField : includeFields) {
			builder.mapNulls(includedField.isMapIncludeFieldNull()).field(includedField.getSourceField(), includedField.getDestinationField());
		}

		builder.byDefault().register();

		MapperFacade mapper = mapperFactory.getMapperFacade();

		return new DataMapperImpl(mapper);
	}
}
