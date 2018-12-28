package io.mosip.preregistration.transliteration.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateTransliterationRequest implements Serializable{
	
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
