package com.attacktive.vpnmanager.job

import com.attacktive.vpnmanager.connectivity.ConnectivityChecker
import com.attacktive.vpnmanager.vpn.VpnStatus
import com.attacktive.vpnmanager.vpn.iptime.Credentials
import com.attacktive.vpnmanager.vpn.iptime.MudfishManipulator
import com.attacktive.vpnmanager.vpn.iptime.Router
import org.quartz.Job
import org.quartz.JobExecutionContext

class VpnManagingJob: Job {
	override fun execute(context: JobExecutionContext) {
		val username = context.mergedJobDataMap.getString("username")
		val password = context.mergedJobDataMap.getString("password")

		val router = Router("http://10.0.0.1/sess-bin/login_handler.cgi", Credentials(username, password))
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
}
