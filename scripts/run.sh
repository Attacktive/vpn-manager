#!/usr/bin/env zsh

pushd "${0:a:h}/.."
./build.sh
java -jar build/libs/vpn-manager-1.3.5-uber.jar
popd
