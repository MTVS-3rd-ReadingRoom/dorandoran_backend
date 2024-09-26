#!/bin/bash

if [[ "$1" != "" ]]; then
  docker-compose $1 down
  docker-compose $1 up -d
else
  docker-compose down
  docker-compose up -d
fi
