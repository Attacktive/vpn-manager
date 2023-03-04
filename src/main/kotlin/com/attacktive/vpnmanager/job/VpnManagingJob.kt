package com.attacktive.vpnmanager.job

import com.attacktive.vpnmanager.connectivity.ConnectivityChecker
import com.attacktive.vpnmanager.mudfish.Mudfish
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory

class VpnManagingJob: Job {
	private val logger = LoggerFactory.getLogger(VpnManagingJob::class.java)

	override fun execute(context: JobExecutionContext) {
		val mudfish = Mudfish()
		mudfish.turnOff()

		val needsVpn = ConnectivityChecker.needsVpn()
		logger.debug("needsVpn: $needsVpn")

		if (needsVpn) {
			logger.info("Seems like you need to connect to the VPN. 😿")
			mudfish.turnOn()
		} else {
			logger.info("You don't need the VPN for now. 👍")
		}
	}
}
