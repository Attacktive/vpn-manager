#!/usr/bin/zsh

git pull --ff-only
./gradlew clean uberJar
