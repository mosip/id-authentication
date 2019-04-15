# Pre-Registration-translitration-service:

[Background & Design](pre-registration-individual.md)

This service is used by Pre-Registration portal to transliterate given value from one language to another language. In this API transliteration is using IDB ICU4J library , so accuracy will be less.



#### Api Documentation

```
mvn javadoc:javadoc

```

#### POST Operation
#### Path - `transliteration/transliterate`
#### Summary

This request is used to transliterate from_Field_value to to_field_value based on given valid from_lang_code to to_lang_code.



#### Request part Parameters

1. id
2. version
3. requestTime
4. request
5. request.from_field_lang	
6. request.from_field_value		
7. request.to_field_lang		

#### Response

This request returns from_field_lang, from_field_value, to_field_lang, to_field_value on success else gives a error message.
