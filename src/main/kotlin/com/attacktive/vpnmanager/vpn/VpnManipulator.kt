package com.attacktive.vpnmanager.vpn

interface VpnManipulator {
	fun turnOn()
	fun turnOff()
	fun status(): VpnStatus
}
