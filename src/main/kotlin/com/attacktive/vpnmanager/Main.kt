package com.attacktive.vpnmanager

import com.attacktive.vpnmanager.connectivity.ConnectivityChecker

fun main(vararg args: String) {
	println("args: ${args.joinToString(", ")}")

	val isConnected = ConnectivityChecker.check()
	println("isConnected: $isConnected")
}
