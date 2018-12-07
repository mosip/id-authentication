﻿# PRID Generator

#### Background

The Pre-registration application needs to display an acknowledgement number when somebody pre-registers for an enrolment.  

#### Solution



**The key solution considerations are**


- There cannot be any duplicate numbers generated.


- The configurations of the PRID is injected to the PRID generator module by the Pre-Registration module. 


- The PRID generator is included in the Pre-Registration client module as Java Jar file. 



**Module diagram**



![Module Diagram](_images/kernel-PRIDGenerator.jpg)



## Implementation


**kernel-ridgenerator** [README](../../kernel/kernel-idgenerator-rid/README.md)
