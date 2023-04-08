package com.attacktive.vpnmanager.job

import com.attacktive.vpnmanager.configuration.ConfigurationsService
import com.attacktive.vpnmanager.connectivity.ConnectivityChecker
import com.attacktive.vpnmanager.mudfish.MudfishService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory

class VpnManagingJob: Job {
	private val logger = LoggerFactory.getLogger(VpnManagingJob::class.java)
	private val configurations = ConfigurationsService.getConfigurations()

	override fun execute(context: JobExecutionContext) {
		executeOutOfNowhere()
	}

	fun executeOutOfNowhere() {
		configurations.mudfishItems.forEach {
			MudfishService.turnOff(it)
			val needsVpn = ConnectivityChecker.needsVpn(it)
			if (needsVpn) {
				logger.info("[${it.name}] Seems like you need to connect to the VPN. 😿")
				MudfishService.turnOn(it)
			} else {
				logger.info("[${it.name}] You don't need the VPN for now. 👍")
			}
		}
	}
}
