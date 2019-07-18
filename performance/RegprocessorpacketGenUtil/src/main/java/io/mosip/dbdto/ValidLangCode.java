package io.mosip.dbdto;

import javax.validation.Payload;

public @interface ValidLangCode {

	String message() default "Language code not supported";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
}
