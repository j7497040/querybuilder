@echo off

%~d0
cd %~p0

REM Java‚ÌŠÂ‹«•Ï”
if NOT {%JAVA_HOME_7%}=="" (
    SET JAVA_HOME=%JAVA_HOME_7%
)

rem maven‚Åclean
rem call mvn clean

rem maven‚Ådeploy
call mvn eclipse:eclipse -DdownloadSources=true -Declipse.useProjectReferences=false
rem call mvn eclipse:eclipse -DdownloadSources=true


pause