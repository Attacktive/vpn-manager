package com.attacktive.vpnmanager.configuration

import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Configurations(val cronExpression: String, val routerAddress: String, val testUrl: String, val testTimeout: String, val credentials: Credentials) {
	@Transient
	private var number = -1L

	@Transient
	private var unit = ChronoUnit.MILLIS

	init {
		val (number, unit) = validate(testTimeout)
		this.number = number
		this.unit = unit
	}

	companion object {
		fun default() = Configurations("0 0/30 * * * ?", "127.0.0.1", "http://localhost", "10s", Credentials("username", "password"))
	}

	fun testTimeoutDuration(): Duration {
		return Duration.of(number, unit)
	}

	private fun validate(string: String): Pair<Long, ChronoUnit> {
		val (_, numberString, unitString) = Regex("(\\d+)\\s*([smh])", RegexOption.IGNORE_CASE)
			.find(string)!!
			.groupValues

		val number = numberString.toLong()
		val unit = when (unitString) {
			"s", "S" -> ChronoUnit.SECONDS
			"m", "M" -> ChronoUnit.MINUTES
			"h", "H" -> ChronoUnit.HOURS
			else -> throw IllegalArgumentException("Unexpected argument: \"$unitString\"")
		}

		return Pair(number, unit)
	}
}
