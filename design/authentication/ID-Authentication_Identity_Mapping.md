### ID-Authentication Identity Mapping
A country can customize [MOSIP ID Schema](https://github.com/mosip/mosip-config/blob/master/config-templates/mosip-identity-json-schema-env.json). ID Authentication Services maps ID attributes used in the Authentication requests with ID attributes to Platform Identity attributes configured by a country using mapping json - [Identity mapping configuration JSON file](https://github.com/mosip/mosip-config/blob/master/config-templates/id-authentication-mapping.json) 

For example:
````
{
	"ida-mapping": {
		"name": [
			"fullName"
		],
		"dob": [
			"dateOfBirth"
		],
		"age": [
			"dateOfBirth"
		],
		"fullAddress": [
			"addressLine1",
			"addressLine2",
			"addressLine3",
			"city",
			"region",
			"province",
			"postalCode"
		],
		"fingerprint": [
			"CBEFF"
		]
}
````

Below are the considerations for maintaining Idenity Mapping configuration file:

1. Left hand side ID attributes (such as `name`, `dob`) are fixed. They are not subject to be changed as part of a configuration change.
2. Right hand side values represent mapping Platform ID attributes that can be modified as part of a configuration change. 
For example, Authentication Service uses `name` as the request attribute, which is mapped to `fullName` of Platform ID Attribute.

**Note** - Any additional attributes required for Authentication Services will require code change.
