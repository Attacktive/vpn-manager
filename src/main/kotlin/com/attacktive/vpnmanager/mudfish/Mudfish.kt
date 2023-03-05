package com.attacktive.vpnmanager.mudfish

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import com.attacktive.vpnmanager.configuration.ConfigurationsService
import org.slf4j.LoggerFactory

class Mudfish {
	private val logger = LoggerFactory.getLogger(Mudfish::class.java)
	private val configurations = ConfigurationsService.getConfigurations()

	fun turnOn() {
		turnOnOrOffMudfish(true)
	}

	fun turnOff() {
		turnOnOrOffMudfish(false)
	}

	private fun turnOnOrOffMudfish(toTurnOn: Boolean) {
		val sessionId = getSessionId()

		val httpRequest = HttpRequest.newBuilder(URI("http://${configurations.routerAddress}:8282/do/mudfish/${if (toTurnOn) "start" else "stop"}"))
			.header("Cookie", "efm_session_id=$sessionId")
			.POST(BodyPublishers.noBody())
			.build()

		val success = try {
			val statusCode = HttpClient.newHttpClient()
				.send(httpRequest) { HttpResponse.BodySubscribers.discarding() }
				.statusCode()

			statusCode < 400
		} catch (e: IOException) {
			logger.error(e.message, e)
			false
		}

		if (!success) {
			logger.error("Failed to ${if (toTurnOn) "start" else "stop"} the VPN.")
		}
	}

	private fun login(): HttpResponse<String> {
		val requestBody = "init_status=1&captcha_on=0&username=${configurations.credentials.username}&passwd=${configurations.credentials.password}"
		logger.debug("Trying to login to ${configurations.routerAddress} with request body: \"$requestBody\"")

		val httpRequest = HttpRequest.newBuilder()
			.uri(URI("http://${configurations.routerAddress}/sess-bin/login_handler.cgi"))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.header("Referer", "http://${configurations.routerAddress}/sess-bin/login_session.cgi?logout=1")
			.POST(BodyPublishers.ofString(requestBody))
			.build()

		return HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString())
	}

	private fun getSessionId(): String {
		val responseBody = login().body()
		logger.debug("Response of the login request: $responseBody")

		return Regex("^ *setCookie\\(['\"]([0-9a-z]{16})['\"]\\).*$", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
			.find(responseBody)!!
			.groupValues[1]
	}
}
