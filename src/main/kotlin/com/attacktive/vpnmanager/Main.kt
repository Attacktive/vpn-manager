package com.attacktive.vpnmanager

import com.attacktive.vpnmanager.scheduler.SchedulerService

fun main() {
	SchedulerService.setup().start()
}
