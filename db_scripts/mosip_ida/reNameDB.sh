#!/usr/bin/env bash

echo "syntax: bash reNameDB.sh oldDbName newDbName";

if [[ $# -ne 2 ]]; then
  echo "oldDB name & newDB name not provided as parameter; EXITING"
  exit 1;
fi

oldDB=$1;
newDB=$2;

## update DB
sed -i "s/$oldDB\>/$newDB/g" mosip_ida_deploy.properties;
sed -i "s/$oldDB/$newDB/g" mosip_ida_db.sql;
sed -i "s/$oldDB/$newDB/g" mosip_ida_ddl_deploy.sql;
sed -i "s/$oldDB/$newDB/g" mosip_ida_dml_deploy.sql;
sed -i "s/$oldDB/$newDB/g" mosip_ida_grants.sql;
sed -i "s/$oldDB/$newDB/g" mosip_role_common.sql;
sed -i "s/$oldDB/$newDB/g" mosip_role_idauser.sql;

echo "success";
