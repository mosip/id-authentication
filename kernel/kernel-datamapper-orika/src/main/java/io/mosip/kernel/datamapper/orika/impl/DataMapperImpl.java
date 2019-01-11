package io.mosip.kernel.datamapper.orika.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.model.IncludeDataField;
import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.datamapper.orika.constant.DataMapperErrorCodes;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

/**
 * Data Mapper implementation of the {@link DataMapper} interface.
 * 
 * @author Neha
 * @since 1.0.0
 * 
 */
@Component
public class DataMapperImpl implements DataMapper {

	private MapperFactory mapperFactory = null;

	
	private MapperFactory getMapperFactory() {
		return new DefaultMapperFactory.Builder().build();
	}

	
	public DataMapperImpl() {
		mapperFactory = getMapperFactory();
	}
	/**
	 * Constructs a new MapClassBuilder instance initialized with the provided types
	 * which can be used to configure/customize the mapping between the two types.
	 * 
	 * @param source
	 *            the Object instance representing the "source" side of the mapping
	 * @param destinationClass
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
	 *//*
	public DataMapperImpl(boolean mapClassNull) {
		mapperFactory = getMapperFactory(mapClassNull);
		
	}*/


	/**
	 * Registers the ClassMap defined by this builder
	 * 
	 * @return {@link MapperFacade}
	 */
	public <S,D> MapperFacade configure(S source, Class<D> destinationClass, List<IncludeDataField> includeDataField,
			List<String> excludeDataField,boolean mapNull,boolean byDefault) {

		ClassMapBuilder<?, ?> classMapBuilder = mapperFactory.classMap(source.getClass(),destinationClass);
	    
		classMapBuilder.mapNulls(mapNull);
		
	    if (excludeDataField != null && !(excludeDataField.isEmpty())) {
			for (String excludedField : excludeDataField) {
				classMapBuilder.exclude(excludedField);
			}
		}

		if (includeDataField != null && !(includeDataField.isEmpty())) {
			for (IncludeDataField includedField : includeDataField) {
				classMapBuilder.mapNulls(includedField.isMapIncludeFieldNull()).field(includedField.getSourceField(),
						includedField.getDestinationField());
			}
		}

		if (byDefault) {
			classMapBuilder.byDefault().register();
		} else {
			classMapBuilder.register();
		}

		return mapperFactory.getMapperFacade();
	
	}
	
	@Override
	public <S, D> void map(S source, D destination, DataConverter<S, D> dataConverter) {
		try {
			dataConverter.convert(source, destination);
		} catch (Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.MAPPING_ERR.getErrorCode(),
					DataMapperErrorCodes.MAPPING_ERR.getErrorMessage(), e);
		}
	}

	@Override
	public <S, D> D map(S source, Class<D> destinationClass, boolean mapNull, List<IncludeDataField> includeDataField,
			List<String> excludeDataField, boolean byDefault) {
		configure(source, destinationClass, includeDataField, excludeDataField,mapNull, byDefault);
		return this.mapperFactory.getMapperFacade().map(source, destinationClass);
	}

	@Override
	public <S, D> void map(S source, D destination, boolean mapNull, List<IncludeDataField> includeDataField,
			List<String> excludeDataField, boolean byDefault) {
		configure(source, destination.getClass(), includeDataField,excludeDataField,mapNull,byDefault);
		this.mapperFactory.getMapperFacade().map(source, destination);
		
	}

}