package com.attacktive.vpnmanager.connectivity

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers
import java.net.http.HttpTimeoutException
import com.attacktive.vpnmanager.configuration.ConfigurationsService
import org.slf4j.LoggerFactory

object ConnectivityChecker {
	private val logger = LoggerFactory.getLogger(ConnectivityChecker::class.java)

	fun needsVpn(): Boolean {
		val configurations = ConfigurationsService.getConfigurations()

		val httpRequest = HttpRequest.newBuilder(URI(configurations.testUrl))
			.timeout(configurations.testTimeoutDuration())
			.GET()
			.build()

		return try {
			val statusCode = HttpClient.newHttpClient()
				.send(httpRequest) { BodySubscribers.discarding() }
				.statusCode()

			logger.debug("statusCode: $statusCode")
			statusCode >= 400
		} catch (e: HttpTimeoutException) {
			logger.info("${e.message} reaching ${configurations.testUrl}")
			true
		}
	}
}
