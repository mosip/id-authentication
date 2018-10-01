## Parent project of Kernel.
This folder has all submodules of kernel of Mosip.

### Sonar
1. `mvn clean install`

2. `mvn jacoco:report`

3. For local: `mvn sonar:sonar -PLOCAL`  (sonar server should be running locally on localhost:9000)

   For dev: `mvn sonar:sonar -PDEV`