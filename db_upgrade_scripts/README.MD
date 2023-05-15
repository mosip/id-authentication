## MOSIP ID Authentication Database (mosip_ida) Release scripts inventory and deployment/revoke guidelines on postgresql database. 

## Prerequisities

* DB Server and access details

* Postgres client (psql) has to be installed on the deployment servers.

* Copy database release scripts(DDL, DML, .SH ... etc) from git/repository on to the DB deployment server.

* Necessary details needs to be updated in peoperties file against to the releavnt variables being used (details are listed below).

* Database release objects related to MOSIP modules are placed in "**mosip_base_directory**>>db_release_scripts>>mosip_<schema_name> folder on git/repository

**Example:** For ID Authentication module release script folder is /**mosip_base_directory**>>db_release_scripts>>mosip_ida where all the database release scripts related to ID Authentication are available.

* Create a log file directory if it doesn't exist on DB deployment server before updating the properties file. Please follow the steps to create the same:
 
<em> bash-4.2$mkdir /mosip_base_directory/<folder_name> </em>

* If we wish to place the log files under different directory other than the above mentioned then we need to create directory and specify the path of the directory in the properties file.

* Pull the DB deployment scripts from Git repository to the deployment server directly and start deploying  OR

* If are pulling to local system from Git repository and pushing them back to deployment server using WinSCP then make a note to modify the following encoding settings in WinSCP before pushing the files to deployment server --> Open WinSCP --> Options --> Preferences --> Transfer --> Edit --> In "Transfer mode" section --> select "Text" --> Click Ok --> Click Ok 


## Each release database folder has the following files / folders

* **mosip_<schema_name>/ddl folder:** Contains table creation scripts for the release.

* **mosip_<schema_name>/dml folder:** Contains seed/master data CSVs for the release.

* **mosip_<schema_name>/sql folder:** SQL alter script files for the release and revoke, Specific to release version.

* **mosip_<schema_name>/<schema_name>_release_db_deploy.sh:**  Script to deploy alter scripts.

* **mosip_<schema_name>/<schema_name>_release_deploy.properties:** Parameters required for release DB deployment

* **mosip_<schema_name>/<schema_name>_revoke_db_deploy.sh:**  Script to revoke database deployment.


**Note:** SQL script files name under sql folder are appended with release version numbers( like **1.2.0**_ida-scripts_release.sql), Same version number has to be passed as argument while executing release/revoke shell scripts. Ex. **$bash ida_release_db_deploy.sh ida_release_deploy.properties 1.2.0**

**Note :** No need of shell script update unless it is really causing any problem or any further implementation is being introduced.

Once completed with sourcing the database release files, Follow the DB release deployment steps by modifying the properties file according the requirement.



### Properties file variable details and description. Properties file has to be updated with the required details  for each database before proceeding with deployment steps.

**DB_SERVERIP=** Contains details of Destination SERVER_IP(Ex:10.0.0.1) where the deployment is targeted

**DB_PORT=** Contains the postgres server port details where the postgres is allowed to connect. Ex: 5433

**SU_USER=** Contains the postgres super user name to connect to the postgres database i.e. postgres

**DEFAULT_DB_NAME=** Default database name to connect with respective postgres server i.e. ex: postgres

**MOSIP_DB_NAME=** Database name for which the deployment is scheduled.

**SYSADMIN_USER=** Variable contains the sysadmin use name which indeed is going to be the super user for the remaining actions going to be performed by shell script.

**BASEPATH=** Path for DB scrips which are kept in the Deployment server. Ex: /home/madmin/database_release

**LOG_PATH=** Path where deployment log file will be created. Ex: /home/madmin/logs/

**ALTER_SCRIPT_FLAG=** Flag variable which contains value as 0 or 1 for any Alter scripts existance for the particular DB. if flag=0 then no alterscripts else flag=1.

**ALTER_SCRIPT_FILENAME=** Alter scripts sql filename, This file is kept under sql directory, ex: <release_version_number>_<schema_name>-scripts_release.sql.

**REVOKE_SCRIPT_FLAG=** Flag variable which contains value as 0 or 1 for any revoke scripts existance for the particular DB. if flag=0 then no revokescripts else flag=1.

**REVOKE_SCRIPT_FILENAME=** Revoke scripts sql filename, This file is kept under sql directory, ex: <release_version_number>_<schema_name>-scripts_revoke.sql.

**Note - Make sure, There is a single empty line at end of the .properties files content and No spaces in beggining and end of the parameter values**

### Set below password for users in environmental variable

**SU_USER_PWD=** Contains the password for postgres super user
**SYSADMIN_PWD=** Contains the credential details for SYSADMIN_USER.
Example:
	`export SU_USER_PWD=supassword
	 export SYSADMIN_PWD=sysadminpassword`

## DB release deployment:
		
**Step 1** -> Make modification to all the respective database properties files **(<<schema>>_release_deploy.properties)** in the respective database directories. Path of properties file and variables list explained above. Once the properties files are ready then access the directory where the deployment script is kept.

**Step 2** -> DB Release deployment for all databases, run the **"<<schema>>_release_db_deploy.sh"** script with release version as parameter.  To access and exeute **"<<schema>>_release_db_deploy.sh"** script, follow the below given sample commands:

<em> **Enter:-bash-4.2$** cd /home/madmin/database_release/<<database_name>> </em>

<em> **Enter:-bash-4.2$** bash <<schema>>_release_db_deploy.sh  <<schema>>_release_deploy.properties <release_version_number> </em>

**Step 3** -> Please observe <em> Post Release Deployment Validation </em>  steps below

**No modification required to be done on any of the <>.sql or <>.sh files in the database_release folder. If it is required to be modified then please reach out to database team and have it modified.**


## DB deployment revoke: (Optional: Execute only if db version release deployment revoking required)

**Step 1** -> DB Revoke deployment for all databases, run the **"<<schema>>_revoke_db_deploy.sh"** script with release version as parameter.  To access and exeute **"<<schema>>_revoke_db_deploy.sh"** script, follow the below given sample commands:

<em> **Enter:-bash-4.2$** cd /home/madmin/database_release/<<database_name>> </em>

<em> **Enter:-bash-4.2$** bash bash <<schema>>_revoke_db_deploy.sh  <<schema>>_release_deploy.properties <release_version_number> </em>

**Step 2** -> Please observe <em> Post Release Deployment Validation </em>  steps below


### Post Deployment Validation

**Note:** If you encounter the following messages then please recheck the details(ip address, port number, database name, password) entered in the properties file, the message would be as follows:

<psql: could not translate host name "52.172.12.285" to address: Name or service not known>.

<psql: FATAL:  password authentication failed for user "postgress">

<psql: FATAL:  database "postgress" does not exist>

**Key points during or after the script execution:**

 * Properties file found message

 * Server status

 * Accessing the right path for DB deploy

 * Creates respective roles

 * Check for any active connections

 * Creates roles, creating Database, schemas, granting access, creating respective tables.

 * Loading data or DML operations valid only for those DB's which carries DML actions.

 * End of sourcing or deployment process.
		
**Post deployment process, look out for database deployment log file which captures all stages of deployment. Log file path is defined in the properties file of the databases.**

**During all the above stages please watch out for any ERRORs which will be capture in the log file.**

Kindly ignore **NOTICE** or **SKIPPING** messages. As these messages states that particular action is already in place hence sql script ignore performing again.
