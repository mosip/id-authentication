package io.mosip.kernel.datamapper.orika.test;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.datamapper.model.IncludeDataField;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.datamapper.orika.builder.DataMapperBuilderImpl;
import io.mosip.kernel.datamapper.orika.test.model.PersonDestination;
import io.mosip.kernel.datamapper.orika.test.model.Personne;

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
}
