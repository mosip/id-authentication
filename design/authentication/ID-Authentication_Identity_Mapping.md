### ID-Authentication Identity Mapping
ID Authentication Services uses a country specific [Identity mapping configuration JSON file](https://github.com/mosip/mosip-config-mt/blob/master/config/id-authentication-mapping.json) to map ID attributes used in the Authentication requests to the Platform Identity attributes.

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

1. The left hand side attributes (such as `name`, `dob`) are fixed. They are not subject to be changed as part of a configuration change.
2. The right hand side values can be modified as part of a configuration change.
3. Any additional attributes required for demo-auth will require code change.
