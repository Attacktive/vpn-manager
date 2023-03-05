package com.attacktive.vpnmanager

import com.attacktive.vpnmanager.scheduler.SchedulerService

fun main() {
	val scheduler = SchedulerService.setup()
	scheduler.start()
}
