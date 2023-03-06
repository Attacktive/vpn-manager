package com.attacktive.vpnmanager.scheduler

import com.attacktive.vpnmanager.configuration.ConfigurationsService
import com.attacktive.vpnmanager.job.VpnManagingJob
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

object SchedulerService {
	private val configurations = ConfigurationsService.getConfigurations()

	fun setup(): Scheduler {
		val jobDetail = JobBuilder.newJob(VpnManagingJob::class.java)
			.withIdentity(System.currentTimeMillis().toString())
			.build()

		val trigger = TriggerBuilder.newTrigger()
			.withIdentity(System.currentTimeMillis().toString())
			.startNow()
			.forJob(jobDetail)
			.withSchedule(CronScheduleBuilder.cronSchedule(configurations.cronExpression))
			.build()

		val scheduler = StdSchedulerFactory().scheduler
		scheduler.scheduleJob(jobDetail, trigger)

		return scheduler
	}
}
