## ID Authentication
ID Authentication (IDA) is the authentication project used to authenticate Individuals using their UIN/VID. Below are the various authentication types supported by MOSIP - 
1. OTP Auth
2. Demographic Auth
3. Biometric Auth (includes Fingerprint, IRIS and Face)
4. Static Pin Auth

### Project Structure
**authentication-** This is the parent project with common dependencies and plugins. This project has below sub-modules - 
- `core` - This module named as `auth-core` defines all the core utilities, SPIs, exceptions and constants required for authentication service
- `service` - This module named as `auth-service` contains all the auth services to be used to authenticate an Individual

### Build Steps
Below commands should be run in the parent project **authentication**
1. `mvn clean install`
2. `mvn jacoco:report`
3. `mvn sonar:sonar -PDEV` 
