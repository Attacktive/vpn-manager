#!/usr/bin/zsh

./gradlew clean uberJar
java -jar build/libs/vpn-manager-1.0.0-uber.jar 'username' 'password' 'optional-cron-expression'
