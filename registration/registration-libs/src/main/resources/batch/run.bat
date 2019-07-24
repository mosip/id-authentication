@echo OFF

set d=%DATE:~-4%-%DATE:~4,2%-%DATE:~7,2%
set t=%time::=.% 
set t=%t: =%
set logfile="%d%_%t%.log"


start jre\jre\bin\javaw -jar bin/run.jar -Xmx2048MB -Xms1024MB
