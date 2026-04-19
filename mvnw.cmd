@echo off
setlocal

set "MVNW_PROJECTBASEDIR=%~dp0"
if "%MVNW_PROJECTBASEDIR%"=="" set "MVNW_PROJECTBASEDIR=."
if "%MVNW_PROJECTBASEDIR:~-1%"=="\" set "MVNW_PROJECTBASEDIR=%MVNW_PROJECTBASEDIR:~0,-1%"

set "WRAPPER_DIR=%MVNW_PROJECTBASEDIR%\.mvn\wrapper"
set "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
set "WRAPPER_PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties"

if not exist "%WRAPPER_PROPERTIES%" (
  echo [ERROR] Maven wrapper properties file not found: "%WRAPPER_PROPERTIES%"
  exit /b 1
)

if not exist "%WRAPPER_JAR%" (
  echo Downloading Maven wrapper jar...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference='Stop';" ^
    "$props = Get-Content '%WRAPPER_PROPERTIES%';" ^
    "$wrapperUrl = ($props | Where-Object { $_ -like 'wrapperUrl=*' } | Select-Object -First 1).Split('=')[1];" ^
    "New-Item -ItemType Directory -Force -Path '%WRAPPER_DIR%' | Out-Null;" ^
    "Invoke-WebRequest -UseBasicParsing -Uri $wrapperUrl -OutFile '%WRAPPER_JAR%'"
  if errorlevel 1 (
    echo [ERROR] Could not download Maven wrapper jar.
    exit /b 1
  )
)

set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if not exist "%JAVA_EXE%" set "JAVA_EXE=java"

"%JAVA_EXE%" "-Dmaven.multiModuleProjectDirectory=%MVNW_PROJECTBASEDIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
exit /b %ERRORLEVEL%
