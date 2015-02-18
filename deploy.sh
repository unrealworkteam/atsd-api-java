#!/bin/bash

. deploy.config

mvn -s settings.xml -DargLine="-Daxibase.tsd.api.server.name=$ATSD_SERVER \
-Daxibase.tsd.api.server.port=$ATSD_PORT -Daxibase.tsd.api.username=$ATSD_USERNAME \
-Daxibase.tsd.api.password=$ATSD_PASSWORD" clean javadoc:jar \
source:jar gpg:sign -Dgpg.passphrase=$GPG_PASSPHRASE deploy