## Properties file
set -e
properties_file="$1"
upgrade_version="$3"
current_version="$2"
     echo "Properties File Name - $properties_file"
     echo "DB Upgrade Version - $upgrade_version"
     echo "DB current version - $current_version"
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
     echo "DB current version \"$current_version\" found."
else
     echo "DB current version not found, Pass current version as argument."
     exit 0
fi
if [ $# -ge 3 ] 
then
     echo "DB upgrade version \"$upgrade_version\" found."
else
     echo "DB upgrade version not found, Pass upgrade version as argument."
     exit 0
fi


## Terminate existing connections
echo "Terminating active connections" 
CONN=$(PGPASSWORD=$SU_USER_PWD psql -v ON_ERROR_STOP=1 --username=$SU_USER --host=$DB_SERVERIP --port=$DB_PORT --dbname=$DEFAULT_DB_NAME -t -c "SELECT count(pg_terminate_backend(pg_stat_activity.pid)) FROM pg_stat_activity WHERE datname = '$MOSIP_DB_NAME' AND pid <> pg_backend_pid()";exit;)
echo "Terminated connections"

## Executing DB Upgrade scripts
echo "Alter scripts deployment on $MOSIP_DB_NAME database from $current_version to $upgrade_version  started...."
ALTER_SCRIPT_FILE="sql/${current_version}_to_${upgrade_version}_${ALTER_SCRIPT_FILENAME}"

echo "Upgrade script considered for release deployment - $ALTER_SCRIPT_FILE"

## Checking If Alter scripts are present
echo "Checking if script $ALTER_SCRIPT_FILE is present"
if [ -f "$ALTER_SCRIPT_FILE" ]
then
     echo "SQL file "$ALTER_SCRIPT_FILE" found."
else
     echo "SQL file not found, Since no SQL file present for \"$upgrade_version\"  hence exiting."
     exit 0
fi
echo Applying upgrade changes

PGPASSWORD=$SU_USER_PWD psql -v ON_ERROR_STOP=1 --username=$SU_USER --host=$DB_SERVERIP --port=$DB_PORT --dbname=$DEFAULT_DB_NAME -a -b -f $ALTER_SCRIPT_FILE
