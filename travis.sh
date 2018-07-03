#!/usr/bin/env bash


function sonar_analysis {
    if [[ -n ${TRAVIS_PULL_REQUEST} ]]; then
        mvn  sonar:sonar \
            -DskipTests=true \
            -Dsonar.analysis.mode=preview \
            -Dsonar.github.pullRequest=${TRAVIS_PULL_REQUEST} \
            -Dsonar.github.repository=unrealworkteam/atsd-api-java \
            -Dsonar.github.oauth=${GITHUB_ACCESS_TOKEN} \
            -Dsonar.host.url=${SONAR_HOST} \
            -Dsonar.login=${SONAR_TOKEN}
    else
       mvn  sonar:sonar \
            -DskipTests=true \
            -Dsonar.host.url=${SONAR_HOST} \
            -Dsonar.login=${SONAR_TOKEN}
    fi
}

sonar_analysis