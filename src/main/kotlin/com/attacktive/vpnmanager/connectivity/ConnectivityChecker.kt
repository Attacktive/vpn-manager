package com.attacktive.vpnmanager.connectivity

import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers
import java.time.Duration
import java.time.temporal.ChronoUnit

class ConnectivityChecker {
	companion object {
		private const val DESTINATION_SITE = "https://www.jandi.com/"

		private val logger = LoggerFactory.getLogger(ConnectivityChecker::class.java)

		fun needsVpn(): Boolean {
			val httpRequest = HttpRequest.newBuilder(URI(DESTINATION_SITE))
				.timeout(Duration.of(10, ChronoUnit.SECONDS))
				.GET()
				.build()

			return try {
				val statusCode = HttpClient.newHttpClient()
					.send(httpRequest) { BodySubscribers.discarding() }
					.statusCode()

				logger.debug("statusCode: $statusCode")
				statusCode >= 400
			} catch (e: HttpConnectTimeoutException) {
				logger.info("${e.message} reaching $DESTINATION_SITE")
				true
			}
		}
	}
}
