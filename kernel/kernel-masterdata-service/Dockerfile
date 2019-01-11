FROM openjdk:8

#ARG active_profile

#ARG host_name

ENV active_profile_env=default

ENV host_name_env=localhost

ADD kernel-masterdata-service-1.0.0-SNAPSHOT.jar kernel-masterdata-service.jar

EXPOSE 8086
EXPOSE 8093

CMD ["java","-XX:+UnlockExperimentalVMOptions","-XX:MaxMetaspaceSize=500M","-XX:+UseCGroupMemoryLimitForHeap", "-XX:MaxRAMFraction=1","-Dcom.sun.management.jmxremote.rmi.port=8093","-Dcom.sun.management.jmxremote=true" ,"-Dcom.sun.management.jmxremote.port=8093" ,"-Dcom.sun.management.jmxremote.ssl=false","-Dcom.sun.management.jmxremote.authenticate=false","-Dcom.sun.management.jmxremote.local.only=false","-jar","-Djava.rmi.server.hostname=104.211.214.143","-Dspring.profiles.active=${active_profile_env}","kernel-masterdata-service.jar"]