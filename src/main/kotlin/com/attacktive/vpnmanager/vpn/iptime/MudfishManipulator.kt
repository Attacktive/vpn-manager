package com.attacktive.vpnmanager.vpn.iptime

import com.attacktive.vpnmanager.vpn.VpnManipulator
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

class MudfishManipulator(private val router: Router): VpnManipulator {
	private val logger = LoggerFactory.getLogger(MudfishManipulator::class.java)

	override fun turnOn() {
		turnOnOrOffMudfish(true)
	}

	override fun turnOff() {
		turnOnOrOffMudfish(false)
	}

	private fun turnOnOrOffMudfish(toTurnOn: Boolean) {
		val sessionId = getSessionId()

		val httpRequest = HttpRequest.newBuilder(URI("http://10.0.0.1:8282/do/mudfish/${if (toTurnOn) "start" else "stop"}"))
			.header("Cookie", "efm_session_id=$sessionId")
			.POST(BodyPublishers.noBody())
			.build()

		val success = try {
			val statusCode = HttpClient.newHttpClient()
				.send(httpRequest) { HttpResponse.BodySubscribers.discarding() }
				.statusCode()

			statusCode < 400
		} catch (e: HttpConnectTimeoutException) {
			logger.error(e.message, e)
			false
		}

		if (!success) {
			logger.error("Failed to ${if (toTurnOn) "start" else "stop"} the VPN.")
		}
	}

	private fun login(): HttpResponse<String> {
		val requestBody = "init_status=1&captcha_on=0&username=${router.credentials.username}&passwd=${router.credentials.password}"

		val httpRequest = HttpRequest.newBuilder()
			.uri(URI(router.url))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.header("Referer", "http://10.0.0.1/sess-bin/login_session.cgi?logout=1")
			.POST(BodyPublishers.ofString(requestBody))
			.build()

		return HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString())
	}

	private fun getSessionId(): String {
		val response = login()

		return Regex("^ *setCookie\\(['\"]([0-9a-z]{16})['\"]\\).*$", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
			.find(response.body())!!
			.groupValues[1]
	}
}
