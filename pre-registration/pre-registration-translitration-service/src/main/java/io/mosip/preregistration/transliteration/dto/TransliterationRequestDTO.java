/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class defines the variables to accept the input parameter from
 * request.
 * 
 * @author Tapaswini Behera
 * @since 1.0.0
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransliterationRequestDTO implements Serializable{
	
	
	private static final long serialVersionUID = -5601709941859093678L;

	/** The from-field-value */
	@JsonProperty("from_field_value")
	@ApiModelProperty(value = "From Field Value", position = 2)
	String fromFieldValue;
	
	/** The from-field-lang */
	@JsonProperty("from_field_lang")
	@ApiModelProperty(value = "From Field Language", position = 3)
	String fromFieldLang;
	
	
	/** The to-field-lang */
	@JsonProperty("to_field_lang")
	@ApiModelProperty(value = "To Field Language", position = 6)
	String toFieldLang;
}
