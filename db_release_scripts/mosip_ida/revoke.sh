## Properties file 
set -e
properties_file="$1"
revoke_version="$2"
echo "Properties File Name - $properties_file"
echo "DB Revoke Version - $revoke_version"

if [ -f "$properties_file" ]
then
     echo "Property file \"$properties_file\" found."
    while IFS='=' read -r key value
    do
        key=$(echo $key | tr '.' '_')
         eval ${key}=\${value}
   done < "$properties_file"
else
     echo "Property file not found, Pass property file name as argument."
     exit 0
fi

if [ $# -ge 2 ]
then
     echo "DB revoke version \"$revoke_version\" found."
else
     echo "DB revoke version not found, Pass revoke version as argument."
     exit 0
fi

## Terminate existing connections
echo "Terminating active connections" 
CONN=$(PGPASSWORD=$SU_USER_PWD psql -v ON_ERROR_STOP=1 --username=$SU_USER --host=$DB_SERVERIP --port=$DB_PORT --dbname=$DEFAULT_DB_NAME -t -c "SELECT count(pg_terminate_backend(pg_stat_activity.pid)) FROM pg_stat_activity WHERE datname = '$MOSIP_DB_NAME' AND pid <> pg_backend_pid()";exit;)
echo "Terminated connections"

## Executing DB Revoke scripts
echo "Alter scripts deployment on $MOSIP_DB_NAME database is started. Revoke Version is $revoke_version"
ALTER_SCRIPT_FILE="sql/${revoke_version}_${REVOKE_SCRIPT_FILENAME}"
echo "Revoke script considered for DB changes - $ALTER_SCRIPT_FILE"

PGPASSWORD=$SU_USER_PWD psql -v ON_ERROR_STOP=1 --username=$SU_USER --host=$DB_SERVERIP --port=$DB_PORT --dbname=$DEFAULT_DB_NAME -a -b -f $ALTER_SCRIPT_FILE
