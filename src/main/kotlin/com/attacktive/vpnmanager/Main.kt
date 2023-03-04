package com.attacktive.vpnmanager

import com.attacktive.vpnmanager.job.VpnManagingJob
import com.attacktive.vpnmanager.vpn.iptime.Credentials
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.SchedulerFactory
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val logger = LoggerFactory.getLogger(Main::class.java)

class Main {
	companion object {
		private const val DEFAULT_CRON_EXPRESSION = "0 0/30 * * * ?"

		@JvmStatic
		fun main(vararg args: String) {
			if (args.size < 2) {
				logger.error("You have to provide arguments; the username and the password delimited by a whitespace.")
				exitProcess(1)
			}

			logger.debug("args: ${args.joinToString(", ")}")

			val cronExpression = if (args.size == 2) {
				logger.info("You provided no cron expression; using the default: \"$DEFAULT_CRON_EXPRESSION\"")

				DEFAULT_CRON_EXPRESSION
			} else {
				if (args.size > 3) {
					logger.error("Other than the first 3 arguments are going to be ignored.")
				}

				args[2]
			}

			val (username, password) = args
			val credentials = Credentials(username, password)

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
	}
}
