package com.attacktive.vpnmanager.mudfish

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import com.attacktive.vpnmanager.configuration.ConfigurationsService
import com.attacktive.vpnmanager.configuration.MudfishItem
import com.attacktive.vpnmanager.connectivity.ErrorsResponseDto

import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

object MudfishService {
	private val logger = LoggerFactory.getLogger(MudfishService::class.java)
	private val json = Json { ignoreUnknownKeys = true }
	private val configurations = ConfigurationsService.getConfigurations()

	fun retrieveItems(): List<OwnedMudfishItems> {
		val graphQL = """
			{
				user {
					items {
						iid
						rid
						name
					}
				}
			}""".trimIndent()

		val query = "{\"query\": \"$graphQL\" }"
			.replace(Regex("\\s+"), " ")

		val mudfishItemResponseDto = requestGraphql<MudfishItemsResponseDto>(query)

		return mudfishItemResponseDto.items()
	}

	fun turnOn(mudfishItem: MudfishItem): Boolean = turnOnOrOffMudfish(mudfishItem, true)

	fun turnOff(mudfishItem: MudfishItem): Boolean = turnOnOrOffMudfish(mudfishItem, false)

	private fun turnOnOrOffMudfish(mudfishItem: MudfishItem, toTurnOn: Boolean): Boolean {
		if (!mudfishItem.enabled) {
			logger.info("The item $mudfishItem is disabled; keeping it as-is.")
			return false
		}

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

		return success
	}

	fun getUrlsToTest(iid: String): Set<String> {
		val graphQL = """
			query CustomItemConf(${"$"}iid: Int!) {
				user {
					item(iid: ${"$"}iid) {
						categoryId
						iid
						name
						rtList
						destinations {
							rid
							location
							ip
							isPrivate
						}
					}
				}
			}""".trimIndent()

		val query = """
			{
				"query": "$graphQL",
				"variables": {
					"iid": "$iid"
				}
			}""".trimIndent()
			.replace(Regex("\\s+"), " ")

		val mudfishItemResponseDto = requestGraphql<MudfishItemResponseDto>(query)

		return mudfishItemResponseDto.routingUrlSet
	}

	private inline fun <reified T> requestGraphql(body: String): T {
		val httpRequest = HttpRequest.newBuilder(URI(configurations.mudfishGraphqlUrl))
			.header("Authorization", configurations.authorization)
			.header("Content-Type", "application/json;charset=UTF-8")
			.POST(BodyPublishers.ofString(body))
			.build()

		val httpResponse = HttpClient.newHttpClient()
			.send(httpRequest) { HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8) }

		val statusCode = httpResponse.statusCode()
		val responseJson = httpResponse.body()
		if (statusCode >= 400) {
			// kotlinx.serialization.json.internal.JsonDecodingException
			val errorResponse: ErrorsResponseDto = json.decodeFromString(responseJson)
			throw IllegalArgumentException("body: ${body}\n${errorResponse.message}")
		}

		return json.decodeFromString(responseJson)
	}
}
