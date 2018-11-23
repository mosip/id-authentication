package io.mosip.kernel.datamapper.orika.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.model.IncludeDataField;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.datamapper.orika.test.model.DestinationModel;
import io.mosip.kernel.datamapper.orika.test.model.Person;
import io.mosip.kernel.datamapper.orika.test.model.Person2;
import io.mosip.kernel.datamapper.orika.test.model.PersonInterface;
import io.mosip.kernel.datamapper.orika.test.model.PersonListConverter;
import io.mosip.kernel.datamapper.orika.test.model.PersonNameList;
import io.mosip.kernel.datamapper.orika.test.model.PersonNameMap;
import io.mosip.kernel.datamapper.orika.test.model.PersonNameParts;
import io.mosip.kernel.datamapper.orika.test.model.Personne;
import io.mosip.kernel.datamapper.orika.test.model.SourceModel;

public class DataMapperImplTest {

	@Autowired
	DataMapper dataMapper;

	@Test
	public void personConverterListTest() {
		PersonListConverter personListConverter = new PersonListConverter();
		Person person = new Person();
		LocalDate dob = LocalDate.of(1994, Month.JANUARY, 1);
		person.setDob(dob);
		List<Person> personList = new ArrayList<>();
		personList.add(person);
		List<Personne> personneList = new ArrayList<Personne>();
		dataMapper.map(personList, personneList, personListConverter);
		assertEquals(24, personneList.get(0).getAge());
	}

	@Test
	public void givenSrcAndDest_whenMaps_thenCorrect() {

		SourceModel sourceObject = new SourceModel("Mosip", 10);
		DestinationModel destinationObject = dataMapper.map(sourceObject, DestinationModel.class, true, null, null,
				true);
		assertEquals(destinationObject.getName(), sourceObject.getName());
		assertEquals(destinationObject.getAge(), sourceObject.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMaps_thenCorrect() {

		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nom", "name", true),
				new IncludeDataField("surnom", "nickName", true));
		Personne french = new Personne("Claire", "cla", 2);

		Person2 english = dataMapper.map(french, Person2.class, true, includeField, null, true);

		assertEquals(french.getNom(), english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDest_whenCanExcludeField_thenCorrect() {

		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));

		Personne french = new Personne("Claire", "cla", 2);
		Person2 english = dataMapper.map(french, Person2.class, true, includeField, excludeField, true);

		assertNull(english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());

	}

	@Test
	public void givenSrcWithListAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {

		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nameList[0]", "firstName", true),
				new IncludeDataField("nameList[1]", "lastName", true));

		List<String> nameList = Arrays.asList(new String[] { "Sylvester", "Stallone" });

		PersonNameList src = new PersonNameList(nameList);

		PersonNameParts dest = dataMapper.map(src, PersonNameParts.class, true, includeField, null, true);

		assertEquals("Sylvester", dest.getFirstName());
		assertEquals("Stallone", dest.getLastName());

	}

	@Test
	public void givenSrcWithMapAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {

		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nameMap['first']", "firstName", true),
				new IncludeDataField("nameMap[\"last\"]", "lastName", true));

		Map<String, String> nameMap = new HashMap<>();
		nameMap.put("first", "Leornado");
		nameMap.put("last", "DiCaprio");

		PersonNameMap src = new PersonNameMap(nameMap);
		PersonNameParts dest = dataMapper.map(src, PersonNameParts.class, true, includeField, null, true);

		assertEquals("Leornado", dest.getFirstName());
		assertEquals("DiCaprio", dest.getLastName());
	}

	@Test
	public void givenSrcWithNullField_whenMapsThenCorrect() {

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = dataMapper.map(src, DestinationModel.class, true, null, null, true);

		assertEquals(src.getAge(), dest.getAge());
		assertEquals(src.getName(), dest.getName());
	}

	@Test
	public void givenSrcWithNullAndClassMapConfigNoNull_whenFailsToMap_thenCorrect() {

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);

		// global configuration for no null
		dataMapper.map(src, dest, true, null, null, true);

		assertEquals(src.getAge(), dest.getAge());
		assertNull(dest.getName());

	}

	@Test
	public void givenSrcWithNullAndClassMapConfigNull_whenFailsToMap_thenCorrect() {

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);

		dataMapper.map(src, dest, false, null, null, true);

		assertEquals(src.getAge(), dest.getAge());
		assertEquals("Neha", dest.getName());

	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsConfigNull_thenCorrect() {

		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nom", "name", false),
				new IncludeDataField("surnom", "nickName", true));

		Personne french = new Personne(null, null, 2);
		Person2 english = new Person2("Mosip", "Project", 10);

		dataMapper.map(french, english, true, includeField, null, true);

		assertEquals("Mosip", english.getName());
		assertNull(english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsConfigNoNullIncludingField_thenCorrect() {

		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nom", "name", true),
				new IncludeDataField("surnom", "nickName", true));

		Personne french = new Personne(null, "cla", 2);
		Person2 english = new Person2("Mosip", "Project", 10);

		dataMapper.map(french, english, true, includeField, null, true);

		assertNull(english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsExlcudingField_thenCorrect() {

		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));

		Personne french = new Personne(null, "cla", 2);
		Person2 english = new Person2("Mosip", "Project", 10);

		dataMapper.map(french, english, false, includeField, excludeField, true);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsNullExlcudingField_thenCorrect() {

		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", false));
		List<String> excludeField = Arrays.asList("nom");
		Personne french = new Personne(null, "cla", 2);
		Person2 english = new Person2("Mosip", "Project", 10);

		dataMapper.map(french, english, true, includeField, excludeField, true);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test(expected = DataMapperException.class)
	public void givenSrcAsNullDestWithFieldNames_whenMapsExlcudingField_thenFails() {

		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));

		Personne french = null;
		Person2 english = new Person2("Mosip", "Project", 10);

		dataMapper.map(french, english, true, includeField, excludeField, true);
	}

	@Test(expected = DataMapperException.class)
	public void givenSrcWithFieldDestAsNull_whenMapsExlcudingField_thenFails() {

		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));

		Personne french = new Personne(null, "cla", 2);
		Person english = null;

		dataMapper.map(french, english, true, includeField, excludeField, true);
	}

	@Test(expected = DataMapperException.class)
	public void givenSrcAndDestAsInterface_whenMaps_thenFails() {

		Personne french = new Personne("Claire", "cla", 2);
		dataMapper.map(french, PersonInterface.class, true, null, null, true);
	}

}
