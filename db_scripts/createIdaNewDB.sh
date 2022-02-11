#!/usr/bin/env bash

echo "syntax: bash reNameDB.sh oldDbName newDbName password";

if [[ $# -ne 3 ]]; then
  echo "oldDB, newDB & dbPwd not provided as parameter; EXITING"
  exit 1;
fi

## set variables
oldDB=$1;     ## mosip_ida
newDB=$2;     ## mosip_ida_1
dbPwd=$3

## create new DB directory
rm -rf $newDB
cp -r $oldDB $newDB

## update DB
sed -i "s/$oldDB\>/$newDB/g" $newDB/mosip_ida_deploy.properties;
sed -i "s/$oldDB/$newDB/g"   $newDB/mosip_ida_db.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/mosip_ida_ddl_deploy.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/mosip_ida_dml_deploy.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/mosip_ida_grants.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/mosip_role_common.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/mosip_role_idauser.sql;

## update DB properties
sed -i "s/DB_SERVERIP=.*/DB_SERVERIP=mzworker0.sb/g" $newDB/mosip_ida_deploy.properties;
sed -i "s/DB_PORT=.*/DB_PORT=30090/g" $newDB/mosip_ida_deploy.properties;
sed -i "s/SYSADMIN_PWD=.*/SYSADMIN_PWD=$dbPwd/g" $newDB/mosip_ida_deploy.properties;
sed -i "s/DBADMIN_PWD=.*/DBADMIN_PWD=$dbPwd/g" $newDB/mosip_ida_deploy.properties;
sed -i "s/APPADMIN_PWD=.*/APPADMIN_PWD=$dbPwd/g" $newDB/mosip_ida_deploy.properties;
sed -i "s/DBUSER_PWD=.*/DBUSER_PWD=$dbPwd/g" $newDB/mosip_ida_deploy.properties;
sed -i "s:BASEPATH=.*:BASEPATH=$PWD:g" $newDB/mosip_ida_deploy.properties;
sed -i "s/LOG_PATH=.*/LOG_PATH=..\/..\/..\/logs\//g" $newDB/mosip_ida_deploy.properties;
sed -i "s/DML_FLAG=.*/DML_FLAG=1/g" $newDB/mosip_ida_deploy.properties;

echo "success";