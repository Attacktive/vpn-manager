#!/usr/bin/env zsh

pushd "${0:a:h}"
./gradlew clean uberJar
popd
