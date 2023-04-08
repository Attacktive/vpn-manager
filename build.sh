#!/usr/bin/zsh

pushd "${0:a:h}"
./gradlew clean uberJar
popd
