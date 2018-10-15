package io.mosip.kernel.datamapper.orika.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.mosip.kernel.core.spi.datamapper.DataMapper;
import io.mosip.kernel.datamapper.orika.config.MapClassBuilder;
import io.mosip.kernel.datamapper.orika.exception.DataMapperException;
import io.mosip.kernel.datamapper.orika.impl.model.DestinationModel;
import io.mosip.kernel.datamapper.orika.impl.model.Person;
import io.mosip.kernel.datamapper.orika.impl.model.PersonNameList;
import io.mosip.kernel.datamapper.orika.impl.model.PersonNameMap;
import io.mosip.kernel.datamapper.orika.impl.model.PersonNameParts;
import io.mosip.kernel.datamapper.orika.impl.model.Personne;
import io.mosip.kernel.datamapper.orika.impl.model.SourceModel;

/**
 * 
 * @author Neha
 *
 */
public class DataMapperImplTest {

	MapClassBuilder mapClassBuilder = new MapClassBuilder();

	@Test
	public void givenSrcAndDest_whenMaps_thenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(SourceModel.class, DestinationModel.class)
				.configure();

		SourceModel sourceObject = new SourceModel("Mosip", 10);

		@SuppressWarnings("unchecked")
		DestinationModel destinationObject = (DestinationModel) dataMapper.map(sourceObject,
				DestinationModel.class);

		assertEquals(destinationObject.getName(), sourceObject.getName());

		assertEquals(destinationObject.getAge(), sourceObject.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMaps_thenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class)
				.mapFieldInclude("nom", "name").mapFieldInclude("surnom", "nickName").configure();

		Personne french = new Personne("Claire", "cla", 2);
		@SuppressWarnings("unchecked")
		Person english = (Person) dataMapper.map(french, Person.class);

		assertEquals(french.getNom(), english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDest_whenCanExcludeField_thenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class)
				.mapFieldExclude("nom").mapFieldInclude("surnom", "nickName").mapFieldInclude("age", "age").configure();

		Personne french = new Personne("Claire", "cla", 2);
		@SuppressWarnings("unchecked")
		Person english = (Person) dataMapper.map(french, Person.class);

		assertNull(english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());

	}

	@Test
	public void givenSrcWithListAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(PersonNameList.class, PersonNameParts.class)
				.mapFieldInclude("nameList[0]", "firstName").mapFieldInclude("nameList[1]", "lastName").configure();

		List<String> nameList = Arrays.asList(new String[] { "Sylvester", "Stallone" });

		PersonNameList src = new PersonNameList(nameList);

		@SuppressWarnings("unchecked")
		PersonNameParts dest = (PersonNameParts) dataMapper.map(src, PersonNameParts.class);

		assertEquals("Sylvester", dest.getFirstName());
		assertEquals("Stallone", dest.getLastName());

	}

	@Test
	public void givenSrcWithMapAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(PersonNameMap.class, PersonNameParts.class)
				.mapFieldInclude("nameMap['first']", "firstName").mapFieldInclude("nameMap[\"last\"]", "lastName")
				.configure();

		Map<String, String> nameMap = new HashMap<>();
		nameMap.put("first", "Leornado");
		nameMap.put("last", "DiCaprio");

		PersonNameMap src = new PersonNameMap(nameMap);
		@SuppressWarnings("unchecked")
		PersonNameParts dest = (PersonNameParts) dataMapper.map(src, PersonNameParts.class);

		assertEquals("Leornado", dest.getFirstName());
		assertEquals("DiCaprio", dest.getLastName());
	}

	// Mapping Null Values
	@Test
	public void givenSrcWithNullField_whenMapsThenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(SourceModel.class, DestinationModel.class)
				.configure();

		SourceModel src = new SourceModel(null, 10);
		@SuppressWarnings("unchecked")
		DestinationModel dest = (DestinationModel) dataMapper.map(src, DestinationModel.class);

		assertEquals(src.getAge(), dest.getAge());
		assertEquals(src.getName(), dest.getName());
	}

	// Testing that Nulls are not getting mapped
	@SuppressWarnings("unchecked")
	@Test
	public void givenSrcWithNullAndClassMapConfigNoNull_whenFailsToMap_thenCorrect() {

		// global configuration for no null
		mapClassBuilder = new MapClassBuilder(false);

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(SourceModel.class, DestinationModel.class)
				.configure();

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);

		dataMapper.mapObjects(src, dest);

		assertEquals(src.getAge(), dest.getAge());
		assertEquals("Neha", dest.getName());

	}

	// Testing that Nulls are not getting mapped
	@SuppressWarnings("unchecked")
	@Test
	public void givenSrcWithNullAndClassMapConfigNull_whenFailsToMap_thenCorrect() {

		// global configuration for no null
		mapClassBuilder = new MapClassBuilder(true);

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(SourceModel.class, DestinationModel.class)
				.configure();

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);

		dataMapper.mapObjects(src, dest);

		assertEquals(src.getAge(), dest.getAge());
		assertNull(dest.getName());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsConfigNull_thenCorrect() {

		mapClassBuilder = new MapClassBuilder(true);

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class)
				.mapFieldInclude("nom", "name", false).mapFieldInclude("surnom", "nickName", true).configure();

		Personne french = new Personne(null, null, 2);
		Person english = new Person("Mosip", "Project", 10);

		dataMapper.mapObjects(french, english);

		assertEquals("Mosip", english.getName());
		assertNull(english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsConfigNoNullIncludingField_thenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class)
				.mapFieldInclude("nom", "name", true).mapFieldInclude("surnom", "nickName").configure();

		Personne french = new Personne(null, "cla", 2);
		Person english = new Person("Mosip", "Project", 10);

		dataMapper.mapObjects(french, english);

		assertNull(english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsExlcudingField_thenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class)
				.mapFieldExclude("nom", true).mapFieldInclude("surnom", "nickName").configure();

		Personne french = new Personne(null, "cla", 2);
		Person english = new Person("Mosip", "Project", 10);

		dataMapper.mapObjects(french, english);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsNullExlcudingField_thenCorrect() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class)
				.mapFieldExclude("nom", false).mapFieldInclude("surnom", "nickName").configure();

		Personne french = new Personne(null, "cla", 2);
		Person english = new Person("Mosip", "Project", 10);

		dataMapper.mapObjects(french, english);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@SuppressWarnings("unchecked")
	@Test(expected=DataMapperException.class)
	public void givenSrcAsNullDestWithFieldNames_whenMapsExlcudingField_thenFails() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class)
				.mapFieldExclude("nom", true).mapFieldInclude("surnom", "nickName").configure();

		Personne french = null;
		Person english = new Person("Mosip", "Project", 10);

		dataMapper.mapObjects(french, english);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=DataMapperException.class)
	public void givenSrcWithFieldDestAsNull_whenMapsExlcudingField_thenFails() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class)
				.mapFieldExclude("nom", true).mapFieldInclude("surnom", "nickName").configure();

		Personne french = new Personne(null, "cla", 2);
		Person english = null;

		dataMapper.mapObjects(french, english);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=DataMapperException.class)
	public void givenSrcAsNullAndDest_whenMaps_thenFails() {

		@SuppressWarnings("rawtypes")
		DataMapper dataMapper = mapClassBuilder.mapClass(Personne.class, Person.class).configure();

		Personne french = new Personne("Claire", "cla", 2);
		dataMapper.map(french, PersonInterface.class);
	}
}
