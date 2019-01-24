package io.mosip.kernel.datamapper.orika.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.datamapper.orika.test.model.DestinationModel;
import io.mosip.kernel.datamapper.orika.test.model.Person;
import io.mosip.kernel.datamapper.orika.test.model.PersonDestination;
import io.mosip.kernel.datamapper.orika.test.model.PersonInterface;
import io.mosip.kernel.datamapper.orika.test.model.PersonListConverter;
import io.mosip.kernel.datamapper.orika.test.model.PersonNameList;
import io.mosip.kernel.datamapper.orika.test.model.PersonNameMap;
import io.mosip.kernel.datamapper.orika.test.model.PersonNameParts;
import io.mosip.kernel.datamapper.orika.test.model.Personne;
import io.mosip.kernel.datamapper.orika.test.model.SourceModel;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DataMapperImplTest {

	@Qualifier("personneToPersonDestinationMapper")
	@Autowired
	DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper;
	
	@Qualifier("personneToPersonDestinationMapper2")
	@Autowired
	DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper2;
	
	@Qualifier("personneToPersonDestinationMapper3")
	@Autowired
	DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper3;
	
	@Qualifier("personneToPersonDestinationMapper4")
	@Autowired
	DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper4;
	
	@Qualifier("personneToPersonDestinationMapper5")
	@Autowired
	DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper5;
	
	@Qualifier("personneToPersonDestinationMapper6")
	@Autowired
	DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper6;

	@Qualifier("personNameListToPersonNamePartsMapper")
	@Autowired
	DataMapper<PersonNameList, PersonNameParts> personNameListToPersonNamePartsMapper;
	
	@Qualifier("personNameMapToPersonNamePartsMapper")
	@Autowired
	DataMapper<PersonNameMap, PersonNameParts> personNameMapToPersonNamePartsMapper;
	
	@Qualifier("sourceModelToDestinationModelMapper")
	@Autowired
	DataMapper<SourceModel, DestinationModel> sourceModelToDestinationModelMapper;
	
	@Qualifier("sourceModelToDestinationModelMapper2")
	@Autowired
	DataMapper<SourceModel, DestinationModel> sourceModelToDestinationModelMapper2;
	
	@Qualifier("personneToPersonMapper")
	@Autowired
	DataMapper<Personne, Person> personneToPersonMapper;
	
	@Qualifier("personneToPersonInterface")
	@Autowired
	DataMapper<Personne, PersonInterface> personneToPersonInterface;
	
	@Qualifier("personListToPersonneListMapper")
	@Autowired
	DataMapper<List<Person>, List<Personne>> personListToPersonneListMapper;
	
	@Qualifier("sourceModelToDestinationModelMapper3")
	@Autowired
	DataMapper<SourceModel, DestinationModel> sourceModelToDestinationModelMapper3;
	
	@Test
	public void personConverterListTest() {
		PersonListConverter personListConverter = new PersonListConverter();
		Person person = new Person();
		LocalDate dob = LocalDate.of(1994, Month.JANUARY, 1);
		person.setDob(dob);
		List<Person> personList = new ArrayList<>();
		personList.add(person);
		List<Personne> personneList = new ArrayList<Personne>();
		personListToPersonneListMapper.map(personList, personneList, personListConverter);
		assertEquals(Period.between(LocalDate.of(1994, Month.JANUARY, 1), LocalDate.now()).getYears(), personneList.get(0).getAge());
	}
	
	@Test(expected = DataMapperException.class)
	public void personConverterListFailureTest() {
		Person person = new Person();
		LocalDate dob = LocalDate.of(1994, Month.JANUARY, 1);
		person.setDob(dob);
		List<Person> personList = new ArrayList<>();
		personList.add(person);
		List<Personne> personneList = new ArrayList<Personne>();
		personListToPersonneListMapper.map(personList, personneList, null);
	}
	
	@Test(expected = DataMapperException.class)
	public void personConverterListFailureMappingTest() {
		PersonListConverter personListConverter = new PersonListConverter();
		Person person = new Person();
		LocalDate dob = LocalDate.of(1994, Month.JANUARY, 1);
		person.setDob(dob);
		List<Person> personList = new ArrayList<>();
		personList.add(person);
		personListToPersonneListMapper.map(personList, null, personListConverter);
	}
	
	@Test
	public void givenSrcAndDest_whenCanExcludeField_thenCorrect() {
		Personne french = new Personne("Claire", "cla", 2);
		PersonDestination english = personneToPersonDestinationMapper.map(french);

		assertNull(english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcWithListAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {

		List<String> nameList = Arrays.asList(new String[] { "Sylvester", "Stallone" });
		PersonNameList src = new PersonNameList(nameList);
		
		PersonNameParts dest = personNameListToPersonNamePartsMapper.map(src);

		assertEquals("Sylvester", dest.getFirstName());
		assertEquals("Stallone", dest.getLastName());
	}

	@Test
	public void givenSrcWithMapAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {

		Map<String, String> nameMap = new HashMap<>();
		nameMap.put("first", "Leornado");
		nameMap.put("last", "DiCaprio");

		PersonNameMap src = new PersonNameMap(nameMap);
		PersonNameParts dest = personNameMapToPersonNamePartsMapper.map(src);

		assertEquals("Leornado", dest.getFirstName());
		assertEquals("DiCaprio", dest.getLastName());
	}

	@Test
	public void givenSrcWithNullField_whenMapsThenSuccess() {

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = sourceModelToDestinationModelMapper2.map(src);

		assertEquals(src.getAge(), dest.getAge());
		assertEquals(src.getName(), dest.getName());
	}
	
	@Test
	public void givenSrcWithNullField_whenMapsThenCorrect() {

		SourceModel src = new SourceModel("Me", 10);
		DestinationModel dest = sourceModelToDestinationModelMapper.map(src);

		assertEquals(src.getAge(), dest.getAge());
		assertEquals(src.getName(), dest.getName());
	}
	
	@Test
	public void givenSrcWithNullField_whenMapsByDefaultFalseThenCorrect() {

		SourceModel src = new SourceModel("Abc", 10);
		DestinationModel dest = new DestinationModel("Efg", 0);
		sourceModelToDestinationModelMapper3.map(src, dest);

		assertEquals(src.getName(), dest.getName());
		assertEquals(10, dest.getAge());
	}

	@Test
	public void givenSrcWithNullAndClassMapConfigNoNull_whenFailsToMap_thenCorrect() {
		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);

		sourceModelToDestinationModelMapper.map(src, dest);

		assertEquals(src.getAge(), dest.getAge());
		assertNull(dest.getName());
	}

	@Test
	public void givenSrcWithNullAndClassMapConfigNull_whenFailsToMap_thenCorrect() {

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);

		sourceModelToDestinationModelMapper2.map(src, dest);

		assertEquals(src.getAge(), dest.getAge());
		assertNull(dest.getName());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsConfigNull_thenCorrect() {
		Personne french = new Personne(null, null, 2);
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);
		personneToPersonDestinationMapper2.map(french, english);

		assertEquals("Mosip", english.getName());
		assertNull(english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsConfigNoNullIncludingField_thenCorrect() {
		Personne french = new Personne(null, "cla", 2);
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

		personneToPersonDestinationMapper3.map(french, english);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsExlcudingField_thenCorrect() {
		Personne french = new Personne(null, "cla", 2);
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

		personneToPersonDestinationMapper4.map(french, english);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsNullExlcudingField_thenCorrect() {
		Personne french = new Personne(null, "cla", 2);
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

		personneToPersonDestinationMapper5.map(french, english);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAsNullDestWithFieldNames_whenMapsExlcudingField_thenFails() {
		Personne french = null;
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

		personneToPersonDestinationMapper6.map(french, english);
		assertEquals("Mosip", english.getName());
	}

	@Test(expected = DataMapperException.class)
	public void givenSrcWithFieldDestAsNull_whenMapsExlcudingField_thenFails() {

		Personne french = new Personne(null, "cla", 2);
		Person english = null; 
		personneToPersonMapper.map(french, english);
		assertNull(english);
	}

	@Test(expected = DataMapperException.class)
	public void givenSrcAndDestAsInterface_whenMaps_thenFails() {
		Personne french = new Personne("Claire", "cla", 2);
		personneToPersonInterface.map(french);
	}

}
