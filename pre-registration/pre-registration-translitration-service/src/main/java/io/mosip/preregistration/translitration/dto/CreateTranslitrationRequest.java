package io.mosip.preregistration.translitration.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateTranslitrationRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6675771212299312546L;
	
	String fromFieldName;
	
	String fromFieldValue;
	
	String fromFieldLang;
	
	String toFieldName;
	
	String toFieldValue;
	
	String toFieldLang;
	

}
