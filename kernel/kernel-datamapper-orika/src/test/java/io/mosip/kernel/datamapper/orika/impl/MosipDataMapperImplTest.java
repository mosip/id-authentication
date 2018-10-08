package io.mosip.kernel.datamapper.orika.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.mosip.kernel.core.spi.datamapper.MosipDataMapper;

public class MosipDataMapperImplTest {

	MosipClassMapBuilder mosipClassMapBuilder = new MosipClassMapBuilder();
	MosipDataMapper mosipDataMapperImpl;

	@Test
	public void givenSrcAndDest_whenMaps_thenCorrect() {

		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(SourceModel.class, DestinationModel.class).configure();

		SourceModel sourceObject = new SourceModel("Mosip", 10);

		DestinationModel destinationObject = (DestinationModel) mosipDataMapperImpl.map(sourceObject,
				DestinationModel.class);

		assertEquals(destinationObject.getName(), sourceObject.getName());

		assertEquals(destinationObject.getAge(), sourceObject.getAge());
	}

	@Test
	public void givenSrcAndDest_whenMapsReverse_thenCorrect() {

		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(SourceModel.class, DestinationModel.class).configure();

		DestinationModel dest = new DestinationModel("Neha", 20);
		SourceModel src = (SourceModel) mosipDataMapperImpl.map(dest, SourceModel.class);

		assertEquals(src.getName(), dest.getName());
		assertEquals(src.getAge(), dest.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMaps_thenCorrect() {

		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(Personne.class, Person.class).mapFieldInclude("nom", "name")
				.mapFieldInclude("surnom", "nickName").configure();

		Personne french = new Personne("Claire", "cla", 2);
		Person english = (Person) mosipDataMapperImpl.map(french, Person.class);

		assertEquals(english.getName(), french.getNom());
		assertEquals(english.getNickName(), french.getSurnom());
		assertEquals(english.getAge(), french.getAge());
	}

	@Test
	public void givenSrcAndDest_whenCanExcludeField_thenCorrect() {

		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(Personne.class, Person.class).mapFieldExclude("nom")
				.mapFieldInclude("surnom", "nickName").mapFieldInclude("age", "age").configure();

		Personne french = new Personne("Claire", "cla", 2);
		Person english = (Person) mosipDataMapperImpl.map(french, Person.class);

		assertNull(english.getName());
		assertEquals(english.getNickName(), french.getSurnom());
		assertEquals(english.getAge(), french.getAge());

	}

	@Test
	public void givenSrcWithListAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {

		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(PersonNameList.class, PersonNameParts.class)
				.mapFieldInclude("nameList[0]", "firstName").mapFieldInclude("nameList[1]", "lastName").configure();

		List<String> nameList = Arrays.asList(new String[] { "Sylvester", "Stallone" });

		PersonNameList src = new PersonNameList(nameList);

		PersonNameParts dest = (PersonNameParts) mosipDataMapperImpl.map(src, PersonNameParts.class);

		assertEquals(dest.getFirstName(), "Sylvester");
		assertEquals(dest.getLastName(), "Stallone");

	}

	// Mapping Null Values
	// In some cases, you may wish to control whether nulls are mapped or ignored
	// when they are encountered. By default, Orika will map null values when
	// encountered
	@Test
	public void givenSrcWithNullField_whenMapsThenCorrect() {

		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(SourceModel.class, DestinationModel.class).configure();

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = (DestinationModel) mosipDataMapperImpl.map(src, DestinationModel.class);

		assertEquals(dest.getAge(), src.getAge());
		assertEquals(dest.getName(), src.getName());
	}

	// Testing that Nulls are not getting mapped
	@Test
	public void givenSrcWithNullAndClassMapConfigNoNull_whenFailsToMap_thenCorrect() {

		// global config for no null
		mosipClassMapBuilder = new MosipClassMapBuilder(false);
		
		mosipDataMapperImpl = mosipClassMapBuilder.mapClass(SourceModel.class, DestinationModel.class).configure();

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);
		
		mosipDataMapperImpl.mapObjects(src, dest);

		assertEquals(dest.getAge(), src.getAge());
		assertEquals(dest.getName(), "Neha");

	}
}
