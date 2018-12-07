# UIN Generator

#### Background

The Registration processor module needs to generate a UIN and assign it to an individual during the end of the process. The UIN Generator micro service responds back with an unique number whenever requested. 

#### Solution



**The key solution considerations are**


- There cannot be any duplicate numbers generated.


- A pool of UIN should be maintained to serve the Registration processor module, so that the Registration Processor module doesn't spend much time in the UIN generation. 


- The configurations should be caliberated for the needs of each country. For example, let's say that we expect a 50,000 user registrations per day, the already generated UIN in the pool should be 50,000



**Module diagram**



![Module Diagram](_images/kernel-datavalidator-cd.png)



## Implementation


**kernel-datavalidator** [README](../../kernel/kernel-idgenerator-uin/README.md)
