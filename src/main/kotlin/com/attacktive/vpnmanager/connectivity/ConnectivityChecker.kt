package com.attacktive.vpnmanager.connectivity

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers
import com.attacktive.vpnmanager.configuration.ConfigurationsService
import com.attacktive.vpnmanager.configuration.MudfishItem
import org.slf4j.LoggerFactory

object ConnectivityChecker {
	private val logger = LoggerFactory.getLogger(ConnectivityChecker::class.java)
	private val configurations = ConfigurationsService.getConfigurations()

	fun needsVpn(mudfishItem: MudfishItem): Boolean {
		val httpRequest = HttpRequest.newBuilder(URI(mudfishItem.testUrl))
			.timeout(configurations.testTimeoutDuration())
			.GET()
			.build()

		return try {
			val statusCode = HttpClient.newHttpClient()
				.send(httpRequest) { BodySubscribers.discarding() }
				.statusCode()

			logger.debug("statusCode: $statusCode")
			statusCode >= 400
		} catch (e: IOException) {
			logger.info("${e.message} reaching ${mudfishItem.testUrl}")
			true
		}
	}
}
