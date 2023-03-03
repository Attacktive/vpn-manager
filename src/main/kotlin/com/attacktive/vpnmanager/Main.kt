package com.attacktive.vpnmanager

import com.attacktive.vpnmanager.connectivity.ConnectivityChecker
import com.attacktive.vpnmanager.vpn.VpnStatus
import com.attacktive.vpnmanager.vpn.iptime.MudfishManipulator
import com.attacktive.vpnmanager.vpn.iptime.Router

fun main(vararg args: String) {
	println("args: ${args.joinToString(", ")}")

	val router = Router("http://10.0.0.1/sess-bin/login_handler.cgi", args[0], args[1])
	val mudfishManipulator = MudfishManipulator(router)
	mudfishManipulator.turnOff()

	val needsVpn = ConnectivityChecker.needsVpn()
	println("needsVpn: $needsVpn")

	if (mudfishManipulator.status() != VpnStatus.ON) {
		if (needsVpn) {
			println("Seems like you need to connect to the VPN.")
			mudfishManipulator.turnOn()
		} else {
			println("You don't need the VPN for now. 👍")
		}
	}
}
