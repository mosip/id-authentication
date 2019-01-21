The database objects related to MOSIP modules is stored under scripts/database/mosip_<module abbreviation> folder on git. for example, the pre-registration module script folder is scripts/database/mosip_prereg where all the scripts related to pre-registration are managed. Each database folder has the following files / folders

<database folder>
	-- mosip_role_common.sql
	-- mosip_role_<application_user>.sql
	-- <database name>_db.sql
	-- <database name>_grants.sql
    -- ddl
		-- file01.sql
		-- file02.sql
		-- .
		-- .
		-- filenn.sql
	-- <database name>_ddl_deploy.sql
    -- dml
		-- file01.sql
		-- file02.sql
		-- .
		-- .
		-- filenn.sql
	-- <database name>_dml_deploy.sql

To deploy the scripts on postgresql database, follow the below instructions, the below commands are sample commands for pre-registration module. 

1. Copy the scripts (entire folder) to the machine where postgresql client software is available.

2. Navigate to the database's main folder(mosip_prereg) and logon to postgresql client (psql) using postgres default credential (make sure the connected user has role creation privilege)

$ psql --hostname=<hostname> --port=<port> --username=<username>

3. Run the role creation script,mosip_role_common.sql on the postgres command line as below

<logged in user>#\i mosip_role_common.sql

4. Run the application role creation script,mosip_role_prereguser.sql on the postgres command line as below

<logged in user>#\i mosip_role_prereguser.sql

5. Run the database and schema creation script,mosip_prereg_db.sql on the postgres command line as below

<logged in user>#\i mosip_prereg_db.sql

6. Run the needed grants script,mosip_prereg_grants.sql on the postgres command line as below

<mosip_prereg>#\i mosip_prereg_grants.sql

Once the database and user/roles are created, the database objects and the needed data are created. Follow the below steps to create it 

7. To deploy the objects, run the following command on the same command line prompt

<mosip_prereg>#\i mosip_prereg_ddl_deploy.sql

8. To create the data, run the following command on the same command line prompt

<mosip_prereg>#\i mosip_prereg_dml_deploy.sql
