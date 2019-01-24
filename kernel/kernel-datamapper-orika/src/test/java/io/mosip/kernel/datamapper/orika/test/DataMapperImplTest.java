package io.mosip.kernel.datamapper.orika.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import io.mosip.kernel.core.datamapper.model.IncludeDataField;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.datamapper.orika.builder.DataMapperBuilderImpl;
import io.mosip.kernel.datamapper.orika.test.model.DestinationModel;
import io.mosip.kernel.datamapper.orika.test.model.Person;
import io.mosip.kernel.datamapper.orika.test.model.PersonDestination;
import io.mosip.kernel.datamapper.orika.test.model.PersonInterface;
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

		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nameList[0]", "firstName", true),
				new IncludeDataField("nameList[1]", "lastName", true));

		List<String> nameList = Arrays.asList(new String[] { "Sylvester", "Stallone" });

		PersonNameList src = new PersonNameList(nameList);

		DataMapper<PersonNameList, PersonNameParts> dataMapperImpl_1 = new DataMapperBuilderImpl<>(PersonNameList.class,
				PersonNameParts.class).includeFields(includeField).build();

		PersonNameParts dest = dataMapperImpl_1.map(src);

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

		DataMapper<PersonNameMap, PersonNameParts> dataMapperImpl_2 = new DataMapperBuilderImpl<>(PersonNameMap.class,
				PersonNameParts.class).includeFields(includeField).build();

		PersonNameMap src = new PersonNameMap(nameMap);
		PersonNameParts dest = dataMapperImpl_2.map(src);

		assertEquals("Leornado", dest.getFirstName());
		assertEquals("DiCaprio", dest.getLastName());
	}

	@Test
	public void givenSrcWithNullField_whenMapsThenCorrect() {

		DataMapper<SourceModel, DestinationModel> dataMapperImp_3 = new DataMapperBuilderImpl<>(SourceModel.class,
				DestinationModel.class).build();

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = dataMapperImp_3.map(src);

		assertEquals(src.getAge(), dest.getAge());
		assertEquals(src.getName(), dest.getName());
	}

	@Test
	public void givenSrcWithNullAndClassMapConfigNoNull_whenFailsToMap_thenCorrect() {
		DataMapper<SourceModel, DestinationModel> dataMapperImpl_4 = new DataMapperBuilderImpl<>(SourceModel.class,
				DestinationModel.class).build();

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);

		dataMapperImpl_4.map(src, dest);

		assertEquals(src.getAge(), dest.getAge());
		assertNull(dest.getName());

	}

	@Test
	public void givenSrcWithNullAndClassMapConfigNull_whenFailsToMap_thenCorrect() {

		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);

		DataMapper<SourceModel, DestinationModel> dataMapperImpl_5 = new DataMapperBuilderImpl<>(SourceModel.class,
				DestinationModel.class).mapNulls(true).build();

		dataMapperImpl_5.map(src, dest);

		assertEquals(src.getAge(), dest.getAge());
		assertNull(dest.getName());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsConfigNull_thenCorrect() {

		/*List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nom", "name", false),
				new IncludeDataField("surnom", "nickName", true));*/

		Personne french = new Personne(null, null, 2);
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

		/*DataMapper<Personne, Person2> dataMapperImpl_6 = personnePerson2Builder.includeFields(includeField).build();*/

		personneToPersonDestinationMapper2.map(french, english);

		assertEquals("Mosip", english.getName());
		assertNull(english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsConfigNoNullIncludingField_thenCorrect() {

		/*List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nom", "name", true),
				new IncludeDataField("surnom", "nickName", true));*/

		Personne french = new Personne(null, "cla", 2);
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

		/*DataMapper<Personne, Person2> dataMapperImpl_7 = personnePerson2Builder.includeFields(includeField)
				.mapNulls(false).build();*/

		personneToPersonDestinationMapper3.map(french, english);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsExlcudingField_thenCorrect() {

		/*List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));*/

		Personne french = new Personne(null, "cla", 2);
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

		/*DataMapper<Personne, Person2> dataMapperImpl_8 = personnePerson2Builder.includeFields(includeField)
				.includeFields(includeField).excludeFields(excludeField).mapNulls(false).build();*/

		personneToPersonDestinationMapper4.map(french, english);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAndDestWithDiffFieldNames_whenMapsNullExlcudingField_thenCorrect() {

		/*List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", false));
		List<String> excludeField = Arrays.asList("nom");*/
		Personne french = new Personne(null, "cla", 2);
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

		/*DataMapper<Personne, Person2> dataMapperImpl_9 = personnePerson2Builder.includeFields(includeField)
				.includeFields(includeField).excludeFields(excludeField).build();*/

		personneToPersonDestinationMapper5.map(french, english);

		assertEquals("Mosip", english.getName());
		assertEquals(french.getSurnom(), english.getNickName());
		assertEquals(french.getAge(), english.getAge());
	}

	@Test
	public void givenSrcAsNullDestWithFieldNames_whenMapsExlcudingField_thenFails() {

		/*List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));*/

		Personne french = null;
		PersonDestination english = new PersonDestination("Mosip", "Project", 10);

	/*	DataMapper<Personne, Person2> dataMapperImpl_10 = personnePerson2Builder.includeFields(includeField)
				.includeFields(includeField).excludeFields(excludeField).build();*/

		personneToPersonDestinationMapper6.map(french, english);
		assertEquals("Mosip", english.getName());
	}

	@Test(expected = DataMapperException.class)
	public void givenSrcWithFieldDestAsNull_whenMapsExlcudingField_thenFails() {

		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));

		Personne french = new Personne(null, "cla", 2);
		Person english = null;

		DataMapper<Personne, Person> dataMapperImpl_11 = new DataMapperBuilderImpl<>(Personne.class, Person.class)
				.includeFields(includeField).includeFields(includeField).excludeFields(excludeField).build();

		dataMapperImpl_11.map(french, english);
		assertNull(english);
	}

	@Test(expected = DataMapperException.class)
	public void givenSrcAndDestAsInterface_whenMaps_thenFails() {

		Personne french = new Personne("Claire", "cla", 2);
		DataMapper<Personne, PersonInterface> dataMapperImpl_12 = new DataMapperBuilderImpl<>(Personne.class,
				PersonInterface.class).build();
		dataMapperImpl_12.map(french);
	}

}
