package io.mosip.preregistration.transliteration.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransliterationDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6675771212299312546L;
	
	@JsonProperty("from_field_name")
	@ApiModelProperty(value = "From Field Name", position = 1)
	String fromFieldName;
	
	@JsonProperty("from_field_value")
	@ApiModelProperty(value = "From Field Value", position = 2)
	String fromFieldValue;
	
	@JsonProperty("from_field_lang")
	@ApiModelProperty(value = "From Field Language", position = 3)
	String fromFieldLang;
	
	@JsonProperty("to_field_name")
	@ApiModelProperty(value = "To Field Name", position = 4)
	String toFieldName;
	
	@JsonProperty("to_field_value")
	@ApiModelProperty(value = "To Field Vaue", position = 5)
	String toFieldValue;
	
	@JsonProperty("to_field_lang")
	@ApiModelProperty(value = "To Field Language", position = 6)
	String toFieldLang;
}
