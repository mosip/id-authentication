package io.mosip.kernel.datamapper.orika.impl;

import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.core.spi.datamapper.MosipDataMapper;
import io.mosip.kernel.datamapper.orika.fieldmapper.DataField;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

public class MosipClassMapBuilder {
	
	private Class<?> source;
	private Class<?> destination;
	private List<DataField> fields;
	private List<String> excludedFields;
	
	public MosipClassMapBuilder() {
		fields = new ArrayList<>();
		excludedFields = new ArrayList<>();
	}
	
	public MosipClassMapBuilder mapClass(Class<?> source, Class<?> destination) {
		this.source = source;
		this.destination = destination;
		return this;
	}
	
	public MosipClassMapBuilder mapField(String sourceField, String destinationField) {
		fields.add(new DataField(sourceField, destinationField));
		return this;
	}
	
	public MosipClassMapBuilder excludeField(String sourceField) {
		excludedFields.add(sourceField);
		return this;
	}
	
	public MosipDataMapper build() {
		
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		
		ClassMapBuilder<?, ?> builder = mapperFactory.classMap(this.source, this.destination);
		
		for(String excludedField : excludedFields) {
			builder.exclude(excludedField);
		}
		
		for(DataField dataField : fields) {
			builder.field(dataField.getSourceField(), dataField.getDestinationField());
		}
		
		builder.byDefault().register();
		
		MapperFacade mapper = mapperFactory.getMapperFacade();
		
		return new MosipDataMapperImpl(mapper);
	}
}











