@echo off

%~d0
cd %~p0

set JAVA_HOME="C:\usr\Java\jdk1.7.0_76"

REM call mvn -Dmaven.test.skip=true assembly:assembly

call mvn -Dmaven.test.skip=true package
rem call mvn dependency:copy-dependencies
call mvn antrun:run

PAUSE

