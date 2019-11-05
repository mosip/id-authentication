### -- ---------------------------------------------------------------------------------------------------------
### -- Script Name		: Kernel Revoke DB deploy
### -- Deploy Module 	: MOSIP Kernel
### -- Purpose    		: To revoke Kernel Database alter scripts for the release.       
### -- Create By   		: Sadanandegowda
### -- Created Date		: 25-Oct-2019
### -- 
### -- Modified Date        Modified By         Comments / Remarks
### -- -----------------------------------------------------------------------------------------------------------

### -- -----------------------------------------------------------------------------------------------------------

#########Properties file #############
set -e
properties_file="$1"
revoke_version="$2"
     echo `date "+%m/%d/%Y %H:%M:%S"` ": $properties_file"
	 echo `date "+%m/%d/%Y %H:%M:%S"` ": DB Revoke Version - $revoke_version"
#properties_file="./app.properties"
if [ -f "$properties_file" ]
then
     echo `date "+%m/%d/%Y %H:%M:%S"` ": Property file \"$properties_file\" found."
    while IFS='=' read -r key value
    do
        key=$(echo $key | tr '.' '_')
         eval ${key}=\${value}
   done < "$properties_file"
else
     echo `date "+%m/%d/%Y %H:%M:%S"` ": Property file not found, Pass property file name as argument."
fi
echo `date "+%m/%d/%Y %H:%M:%S"` ": ------------------ Database server and service status check for ${MOSIP_DB_NAME}------------------------"

today=`date '+%d%m%Y_%H%M%S'`;
LOG="${LOG_PATH}${MOSIP_DB_NAME}-revoke-${today}.log"
touch $LOG

SERVICE=$(PGPASSWORD=$SU_USER_PWD  psql --username=$SU_USER --host=$DB_SERVERIP --port=$DB_PORT --dbname=$DEFAULT_DB_NAME -t -c "select count(1) from pg_roles where rolname IN('sysadmin')";exit; > /dev/null)
  
if [ "$SERVICE" -eq 0 ] || [ "$SERVICE" -eq 1 ]
then
echo `date "+%m/%d/%Y %H:%M:%S"` ": Postgres database server and service is up and running" | tee -a $LOG 2>&1
else
echo `date "+%m/%d/%Y %H:%M:%S"` ": Postgres database server or service is not running" | tee -a $LOG 2>&1
fi

echo `date "+%m/%d/%Y %H:%M:%S"` ": ----------------------------------------------------------------------------------------"

echo `date "+%m/%d/%Y %H:%M:%S"` ": Started sourcing the $MOSIP_DB_NAME Database Deployment Revoke scripts" | tee -a $LOG 2>&1

echo `date "+%m/%d/%Y %H:%M:%S"` ": Database revoke scripts are sourcing from :$BASEPATH/$MOSIP_DB_NAME/alter-scripts" | tee -a $LOG 2>&1

#========================================DB Alter Scripts deployment process begins on IDMAP DB SERVER==================================

echo `date "+%m/%d/%Y %H:%M:%S"` ": Revoke scripts for DB deployment on $MOSIP_DB_NAME database is started....Revoke Version...$revoke_version" | tee -a $LOG 2>&1

REVOKE_SCRIPT_FILENAME_VERSION="sql/${revoke_version}_${REVOKE_SCRIPT_FILENAME}"

echo `date "+%m/%d/%Y %H:%M:%S"` ": Alter scripts file which is considered for deployment revoke - $REVOKE_SCRIPT_FILENAME_VERSION" | tee -a $LOG 2>&1

cd /$BASEPATH/$MOSIP_DB_NAME/

pwd | tee -a $LOG 2>&1

CONN=$(PGPASSWORD=$SYSADMIN_PWD psql --username=$SYSADMIN_USER --host=$DB_SERVERIP --port=$DB_PORT --dbname=$DEFAULT_DB_NAME -t -c "SELECT count(pg_terminate_backend(pg_stat_activity.pid)) FROM pg_stat_activity WHERE datname = '$MOSIP_DB_NAME' AND pid <> pg_backend_pid()";exit; >> $LOG 2>&1)

if [ ${CONN} == 0 ]
then
    echo `date "+%m/%d/%Y %H:%M:%S"` ": No active database connections exist on ${MOSIP_DB_NAME}" | tee -a $LOG 2>&1
else
    echo `date "+%m/%d/%Y %H:%M:%S"` ": Active connections exist on the database server and active connection will be terminated for DB deployment." | tee -a $LOG 2>&1
fi 

if [ ${REVOKE_SCRIPT_FLAG} == 1 ]
then
    echo `date "+%m/%d/%Y %H:%M:%S"` ": Executing revoke scripts for ${MOSIP_DB_NAME} database" | tee -a $LOG 2>&1
    PGPASSWORD=$SYSADMIN_PWD psql --username=$SYSADMIN_USER --host=$DB_SERVERIP --port=$DB_PORT --dbname=$DEFAULT_DB_NAME -a -b -f $REVOKE_SCRIPT_FILENAME_VERSION >> $LOG 2>&1
else
    echo `date "+%m/%d/%Y %H:%M:%S"` ": There are no revoke scripts available for this deployment at ${MOSIP_DB_NAME}" | tee -a $LOG 2>&1
fi

if [ $(grep -c ERROR $LOG) -ne 0 ]
then
    echo `date "+%m/%d/%Y %H:%M:%S"` ": Database deployment revoke version $revoke_version is completed with ERRORS, Please check the logs for more information" | tee -a $LOG 2>&1
	echo `date "+%m/%d/%Y %H:%M:%S"` ": END of Alter scripts MOSIP database deployment" | tee -a $LOG 2>&1
else
    echo `date "+%m/%d/%Y %H:%M:%S"` ": Database deployment revoke version $revoke_version completed successfully, Please check the logs for more information" | tee -a $LOG 2>&1
    echo `date "+%m/%d/%Y %H:%M:%S"` ": END of MOSIP \"${MOSIP_DB_NAME}\" database deployment revoke" | tee -a $LOG 2>&1
fi 	

echo "******************************************"`date "+%m/%d/%Y %H:%M:%S"` "*****************************************************" >> $LOG 2>&1


