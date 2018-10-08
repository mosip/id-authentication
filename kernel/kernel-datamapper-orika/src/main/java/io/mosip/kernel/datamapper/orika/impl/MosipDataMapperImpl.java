package io.mosip.kernel.datamapper.orika.impl;

import io.mosip.kernel.core.spi.datamapper.MosipDataMapper;
import ma.glasnost.orika.MapperFacade;

public class MosipDataMapperImpl implements MosipDataMapper{
	
	private MapperFacade mapper = null;

	public MosipDataMapperImpl(MapperFacade mapper) {

		this.mapper = mapper;

	}

	@Override
	public <S,D> D map(S source, Class<D> destinationClass) {
		return mapper.map(source, destinationClass);
	}

	@Override
	public <S,D> void mapObjects(S source, D destination) {
		mapper.map(source, destination);
	}

}