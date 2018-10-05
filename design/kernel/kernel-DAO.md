
# MOSIP Modules Components

![Modules Components](_images/MOSIP_modules_components.png)



## Data Access Objects layer (DAO layer)
 
Background
Almost every application in the MOSIP have to use the DAO layer. There can be multiple databases and multiple DAO framework implementation can be used in various applications in the MOSIP platform. In future, if one the country can decide to use another database or DAO framework implementation. This decision should not impact the existing code base except minimal configuration file changes. 
Solution
The key solution considerations are
-	The Service Provider Interface design pattern have to be used here. 
-	Create a set of interfaces in the Kernel’s DOA framework which will be used by the other applications in the MOSIP platform. 
-	The caller uses these interfaces for their API. 
-	The caller can decide which implementation for the DAO framework by editing the dependency in the POM file. 
-	The interfaces must use the APIs from the Java Persistence API. So that it will be compatible with all future and current ORM tools. 


Diagram: Sequence diagram


