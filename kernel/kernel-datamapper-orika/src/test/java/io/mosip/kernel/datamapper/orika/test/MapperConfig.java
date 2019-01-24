package io.mosip.kernel.datamapper.orika.test;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

@Configuration
public class MapperConfig {

	@Bean(name = "personneToPersonDestinationMapper")
	public DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper() {
		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));

		return new DataMapperBuilderImpl<>(Personne.class, PersonDestination.class).includeFields(includeField)
				.excludeFields(excludeField).build();
	}

	@Bean(name = "personneToPersonDestinationMapper2")
	public DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper2() {
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nom", "name", false),
				new IncludeDataField("surnom", "nickName", true));
		return new DataMapperBuilderImpl<>(Personne.class, PersonDestination.class).includeFields(includeField).build();
	}

	@Bean(name = "personneToPersonDestinationMapper3")
	public DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper3() {
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nom", "name", true),
				new IncludeDataField("surnom", "nickName", true));
		return new DataMapperBuilderImpl<>(Personne.class, PersonDestination.class).includeFields(includeField)
				.mapNulls(false).build();
	}

	@Bean(name = "personneToPersonDestinationMapper4")
	public DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper4() {
		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));
		return new DataMapperBuilderImpl<>(Personne.class, PersonDestination.class).includeFields(includeField)
				.includeFields(includeField).excludeFields(excludeField).mapNulls(false).build();
	}

	@Bean(name = "personneToPersonDestinationMapper5")
	public DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper5() {
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", false));
		List<String> excludeField = Arrays.asList("nom");
		return new DataMapperBuilderImpl<>(Personne.class, PersonDestination.class).includeFields(includeField)
				.excludeFields(excludeField).build();
	}

	@Bean(name = "personneToPersonDestinationMapper6")
	public DataMapper<Personne, PersonDestination> personneToPersonDestinationMapper6() {
		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));

		return new DataMapperBuilderImpl<>(Personne.class, PersonDestination.class).includeFields(includeField)
				.includeFields(includeField).excludeFields(excludeField).build();
	}

	@Bean(name = "personNameListToPersonNamePartsMapper")
	public DataMapper<PersonNameList, PersonNameParts> personNameListToPersonNamePartsMapper() {
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nameList[0]", "firstName", true),
				new IncludeDataField("nameList[1]", "lastName", true));

		return new DataMapperBuilderImpl<>(PersonNameList.class, PersonNameParts.class).includeFields(includeField)
				.build();
	}

	@Bean(name = "personNameMapToPersonNamePartsMapper")
	DataMapper<PersonNameMap, PersonNameParts> personNameMapToPersonNamePartsMapper() {
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("nameMap['first']", "firstName", true),
				new IncludeDataField("nameMap[\"last\"]", "lastName", true));

		return new DataMapperBuilderImpl<>(PersonNameMap.class, PersonNameParts.class).includeFields(includeField)
				.build();
	}

	@Bean(name = "sourceModelToDestinationModelMapper")
	DataMapper<SourceModel, DestinationModel> sourceModelToDestinationModelMapper() {
		return new DataMapperBuilderImpl<>(SourceModel.class, DestinationModel.class).build();
	}
	
	@Bean(name = "sourceModelToDestinationModelMapper2")
	DataMapper<SourceModel, DestinationModel> sourceModelToDestinationModelMapper2() {
		return new DataMapperBuilderImpl<>(SourceModel.class,
				DestinationModel.class).mapNulls(true).build();
	}
	
	@Bean(name = "personneToPersonMapper")
	DataMapper<Personne, Person> personneToPersonMapper() {
		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));
		return new DataMapperBuilderImpl<>(Personne.class, Person.class)
				.includeFields(includeField).includeFields(includeField).excludeFields(excludeField).build();
	}
	
	@Bean(name = "personneToPersonInterface")
	DataMapper<Personne, PersonInterface> personneToPersonInterface() {
		return new DataMapperBuilderImpl<>(Personne.class,
				PersonInterface.class).build();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean(name = "personListToPersonneListMapper")
	DataMapper<List<Person>, List<Personne>> personListToPersonneListMapper() {
		Class<List<Person>> personClass = (Class) List.class;
		Class<List<Personne>> personneClass = (Class) List.class;
		return new DataMapperBuilderImpl<>(personClass, personneClass).build();
	}
}
