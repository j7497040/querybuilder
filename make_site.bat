@echo off

%~d0
cd %~p0

REM Javaの環境変数
if NOT {%JAVA_HOME_7%}=="" (
     SET JAVA_HOME=%JAVA_HOME_7%
)

rem サイト生成
call mvn site:site

pause
