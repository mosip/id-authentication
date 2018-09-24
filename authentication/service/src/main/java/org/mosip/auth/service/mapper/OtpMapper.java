package org.mosip.auth.service.mapper;

import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class OtpMapper {
	private static MapperFacade mapperFacade = null;

	MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

//	public OtpMapper() {
//		mapperFactory.classMap(OtpRequestDTO.class, org.mosip.auth.service.entity.OtpEntity.class).field("uniqueID", "uniqueID").byDefault()
//				.register();
//		mapperFacade = mapperFactory.getMapperFacade();
//	}

}
