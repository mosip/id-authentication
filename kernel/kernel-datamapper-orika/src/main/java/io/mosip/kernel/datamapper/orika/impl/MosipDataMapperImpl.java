package io.mosip.kernel.datamapper.orika.impl;

import io.mosip.kernel.core.spi.datamapper.MosipDataMapper;
import ma.glasnost.orika.MapperFacade;


public class MosipDataMapperImpl implements MosipDataMapper {
	
	private MapperFacade mapper = null;
	
	public MosipDataMapperImpl(MapperFacade mapper) {
		
		this.mapper=mapper;
		
	}
	
	public Object map(Object source, Class<?> destinationClass) {
		
		return mapper.map(source, destinationClass);
	}
}
