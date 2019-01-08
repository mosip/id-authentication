FROM openjdk:8

ARG active_profile
ARG host_name
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/kernel-masterdata-service-*.jar kernel-masterdata-service.jar

EXPOSE 8086


CMD ["java","-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:MaxRAMFraction=1","-Dcom.sun.management.jmxremote.rmi.port=8093","-Dcom.sun.management.jmxremote=true" ,"-Dcom.sun.management.jmxremote.port=8093" ,"-Dcom.sun.management.jmxremote.ssl=false","-Dcom.sun.management.jmxremote.authenticate=false","-Dcom.sun.management.jmxremote.local.only=false","-Djava.rmi.server.hostname=${host_name}","-jar","-Dspring.profiles.active=${active_profile_env}","kernel-masterdata-service.jar"]
