# VID Generator

#### Background

A virtual ID can be requested by an Indivudual against his UIN. A library should be able to generate an unique ID and assign it against a UIN. When a request comes to retrieve a virtual ID agains UIN, the system should return a mapping, if already exists. Otherwise, a mapping should be created and returned.  

#### Solution



**The key solution considerations are**


- There cannot be any duplicate numbers generated. A database is maintained to ensure the uniqueness by unique constraint defintion for that column.

- The configurations are injected by the caller of the module. These configurations are defined in the config server. 


**Module diagram**



![Module Diagram](_images/kernel-VIDGenerator.jpg)



## Implementation


**kernel-vidgenerator** [README](../../kernel/kernel-idgenerator-vid/README.md)
