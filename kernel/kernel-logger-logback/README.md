## kernel-logger-logback
This folder has kernel-logger-logback module which can be used for logging functionalities.

[Background & Design](../../design/kernel/kernel-logger.md)

**Api Documentation**
[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```

**The flow to be followed is:**
  
  Flow 1:

1. Create an appender's object and provide configuration 
2. Pass that object and class name in *Logfactory* to get logger instance.

  Flow 2:
  
1. Create an XML file and provide configuration 
2. Pass that file and class name in *Logfactory* to get logger instance.  

**All the supported logging systems can have the logger levels set in the Spring Environment (for example, in application.properties) by using logging.level.<logger-name> = level where level is one of *TRACE, DEBUG, INFO, WARN, ERROR, FATAL, or OFF*.**

**The root log level can be configured by using**  

```
logging.level.root=debug
```
**For any application log level can be configured by using logging.level.<logger-name> = level, For Example**

```
logging.level.io.mosip.authentication.service.*=debug 
```

**If there is any error which occurs while configuration in log factory, it will be thrown as Exception.** 

**Exceptions to be handled while using this functionality:**
1. ClassNameNotFoundException
2. EmptyPatternException
3. FileNameNotProvided
4. ImplementationNotFound
5. XMLConfigurationParseException
6. PatternSyntaxException
7. IllegalStateException
8. IllegalArgumentException

**Usage Sample**

  *Usage 1:*
 
 ```
RollingFileAppender rollingFileAppender = new RollingFileAppender();
       rollingFileAppender.setAppenderName("kernelrollingfileappender");
		rollingFileAppender.setAppend(true);
		rollingFileAppender.setFileName("/kernel-logs.log");
		rollingFileAppender.setImmediateFlush(true);
		rollingFileAppender.setPrudent(false);
		rollingFileAppender.setFileNamePattern("/kernel-logs-%d{ss}-%i.log");
		rollingFileAppender.setMaxHistory(5);
		rollingFileAppender.setTotalCap("100KB");
		rollingFileAppender.setMaxFileSize("10kb");
		
Logger logger=Logfactory.getDefaultRollingFileLogger(rollingFileAppender, Kernel.class);
       
       logger.error(sessionId,idType,id,description);
       logger.debug(sessionId,idType,id,description);
       logger.warn(sessionId,idType,id,description);
       logger.info(sessionId,idType,id,description);
       logger.trace(sessionId,idType,id,description); 		
 
 ```

 *Usage 2:*
 
 ```
 <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<rollingFileAppender
	appenderName="fileappenderRollingFile">
	<append>true</append>
	<fileName>/kernel-logs</fileName>
	<immediateFlush>true</immediateFlush>
	<prudent>false</prudent>
	<fileNamePattern>/kernel-logs-%d{ss}-%i.log</fileNamePattern>
	<maxFileSize>1kb</maxFileSize>
	<maxHistory>3</maxHistory>
	<totalCap>10mb</totalCap>
</rollingFileAppender>
 ```
 
 ```
Logger logger= Logfactory.getDefaultRollingFileLogger(rollingFileAppenderXMLFile,Kernel.class); 
       logger.error(sessionId,idType,id,description);
       logger.debug(sessionId,idType,id,description);
       logger.warn(sessionId,idType,id,description);
       logger.info(sessionId,idType,id,description);
       logger.trace(sessionId,idType,id,description); 		
    
 ```

