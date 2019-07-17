@echo off

%~d0
cd %~p0

REM Javaの環境変数
if NOT {%JAVA_HOME_7%}=="" (
    SET JAVA_HOME=%JAVA_HOME_7%
)
rem コンパイル
call mvn compile

rem jarデプロイ
call mvn -Dmaven.test.skip=true source:jar deploy

pause
