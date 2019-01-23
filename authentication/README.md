## ID Authentication
ID Authentication (IDA) is the authentication module of MOSIP, used to authenticate Individuals using their UIN/VID. 

List of authentication types supported by MOSIP are - 
1. OTP Auth
2. Demographic Auth
3. Biometric Auth (includes Fingerprint, IRIS and Face)
4. Static Pin Auth

Refer wiki page for [ID Authentication API](https://github.com/mosip/mosip/wiki/ID-Authentication-APIs)   

### Project Structure
**authentication-** This is the parent project with common dependencies and plugins. This project has below sub-modules - 
- `core` - This module named as `auth-core` defines all the core utilities, SPIs, exceptions and constants required for authentication service
- `service` - This module named as `auth-service` contains all the auth services to be used to authenticate an Individual

### Build Steps
Below commands should be run in the parent project **authentication**
1. `mvn clean install`
2. `mvn sonar:sonar -PDEV` 
