package com.attacktive.vpnmanager.connectivity

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers
import java.time.Duration
import java.time.temporal.ChronoUnit

class ConnectivityChecker {
	companion object {
		fun check(): Boolean {
			var success: Boolean

			val httpRequest = HttpRequest.newBuilder(URI("https://www.jandi.com/"))
				.timeout(Duration.of(10, ChronoUnit.SECONDS))
				.GET()
				.build()

			try {
				val statusCode = HttpClient.newHttpClient()
					.send(httpRequest) { BodySubscribers.discarding() }
					.statusCode()

				println("statusCode: $statusCode")
				success = statusCode < 400
			} catch (e: HttpConnectTimeoutException) {
				e.printStackTrace()
				success = false
			}

			return success
		}
	}
}
