#!/bin/bash

## Run automationtests
java -jar -Dmodules="$MODULES" -Denv.user="$ENV_USER" -Denv.endpoint="$ENV_ENDPOINT" -Denv.testLevel="$ENV_TESTLEVEL" apitest-auth-*-jar-with-dependencies.jar;
