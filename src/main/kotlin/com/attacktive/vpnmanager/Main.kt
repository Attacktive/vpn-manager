package com.attacktive.vpnmanager

import com.attacktive.vpnmanager.job.VpnManagingJob
import com.attacktive.vpnmanager.vpn.iptime.Credentials
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.SchedulerFactory
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import kotlin.system.exitProcess

fun main(vararg args: String) {
	if (args.size < 2) {
		System.err.println("You have to provide arguments; the username and the password delimited by a whitespace.")
		exitProcess(1)
	}

	println("args: ${args.joinToString(", ")}")
	if (args.size > 3) {
		System.err.println("Other than the first 3 arguments are going to be ignored.")
	}

	val (username, password) = args
	val credentials = Credentials(username, password)

	val cronExpression = if (args.size == 3) {
		args[2]
	} else {
		"0 0/30 * * * ?"
	}

	setupScheduler(credentials, cronExpression)
}

private fun setupScheduler(credentials: Credentials, cronExpression: String) {
	val jobDetail = JobBuilder.newJob(VpnManagingJob::class.java)
		.withIdentity(System.currentTimeMillis().toString())
		.usingJobData("username", credentials.username)
		.usingJobData("password", credentials.password)
		.build()

	val trigger = TriggerBuilder.newTrigger()
		.withIdentity(System.currentTimeMillis().toString())
		.startNow()
		.forJob(jobDetail)
		.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
		.build()

	val schedulerFactory: SchedulerFactory = StdSchedulerFactory()
	schedulerFactory.scheduler.scheduleJob(jobDetail, trigger)
	schedulerFactory.scheduler.start()
}
