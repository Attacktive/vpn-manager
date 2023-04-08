package com.attacktive.vpnmanager

import com.attacktive.vpnmanager.configuration.ConfigurationsWatchService
import com.attacktive.vpnmanager.job.VpnManagingJob
import com.attacktive.vpnmanager.scheduler.SchedulerService

fun main() {
	SchedulerService.setup().start()

	VpnManagingJob().executeOutOfNowhere()

	ConfigurationsWatchService.watchAndExecute { VpnManagingJob().executeOutOfNowhere() }
}
