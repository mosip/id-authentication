package io.mosip.kernel.core.spi.datamapper;

public interface MosipDataMapper {

	public <S, D> D map(S source, Class<D> destinationClass);
	
	public <S, D> void mapObjects(S source, D destination);
	
}
