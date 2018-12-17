# RID Generator

#### Background

The Registration client module needs to generate a RID and assign it to an individual during the Registration process. The Registration client can go in offline mode also. 

#### Solution



**The key solution considerations are**


- There cannot be any duplicate numbers generated.


- The registration client should be able to generate a unique number even if the Registration client goes to offline mode. 


- The configurations of the RID is injected to the RID generator module. 


- The RID generator is included in the Registration client module as Java Jar file. 



**Module diagram**



![Module Diagram](_images/kernel-RIDGenerator.jpg)



## Implementation


**kernel-ridgenerator** [README](../../kernel/kernel-idgenerator-rid/README.md)
