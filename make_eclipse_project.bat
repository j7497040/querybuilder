@echo off

%~d0
cd %~p0

REM Javaの環境変数
if NOT {%JAVA_HOME_7%}=="" (
    SET JAVA_HOME=%JAVA_HOME_7%
)

rem mavenでclean
rem call mvn clean

rem mavenでdeploy
call mvn eclipse:eclipse -DdownloadSources=true -Declipse.useProjectReferences=false
rem call mvn eclipse:eclipse -DdownloadSources=true


pause