package io.mosip.kernel.datamapper.orika.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.model.IncludeDataField;
import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.datamapper.orika.constant.DataMapperErrorCodes;
import io.mosip.kernel.datamapper.orika.provider.MapperFactoryProvider;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.MapperKey;
import ma.glasnost.orika.metadata.TypeFactory;

/**
 * Data Mapper implementation of the {@link DataMapper} interface.
 * 
 * @author Urvil Joshi
 * @author Neha
 * @since 1.0.0
 * 
 */
@Component
public class DataMapperImpl<S, D> implements DataMapper<S,D>{

	private BoundMapperFacade<S, D> mapper;

	public DataMapperImpl(Class<S> sourceClass, Class<D> destinationClass, boolean mapNull, boolean byDefault,
			List<IncludeDataField> includeDataField, List<String> excludeDataField) {
		DefaultMapperFactory mapperFactory = MapperFactoryProvider.getMapperFactory();
		MapperKey mapperKey = new MapperKey(TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass));
		ClassMapBuilder<?, ?> classMapBuilder = mapperFactory.classMap(mapperKey.getAType(), mapperKey.getBType());
        classMapBuilder.mapNulls(mapNull);
		if (excludeDataField != null && !(excludeDataField.isEmpty())) {
			excludeDataField.forEach(classMapBuilder::exclude);
		}

		if (includeDataField != null && !(includeDataField.isEmpty())) {
			includeDataField.forEach(includedField ->classMapBuilder.mapNulls(includedField.isMapIncludeFieldNull()).field(includedField.getSourceField(),
					includedField.getDestinationField()));
		}

		if (byDefault) {
			classMapBuilder.byDefault().register();
		} else {
			classMapBuilder.register();
		}
		this.mapper = mapperFactory.getMapperFacade(sourceClass, destinationClass, false);
	}
    
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataMapper#map(java.lang.Object)
	 */
	@Override
	public D map(S source) {
		try {
		return mapper.map(source);
		} catch (Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.MAPPING_ERR.getErrorCode(),
					DataMapperErrorCodes.MAPPING_ERR.getErrorMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataMapper#map(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void map(S source, D destination) {
		try {
		mapper.map(source, destination);
		} catch (Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.MAPPING_ERR.getErrorCode(),
					DataMapperErrorCodes.MAPPING_ERR.getErrorMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataMapper#map(java.lang.Object, java.lang.Object, io.mosip.kernel.core.datamapper.spi.DataConverter)
	 */
	@Override
	public void map(S source, D destination, DataConverter<S, D> dataConverter) {
		try {
			dataConverter.convert(source, destination);
		} catch (Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.MAPPING_ERR.getErrorCode(),
					DataMapperErrorCodes.MAPPING_ERR.getErrorMessage(), e);
		}
	}
}