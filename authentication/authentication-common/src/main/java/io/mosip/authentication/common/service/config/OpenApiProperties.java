package io.mosip.authentication.common.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "openapi")
@Data
public class OpenApiProperties {
    private InfoProperty info;
    private Service service;
    private Group group;
}

@Data
class InfoProperty {
    private String title;
    private String description;
    private String version;
    private LicenseProperty license;
}

@Data
class LicenseProperty {
    private String name;
    private String url;
}

@Data
class Service {
    private List<Server> servers;
}

@Data
class Server {
    private String description;
    private String url;
}

@Data
class Group {
    private String name;
    private List<String> paths;
}
