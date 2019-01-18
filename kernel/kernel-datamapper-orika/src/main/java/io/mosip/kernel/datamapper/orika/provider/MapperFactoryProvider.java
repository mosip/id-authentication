package io.mosip.kernel.datamapper.orika.provider;

import lombok.Getter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * MapperFactory provider for DataMapper
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class MapperFactoryProvider  {
	
	/**
	 * Constructor for this class
	 */
	private MapperFactoryProvider() {
		
	}

	/**
	 * Default {@link MapperFactory} instance
	 */
	@Getter
	private static DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();


}
