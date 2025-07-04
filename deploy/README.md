# IDA module

## Deployment in K8 cluster with other MOSIP services:
### Pre-requisites
* Set KUBECONFIG variable to point to existing K8 cluster kubeconfig file:
    ```
    export KUBECONFIG=~/.kube/<k8s-cluster.config>
    ```
### Install Admin module
 ```
    cd deploy/ida
    $ ./install.sh
   ```
### Delete
  ```
    cd deploy/ida
    $ ./delete.sh
   ```
### Restart
  ```
    cd deploy/ida
    $ ./restart.sh
   ```
### Install Keycloak client
  ```
    cd deploy/keycloak
    $ ./keycloak_init.sh
   ```

* Run the Installer Script
  ```
  cd deploy/apitest-auth
  ./install.sh
  ```
Note:
* Script prompts for below mentioned inputs please provide as and when needed:
  * Enter the time (hr) to run the cronjob every day (0–23): Specify the hour you want the cronjob to run (e.g., 6 for 6 AM)
  * Do you have a public domain and valid SSL certificate? (Y/n):
  * Y – If you have a public domain and valid SSL certificate
  * n – If you do not have one (recommended only for development environments)
  * Retention days to remove old reports (Default: 3): Press Enter to accept the default or specify another value (e.g., 5).
  * Provide Slack Webhook URL to notify server issues on your Slack channel: (change the URL to your channel one)
     ```
      https://hooks.slack.com/services/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 
       ```
  * Is the eSignet service deployed? (yes/no):
      * no – If eSignet is not deployed, related test cases will be skipped.
        * Is values.yaml for the apitestrig chart set correctly as part of the prerequisites? (Y/n):
             * Enter Y if this step is already completed.
  * Do you have S3 details for storing API-Testrig reports? (Y/n):
  * Enter Y to proceed with S3 configuration.
  * S3 Host: eg. `http://minio.minio:9000`
  * S3 Region:(Leave blank or enter your specific region, if applicable)
  S3 Access Key:admin
