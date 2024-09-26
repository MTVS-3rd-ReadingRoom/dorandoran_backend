@echo off

if "%1"=="" (
    docker-compose  down
    docker-compose  up -d
) else (
    docker-compose down %1
    docker-compose up -d %1
)
