# Machine ID Generator

#### Background

A Machine ID can be generated for various machines in the MOSIP platform. Once a unique number is generated, this Machine ID is assigned against individual machines. 

#### Solution



**The key solution considerations are**


- An unique number have to be generated for each request.

- The various configurations such as the starting number, incremental value, restricted numbers etc., are retrieved from the config server and injected to the library. 


**Module diagram**



![Module Diagram](_images/kernel-MachineIDGenerator.jpg)



## Implementation


**kernel-machineidgenerator** [README](../../kernel/kernel-idgenerator-machineid/README.md)
