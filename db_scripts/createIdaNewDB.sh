#!/usr/bin/env bash

echo "syntax: bash reNameDB.sh oldDbName newDbName password";

if [[ $# -ne 3 ]]; then
  echo "oldDB, newDB & dbPwd not provided as parameter; EXITING"
  exit 1;
fi

## set variables
oldDB=$1;     ## :mosipdbname
newDB=$2;     ## :mosipdbname_1
dbPwd=$3

## create new DB directory
rm -rf $newDB
cp -r $oldDB $newDB

## update DB
sed -i "s/$oldDB\>/$newDB/g" $newDB/:mosipdbname_deploy.properties;
sed -i "s/$oldDB/$newDB/g"   $newDB/:mosipdbname_db.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/:mosipdbname_ddl_deploy.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/:mosipdbname_dml_deploy.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/:mosipdbname_grants.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/mosip_role_common.sql;
sed -i "s/$oldDB/$newDB/g"   $newDB/mosip_role_:dbuname.sql;

## update DB properties
sed -i "s/DB_SERVERIP=.*/DB_SERVERIP=mzworker0.sb/g" $newDB/:mosipdbname_deploy.properties;
sed -i "s/DB_PORT=.*/DB_PORT=30090/g" $newDB/:mosipdbname_deploy.properties;
sed -i "s/SYSADMIN_PWD=.*/SYSADMIN_PWD=$dbPwd/g" $newDB/:mosipdbname_deploy.properties;
sed -i "s/DBADMIN_PWD=.*/DBADMIN_PWD=$dbPwd/g" $newDB/:mosipdbname_deploy.properties;
sed -i "s/APPADMIN_PWD=.*/APPADMIN_PWD=$dbPwd/g" $newDB/:mosipdbname_deploy.properties;
sed -i "s/DBUSER_PWD=.*/DBUSER_PWD=$dbPwd/g" $newDB/:mosipdbname_deploy.properties;
sed -i "s:BASEPATH=.*:BASEPATH=$PWD:g" $newDB/:mosipdbname_deploy.properties;
sed -i "s/LOG_PATH=.*/LOG_PATH=..\/..\/..\/logs\//g" $newDB/:mosipdbname_deploy.properties;
sed -i "s/DML_FLAG=.*/DML_FLAG=1/g" $newDB/:mosipdbname_deploy.properties;

echo "success";