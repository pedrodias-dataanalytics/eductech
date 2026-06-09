@echo off
setlocal

cd /d "%~dp0"

set "APP_JAR=target\eductech-manager-1.0.0.jar"
set "JAVA_EXE=java"
set "MAVEN_EXE=mvn"

where java >nul 2>nul
if errorlevel 1 (
    if exist "C:\Tools\jdk-21.0.11+10\bin\java.exe" (
        set "JAVA_EXE=C:\Tools\jdk-21.0.11+10\bin\java.exe"
    ) else (
        echo Java nao encontrado. Configure o JDK 21 no PATH ou instale em C:\Tools\jdk-21.0.11+10.
        pause
        exit /b 1
    )
)

if not exist "%APP_JAR%" (
    echo JAR nao encontrado. Gerando a aplicacao com Maven...

    where mvn >nul 2>nul
    if errorlevel 1 (
        if exist "C:\Tools\apache-maven-3.9.16\bin\mvn.cmd" (
            set "MAVEN_EXE=C:\Tools\apache-maven-3.9.16\bin\mvn.cmd"
        ) else (
            echo Maven nao encontrado. Configure o Maven no PATH ou instale em C:\Tools\apache-maven-3.9.16.
            pause
            exit /b 1
        )
    )

    call "%MAVEN_EXE%" -q -DskipTests package
    if errorlevel 1 (
        echo Nao foi possivel gerar o JAR da aplicacao.
        pause
        exit /b 1
    )
)

echo Abrindo EducTech Manager...
"%JAVA_EXE%" -jar "%APP_JAR%"

if errorlevel 1 (
    echo.
    echo A aplicacao encerrou com erro. Verifique se o MySQL80 esta rodando e se a senha do banco esta correta.
    pause
)
