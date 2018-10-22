package io.mosip.kernel.datamapper.orika.impl;

import java.util.List;

import io.mosip.kernel.core.spi.datamapper.DataMapper;
import io.mosip.kernel.core.spi.datamapper.converter.DataConverter;
import io.mosip.kernel.core.spi.datamapper.model.IncludeDataField;
import io.mosip.kernel.datamapper.orika.config.MapClassBuilder;
import io.mosip.kernel.datamapper.orika.constant.DataMapperErrorCodes;
import io.mosip.kernel.datamapper.orika.exception.DataMapperException;

/**
 * Data Mapper implementation of the {@link DataMapper} interface.
 * 
 * @author Neha
 * @since 1.0.0
 * 
 */
public class DataMapperImpl implements DataMapper {
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.spi.datamapper.DataMapper#map(java.lang.Object,
	 * java.lang.Class, java.util.List, java.util.List, boolean)
	 */
	@Override
	public <S, D> D map(S source, Class<D> destinationClass, boolean mapNull, List<IncludeDataField> includeDataField,
			List<String> excludeDataField, boolean applyDefault) {
		try {
			MapClassBuilder<S, D> mapClassBuilder = new MapClassBuilder<>(mapNull);
			mapClassBuilder.mapClass(source, destinationClass, includeDataField, excludeDataField, applyDefault);
			return mapClassBuilder.configure().map(source, destinationClass);
		} catch (Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.MAPPING_ERR.getErrorCode(),
					DataMapperErrorCodes.MAPPING_ERR.getErrorMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.spi.datamapper.DataMapper#map(java.lang.Object,
	 * java.lang.Object, java.util.List, java.util.List, boolean)
	 */
	@Override
	public <S, D> void map(S source, D destination, boolean mapNull, List<IncludeDataField> includeDataField,
			List<String> excludeDataField, boolean applyDefault) {
		try {
			MapClassBuilder<S, D> mapClassBuilder = new MapClassBuilder<>(mapNull);
			mapClassBuilder.mapClass(source, destination, includeDataField, excludeDataField, applyDefault);
			mapClassBuilder.configure().map(source, destination);
		} catch (Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.MAPPING_ERR.getErrorCode(),
					DataMapperErrorCodes.MAPPING_ERR.getErrorMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.spi.datamapper.DataMapper#map(java.lang.Object,
	 * java.lang.Class, java.lang.Class)
	 */
	@Override
	public <S, D> void map(S source, D destination, DataConverter<S, D> dataConverter) {
		try {
			dataConverter.convert(source, destination);
		} catch (Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.MAPPING_ERR.getErrorCode(),
					DataMapperErrorCodes.MAPPING_ERR.getErrorMessage(), e);
		}
	}

}