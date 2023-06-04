package com.attacktive.vpnmanager.connectivity

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers
import com.attacktive.vpnmanager.configuration.ConfigurationsService
import com.attacktive.vpnmanager.mudfish.MudfishService
import com.attacktive.vpnmanager.mudfish.OwnedMudfishItem
import org.slf4j.LoggerFactory

object ConnectivityChecker {
	private val logger = LoggerFactory.getLogger(ConnectivityChecker::class.java)
	private val configurations = ConfigurationsService.getConfigurations()

	fun needsVpn(mudfishItem: OwnedMudfishItem): Boolean {
		MudfishService.getUrlsToTest(mudfishItem.iid)
			.forEach {
				val httpRequest = HttpRequest.newBuilder(URI(it))
					.timeout(configurations.testTimeoutDuration())
					.GET()
					.build()

				try {
					val statusCode = HttpClient.newHttpClient()
						.send(httpRequest) { BodySubscribers.discarding() }
						.statusCode()

					logger.debug("$it: $statusCode")

					if (statusCode >= 400) {
						return true
					}
				} catch (ioException: IOException) {
					logger.info("${ioException.message ?: ioException.javaClass.name} reaching $it")
					return true
				}
			}

		return false
	}
}
