## kernel-batch-server
This folder has batch server module based on spring cloud local dataflow server which has a UI interface which can be used to monitor batch jobs that are register and executing on the batch server.
 
 1- [Background & Design](../../design/kernel/kernel-batch-framework.md)
 

 2- [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
####Properties to be modified before starting server

 [kernel-batch-server-dev.properties](../../config/kernel-batch-server-dev.properties)
 

###Uses-

Batch jobs can be register and launched using UI interface or by using batch framework module of kernel,the jobs will start apperaing on UI dashboard.Uri of batch job executing jars need to be provided to register a job on server.
For example-

```
maven://groupId:artifactId:jar:1.0.0-SNAPSHOT

```
The batch jobs must contain **@EnableTask** annotation so that it can be executed by server.

 








