package io.mosip.resident.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 
 * @author Girish Yarru
 * @version 1.0
 *
 */
public class ResidentParameterizedTypeImpl implements ParameterizedType {
	private ParameterizedType parameterizedType;
	private Type[] responseType;

	ResidentParameterizedTypeImpl(ParameterizedType parameterizedType, Type[] responseType) {
		this.parameterizedType = parameterizedType;
		this.responseType = responseType;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return responseType;
	}

	@Override
	public Type getRawType() {
		return parameterizedType.getRawType();
	}

	@Override
	public Type getOwnerType() {
		return parameterizedType.getOwnerType();
	}

}