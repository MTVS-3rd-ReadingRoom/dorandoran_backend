@echo off

if "%1"=="" (
    docker-compose --profile required down
    docker-compose --profile required up -d
) else (
    docker-compose down %1
    docker-compose up -d %1
)
