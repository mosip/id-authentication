# TSPID Generator

#### Background

A TSP ID can be generated for each TSP. There are configurations during the TSP ID generations such as only numeric, length, restricted numbers etc, 

#### Solution



**The key solution considerations are**


- An unique number have to be generated for each request.

- The counter have to be maintained in a persistent storage, so that when next request comes, this counter is incremented for the next generated number


**Module diagram**



![Module Diagram](_images/kernel-TSPIDGenerator.jpg)



## Implementation


**kernel-tokenidgenerator** [README](../../kernel/kernel-idgenerator-tspid/README.md)
