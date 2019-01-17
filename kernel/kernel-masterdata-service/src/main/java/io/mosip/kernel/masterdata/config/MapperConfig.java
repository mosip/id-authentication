package io.mosip.kernel.masterdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.datamapper.orika.builder.DataMapperBuilderImpl;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.WordAndLanguageCodeID;

@Configuration
public class MapperConfig {

	@Bean(name="applicationtoToApplicationDtoDefaultMapper")
	public DataMapper<Application, ApplicationDto> applicationtoToApplicationDtoMapper(){
		return new DataMapperBuilderImpl<>(Application.class, ApplicationDto.class).build();
	}
	
	@Bean(name="biometricTypeTobiometricTypeDtoDefaultMapper")
	public DataMapper<BiometricType, BiometricTypeDto> biometricTypeTobiometricTypeDtoDefaultMapper(){
		return new DataMapperBuilderImpl<>(BiometricType.class, BiometricTypeDto.class).build();
	}
	
	@Bean(name="biometricTypeToCodeandlanguagecodeDefaultMapper")
	public DataMapper<BiometricType, CodeAndLanguageCodeID> biometricTypeToCodeandlanguagecodeDefaultMapper(){
		return new DataMapperBuilderImpl<>(BiometricType.class, CodeAndLanguageCodeID.class).build();
	}
	
	@Bean(name="blacklistedWordsToWordAndLanguageCodeIDDefaultMapper")
	public DataMapper<BlacklistedWords, WordAndLanguageCodeID> blacklistedWordsToWordAndLanguageCodeIDDefaultMapper(){
		return new DataMapperBuilderImpl<>(BlacklistedWords.class, WordAndLanguageCodeID.class).build();
	}
	
	@Bean(name="applicationDtoToApplicationDefaultMapper")
	public DataMapper<ApplicationDto,Application> applicationDtoToApplicationDefaultMapper(){
		return new DataMapperBuilderImpl<>(ApplicationDto.class, Application.class).build();
	}
	
	@Bean(name="applicationToCodeandlanguagecodeDefaultMapper")
	public DataMapper<Application, CodeAndLanguageCodeID> applicationToCodeandlanguagecodeDefaultMapper(){
		return new DataMapperBuilderImpl<>(Application.class, CodeAndLanguageCodeID.class).build();
	}
	
}
