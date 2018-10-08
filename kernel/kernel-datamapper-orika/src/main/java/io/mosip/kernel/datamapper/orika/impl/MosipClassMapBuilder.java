package io.mosip.kernel.datamapper.orika.impl;

import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.core.spi.datamapper.MosipDataMapper;
import io.mosip.kernel.datamapper.orika.fieldmapper.ExcludeDataField;
import io.mosip.kernel.datamapper.orika.fieldmapper.IncludeDataField;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

public class MosipClassMapBuilder {

	private MapperFactory mapperFactory = null;

	private Class<?> source;
	private Class<?> destination;
	private List<IncludeDataField> includeFields;
	private List<ExcludeDataField> excludeFields;

	private boolean mapClassNull = true;

	public MosipClassMapBuilder() {
		includeFields = new ArrayList<>();
		excludeFields = new ArrayList<>();
		mapperFactory = new DefaultMapperFactory.Builder().build();
	}

	public MosipClassMapBuilder(boolean mapClassNull) {
		this();
		this.mapClassNull = mapClassNull;
		mapperFactory = new DefaultMapperFactory.Builder().mapNulls(this.mapClassNull).build();
	}

	public MosipClassMapBuilder mapClass(Class<?> source, Class<?> destination) {
		this.source = source;
		this.destination = destination;
		return this;
	}

	public MosipClassMapBuilder mapFieldInclude(String sourceField, String destinationField) {
		includeFields.add(new IncludeDataField(sourceField, destinationField, true));
		return this;
	}

	public MosipClassMapBuilder mapFieldInclude(String sourceField, String destinationField,
			boolean mapIncludeFieldNull) {
		if (!mapIncludeFieldNull) {
			includeFields.add(new IncludeDataField(sourceField, destinationField, mapIncludeFieldNull));
		} else {
			this.mapFieldInclude(sourceField, destinationField);
		}
		return this;
	}

	public MosipClassMapBuilder mapFieldExclude(String sourceField) {
		excludeFields.add(new ExcludeDataField(sourceField, true));
		return this;
	}

	public MosipClassMapBuilder mapFieldExclude(String sourceField, boolean mapExcludeFieldNull) {
		if (!mapExcludeFieldNull) {
			excludeFields.add(new ExcludeDataField(sourceField, mapExcludeFieldNull));
		} else {
			this.mapFieldExclude(sourceField);
		}
		return this;
	}

	public MosipDataMapper configure() {

		ClassMapBuilder<?, ?> builder = mapperFactory.classMap(this.source, this.destination);

		for (ExcludeDataField excludedField : excludeFields) {
			builder.exclude(excludedField.getSourceField()).mapNulls(excludedField.isMapExcludeFieldNull());
		}

		for (IncludeDataField includedField : includeFields) {
			builder.field(includedField.getSourceField(), includedField.getDestinationField())
					.mapNulls(includedField.isMapIncludeFieldNull());
		}

		builder.byDefault().register();

		MapperFacade mapper = mapperFactory.getMapperFacade();

		return new MosipDataMapperImpl(mapper);
	}
}
