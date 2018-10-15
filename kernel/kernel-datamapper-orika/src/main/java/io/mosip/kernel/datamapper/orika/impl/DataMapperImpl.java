package io.mosip.kernel.datamapper.orika.impl;

import io.mosip.kernel.core.spi.datamapper.DataMapper;
import io.mosip.kernel.datamapper.orika.constant.DataMapperErrorCodes;
import io.mosip.kernel.datamapper.orika.exception.DataMapperException;
import ma.glasnost.orika.MapperFacade;

/**
 * Data Mapper implementation of the {@link DataMapper} interface.
 * 
 * @author Neha
 * @since 1.0.0
 * 
 * @param <S>
 *            the type of the source object
 * @param <D>
 *            the type of the destination object
 */
public class DataMapperImpl<S, D> implements DataMapper<S, D> {

	/**
	 * Field for runtime interface between a Java application and data-mapper.
	 */
	private MapperFacade mapper = null;

	/**
	 * Constructor for DataMapperImpl having Mapper configuration information
	 * 
	 * @param mapper
	 */
	public DataMapperImpl(MapperFacade mapper) {
		this.mapper = mapper;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.datamapper.DataMapper#map(java.lang.Object, java.langClass)
	 */
	@Override
	public D map(S source, Class<D> destinationClass) {
		try {
			return mapper.map(source, destinationClass);
		}
		catch(Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.ERR_MAPPING);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.datamapper.DataMapper#map(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void mapObjects(S source, D destination) {
		try {
			mapper.map(source, destination);
		} 
		catch(Exception e) {
			throw new DataMapperException(DataMapperErrorCodes.ERR_MAPPING);
		}
	}

}