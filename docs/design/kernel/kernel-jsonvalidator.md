# Json Validator:

## Background

This library can be used to validate JSON against Schema. It contains a method that accept JSON String and Schema File Name.

The schema can be taken either from Config Server or from Local resource location.
1. To get the schema from local, set key 'property.source' in you property file as 'LOCAL'
2. To get the schema from spring cloud config server, set 'property.source' in your property file as 'CONFIG_SERVER'


## Solution:

**The key solution considerations are**


- Create an interface JsonValidator having required method declaration to validate Json input, which will be exposed to the other applications.


- Create a project which implements JsonValidator based on any opensource json-schema-validator implementing Draft v7 specifications.


- JsonValidator can be used in any MOSIP module to validate after adding its implementation to their class path.


**Class Diagram:**



![kernel_jsonvalidator_classdiagram](_images/kernel-jsonvalidator-cd.png)




## Implementation


**kernel-jsonvalidator** [README](../../../kernel/kernel-jsonvalidator/README.md)
