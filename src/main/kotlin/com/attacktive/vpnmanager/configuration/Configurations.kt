package com.attacktive.vpnmanager.configuration

import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Configurations(val cronExpression: String, val testTimeout: String, val vpnToggleUrl: String, val mudfishGraphqlUrl: String = "https://api.mudfish.net/graphql", val authorization: String, val mudfishItems: List<MudfishItem> = listOf()) {
	@Transient
	private var number = -1L

	@Transient
	private var unit = ChronoUnit.MILLIS

	init {
		val (number, unit) = validate(testTimeout)
		this.number = number
		this.unit = unit
	}

	fun testTimeoutDuration(): Duration = Duration.of(number, unit)

	fun mergeWith(other: NullableConfigurations): Configurations {
		val cronExpression = other.cronExpression ?: this.cronExpression
		val testTimeout = other.testTimeout ?: this.testTimeout
		val vpnToggleUrl = other.vpnToggleUrl ?: this.vpnToggleUrl
		val mudfishGraphqlUrl = other.mudfishGraphqlUrl ?: this.mudfishGraphqlUrl
		val authorization = other.authorization ?: this.authorization
		val mudfishItems = other.mudfishItems.ifEmpty { this.mudfishItems }

		return Configurations(cronExpression, testTimeout, vpnToggleUrl, mudfishGraphqlUrl, authorization, mudfishItems)
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
