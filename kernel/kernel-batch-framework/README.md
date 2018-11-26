## kernel-batch-framework

 This folder has batch framework module which can be used to register and launch batch jobs on kernel batch server .
 
 1- [Background & Design](../../design/kernel/kernel-batch-framework.md)
 

 2- [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
###Properties to be modified before starting server and framework

 [kernel-batch-framework-dev.properties](../../config/kernel-batch-framework-dev.properties)
 
 
###Uses-

 The batch jobs uri must be in the format of **task.JOBNAME:maven://groupId:artifactId:jar:exec:1.0.0-SNAPSHOT**
 and separated by "," in the property file.
 Once executed it will register all the jobs in batch server where jobs can be monitor.







