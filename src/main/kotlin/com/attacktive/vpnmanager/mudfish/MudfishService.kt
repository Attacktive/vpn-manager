package com.attacktive.vpnmanager.mudfish

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import com.attacktive.vpnmanager.configuration.ConfigurationsService
import com.attacktive.vpnmanager.configuration.MudfishItem
import org.slf4j.LoggerFactory

object MudfishService {
	private val logger = LoggerFactory.getLogger(MudfishService::class.java)
	private val configurations = ConfigurationsService.getConfigurations()

	fun turnOn(mudfishItem: MudfishItem) {
		turnOnOrOffMudfish(mudfishItem, true)
	}

	fun turnOff(mudfishItem: MudfishItem) {
		turnOnOrOffMudfish(mudfishItem, false)
	}

	private fun turnOnOrOffMudfish(mudfishItem: MudfishItem, toTurnOn: Boolean) {
		val httpRequest = HttpRequest.newBuilder(URI(configurations.vpnToggleUrl))
			.header("Authorization", configurations.authorization)
			.header("Content-Type", "application/json;charset=UTF-8")
			.POST(BodyPublishers.ofString(mudfishItem.getToggleRequestBody(toTurnOn)))
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
			logger.error("Failed to ${if (toTurnOn) "start" else "stop"} the VPN for \"${mudfishItem.name}\".")
		}
	}
}
