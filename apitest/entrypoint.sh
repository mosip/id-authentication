#!/bin/bash

## Run automationtests
java -Dmodules="$MODULES" -Denv.user="$ENV_USER" -Denv.endpoint="$ENV_ENDPOINT" -Denv.testLevel="$ENV_TESTLEVEL" -jar apitest-auth-*-jar-with-dependencies.jar;
