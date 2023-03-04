package com.attacktive.vpnmanager.connectivity

import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.time.temporal.ChronoUnit

class ConnectivityChecker {
	companion object {
		private const val URL_TO_TEST = "https://youtrack.jetbrains.com/"

		private val logger = LoggerFactory.getLogger(ConnectivityChecker::class.java)

		fun needsVpn(): Boolean {
			val httpRequest = HttpRequest.newBuilder(URI(URL_TO_TEST))
				.timeout(Duration.of(10, ChronoUnit.SECONDS))
				.GET()
				.build()

			return try {
				val statusCode = HttpClient.newHttpClient()
					.send(httpRequest) { BodySubscribers.discarding() }
					.statusCode()

				logger.debug("statusCode: $statusCode")
				statusCode >= 400
			} catch (e: HttpTimeoutException) {
				logger.info("${e.message} reaching $URL_TO_TEST")
				true
			}
		}
	}
}
