#!/usr/bin/zsh

./build.sh
java -jar build/libs/vpn-manager-1.0.0-uber.jar 'username' 'password' 'optional-cron-expression'
