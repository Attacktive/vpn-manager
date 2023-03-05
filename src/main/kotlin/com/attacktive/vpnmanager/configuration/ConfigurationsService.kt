package com.attacktive.vpnmanager.configuration

import java.io.FileNotFoundException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

object ConfigurationsService {
	private const val CONFIGURATIONS_FILE_NAME = "configurations.json"

	private val logger = LoggerFactory.getLogger(ConfigurationsService::class.java)

	private var configurations: Configurations? = null

	fun getConfigurations(): Configurations {
		if (configurations == null) {
			val resource = ConfigurationsService::class.java.classLoader.getResource(CONFIGURATIONS_FILE_NAME) ?: throw FileNotFoundException("File $CONFIGURATIONS_FILE_NAME does not exist; returning the default which surely won't work.")

			val json = resource.readText().trim()
			logger.debug("Deserialized $CONFIGURATIONS_FILE_NAME as:\n$json")

			configurations = run { Json.decodeFromString(json) }
		}

		return configurations!!
	}
}
