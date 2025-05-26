#!/bin/bash

docker compose pull
docker compose up --remove-orphans --force-recreate --build -d
docker image prune -f
