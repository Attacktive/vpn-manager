package com.attacktive.vpnmanager

import com.attacktive.vpnmanager.configuration.ConfigurationsService
import com.attacktive.vpnmanager.scheduler.SchedulerService
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(Main::class.java)

class Main {
	companion object {
		@JvmStatic
		fun main(vararg args: String) {
			val configurations = ConfigurationsService.getConfigurations()

			val scheduler = SchedulerService.setup(configurations)
			scheduler.start()
		}
	}
}
