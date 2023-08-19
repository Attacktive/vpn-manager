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
		// fixme: VPN-managing job is doing too much. ☠
		for (retrievedItem in MudfishService.retrieveItems()) {
			val mudfishItemInConfigurations = configurations.mudfishItems.firstOrNull { mudfishItem -> mudfishItem.iid == retrievedItem.iid }
			if (mudfishItemInConfigurations?.pinned == true) {
				continue
			}

			MudfishService.turnOff(retrievedItem)

			if (mudfishItemInConfigurations?.enabled == false) {
				continue
			}

			val needsVpn = ConnectivityChecker.needsVpn(retrievedItem)
			if (needsVpn) {
				logger.info("[${retrievedItem.name}] Seems like you need to connect to the VPN. 😿")
				MudfishService.turnOn(retrievedItem)
			} else {
				logger.info("[${retrievedItem.name}] You don't need the VPN for now. 👍")
			}
		}
	}
}
