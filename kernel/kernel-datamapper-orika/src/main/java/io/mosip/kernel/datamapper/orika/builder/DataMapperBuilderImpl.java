package io.mosip.kernel.datamapper.orika.builder;

import java.util.List;

import io.mosip.kernel.core.datamapper.model.IncludeDataField;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.core.datamapper.spi.DataMapperBuilder;
import io.mosip.kernel.datamapper.orika.impl.DataMapperImpl;


/**
 * DataMapper Builder implementation for configuring {@link DataMapperImpl} with configurations
 * {@link #mapNulls}  {@link #byDefault}  {@link #sourceClass} 
 * {@link #destinationClass} {@link #includeFields} {@link #excludeFields}
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class DataMapperBuilderImpl<S,D> implements DataMapperBuilder<S, D>{
   
	/**
	 * Configure map null in mapping
	 */
	private boolean mapNulls=true;
	/**
	 * Configure byDefault in mapping
	 */
	private boolean byDefault=true;
	/**
	 * Configure source class in mapping
	 */
	private Class<S> sourceClass; 
	/**
	 * Configure destination class in mapping
	 */
	private Class<D> destinationClass; 
	/**
	 * Configure included field in mapping
	 */
	private List<IncludeDataField> includeFields=null;
	/**
	 * Configure excluded fields in mapping
	 */
	private List<String> excludeFields=null;
	
	public DataMapperBuilderImpl(Class<S> sourceClass, Class<D> destinationClass) {
		this.sourceClass = sourceClass;
		this.destinationClass = destinationClass;
		
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataMapperBuilder#mapNulls(boolean)
	 */
	@Override
	public DataMapperBuilder<S, D> mapNulls(boolean mapNulls) {
		this.mapNulls=mapNulls;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataMapperBuilder#byDefault(boolean)
	 */
	@Override
	public DataMapperBuilder<S, D> byDefault(boolean byDefault) {
		this.byDefault=byDefault;
		return this;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataMapperBuilder#includeFields(java.util.List)
	 */
	@Override
	public DataMapperBuilder<S, D> includeFields(List<IncludeDataField> includeFields) {
		this.includeFields=includeFields;
		return this;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataMapperBuilder#excludeFields(java.util.List)
	 */
	@Override
	public DataMapperBuilder<S, D> excludeFields(List<String> excludeFields) {
	   this.excludeFields=excludeFields;
	   return this;
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataMapperBuilder#build()
	 */
	@Override
	public DataMapper<S, D> build() {
		return new DataMapperImpl<>(sourceClass, destinationClass,mapNulls,byDefault,includeFields,excludeFields);
	}

	

	
	
}