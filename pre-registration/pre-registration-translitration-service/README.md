# Pre-Registration-translitration-service:

[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-transliteration-service.md)

This service is used by Pre-Registration portal to transliterate given value from one language to another language. In this API transliteration is using IDB ICU4J library , so accuracy will be less.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#transliteration-service-public)

### Default Port and Context Path
```
server.port=9098
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9098/preregistration/v1/transliteration/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.

1. POST /transliteration/transliterate
This request is used to transliterate one value to another based on given valid language code.
