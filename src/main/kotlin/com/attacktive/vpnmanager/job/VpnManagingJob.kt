package com.attacktive.vpnmanager.job

import com.attacktive.vpnmanager.connectivity.ConnectivityChecker
import com.attacktive.vpnmanager.vpn.iptime.MudfishManipulator
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory

class VpnManagingJob: Job {
	private val logger = LoggerFactory.getLogger(VpnManagingJob::class.java)

	override fun execute(context: JobExecutionContext) {
		val mudfishManipulator = MudfishManipulator()
		mudfishManipulator.turnOff()

		val needsVpn = ConnectivityChecker.needsVpn()
		logger.debug("needsVpn: $needsVpn")

		if (needsVpn) {
			logger.info("Seems like you need to connect to the VPN. 😿")
			mudfishManipulator.turnOn()
		} else {
			logger.info("You don't need the VPN for now. 👍")
		}
	}
}
