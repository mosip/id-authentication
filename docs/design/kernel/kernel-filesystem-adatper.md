# File system adapter

#### Background

MOSIP platform can have 'n' number of file system. For example, the file system can be either HDFS, CEPH etc., The implementor of the platform can decide to switch to any platform during the implementation.

#### Solution



**The key solution considerations are**


- A generic interface has to defined, through which the MOSIP modules have to use when using the using the file system. 

- In future, if a component have to use another File System, the new File System should be pluggable. 

- Create a File System for the HDFS for the current implementation. 



**Class diagram**

![Class Diagram](_images/kernel-filesystem-adatper.jpg)


## Implementation


**kernel-kernel-filesystem-adatper** [README](../../kernel/kernel-filesystem-adatper/README.md)