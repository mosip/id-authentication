# Pre-Registration-Generate-QRcode-service:

[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-generate-qr-code-service.md)

This service details is use by Pre-Registration portal to generate QR Code.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#generate-qr-code-service-public)

### Default Port and Context Path
```
server.port=9091
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9091/preregistration/v1/qrCode/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.

1. POST /generate -This request is used to generate QR Code for the pre-registration acknowledgement.