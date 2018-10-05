package io.mosip.kernel.datamapper.orika.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.mosip.kernel.core.spi.datamapper.MosipDataMapper;

public class MosipDataMapperImplTest {

	MosipClassMapBuilder mosipClassMapBuilder = new MosipClassMapBuilder();
	MosipDataMapper mosipDataMapperImpl;

	@Test
	public void givenSrcAndDest_whenMaps_thenCorrect() {

		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(SourceModel.class, DestinationModel.class).build();

		SourceModel sourceObject = new SourceModel("Mosip", 10);

		DestinationModel destinationObject = (DestinationModel) mosipDataMapperImpl.map(sourceObject,
				DestinationModel.class);

		assertEquals(destinationObject.getName(), sourceObject.getName());

		assertEquals(destinationObject.getAge(), sourceObject.getAge());
	}

	@Test
	public void givenSrcAndDest_whenMapsReverse_thenCorrect() {

		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(SourceModel.class, DestinationModel.class).build();

		DestinationModel dest = new DestinationModel("Neha", 20);
		SourceModel src = (SourceModel) mosipDataMapperImpl.map(dest, SourceModel.class);

		assertEquals(src.getName(), dest.getName());
		assertEquals(src.getAge(), dest.getAge());
	}
	
	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMaps_thenCorrect() {
		
	}
}
