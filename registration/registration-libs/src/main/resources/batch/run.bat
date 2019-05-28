@echo OFF

set d=%DATE:~-4%-%DATE:~4,2%-%DATE:~7,2%
set t=%time::=.% 
set t=%t: =%
set logfile="%d%_%t%.log"

cd ./jre/bin
java -jar run.jar >> logs/registration-%logfile%
pause
