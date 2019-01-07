package io.mosip.registration.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.json.demo.Address;
import io.mosip.registration.dto.json.demo.Demographic;
import io.mosip.registration.dto.json.demo.DemographicInfo;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;

/**
 * Class for Orika Object Mapper
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class CustomObjectMapper extends ConfigurableMapper {

	/**
	 * Instance of MapperFacade for object mapping
	 */
	public static final MapperFacade MAPPER_FACADE = new CustomObjectMapper();

	/**
	 * Method to configure the Orika Mapper for object conversions
	 * 
	 * @param mapperFactory the Orika MapperFactory
	 */
	@Override
	public void configure(MapperFactory mapperFactory) {

		ConverterFactory converterFactory = mapperFactory.getConverterFactory();
		converterFactory.registerConverter(new PassThroughConverter(LocalDateTime.class));
		converterFactory.registerConverter(new PassThroughConverter(OffsetDateTime.class));
		converterFactory.registerConverter("packetMetaInfo", new PacketMetaInfoConverter());

		mapperFactory.classMap(DemographicInfoDTO.class, DemographicInfo.class).byDefault().register();

		mapperFactory.classMap(AddressDTO.class, Address.class).byDefault().register();

		mapperFactory.classMap(DemographicDTO.class, Demographic.class).exclude("applicantDocumentDTO")
				.exclude("introducerRID").exclude("introducerUIN").byDefault().register();
	}

}
