## RID generator module for kernel
This folder has RID generator module which can be used to generate RID as numeric string based on the centerId and DongleId provided.

 [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
### The inputs which have to be provided are:
1.CenterId of the registration center as string of size  metion in property.

2.DongleId of the device as string of size  metion in property.

   For example: centerId="32345" and dongleId="56789".

####Properties to be added in parent Spring Application environment

 [kernel-idgenerator-rid-dev.properties](../../config/kernel-idgenerator-rid-dev.properties)
 
 
**The response will be numeric string of desire size with centerId,dongleId,five digit sequence generated numbers and timestamp in format "yyyymmddhhmmss" of 14 digits.**

####Usage Sample:
Autowired interface RidGenerator and call the method generateId(centerId,machineId).

For example-

```
@Autowired

RidGenerator <String> ridGenerator;


String rid=ridGenerator.generateId("34532","67897");

System.out.println("GENERATED RID="+rid);

```

**OUTPUT:**

GENERATED RID=34532678970000120181122173040
 




