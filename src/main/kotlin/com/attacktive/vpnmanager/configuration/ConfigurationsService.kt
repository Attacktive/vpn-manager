package com.attacktive.vpnmanager.configuration

import java.io.FileNotFoundException
import java.nio.file.Paths
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

object ConfigurationsService {
	private const val CONFIGURATIONS_FILE_NAME = "default-configurations.json"

	private val logger = LoggerFactory.getLogger(ConfigurationsService::class.java)

	private var configurations: Configurations? = null

	fun getConfigurations(): Configurations {
		if (configurations == null) {
			configurations = getDefaultConfigurations()

			val customConfigurations = getCustomConfigurations()
			if (customConfigurations != null) {
				configurations = configurations!!.mergeWith(customConfigurations)
			}
		}

		logger.debug("Using configurations:\n${Json.encodeToString(configurations)}")

		return configurations!!
	}

	private fun getDefaultConfigurations(): Configurations {
		val resource = ConfigurationsService::class.java.classLoader.getResource(CONFIGURATIONS_FILE_NAME) ?: throw FileNotFoundException("File $CONFIGURATIONS_FILE_NAME does not exist; returning the default which surely won't work.")

		val json = resource.readText().trim()

		return run { Json.decodeFromString(json) }
	}

	private fun getCustomConfigurations(): NullableConfigurations? {
		// FIXME: hacky workaround to get the 'resources' directory
		val resource = ConfigurationsService::class.java.classLoader.getResource("")
		val classpathRoot = Paths.get(resource!!.toURI()).toFile()
		val kotlin = classpathRoot.parentFile
		val classes = kotlin.parentFile
		val build = classes.parentFile
		val resources = build.listFiles()!!.firstOrNull { it.isDirectory && it.name.equals("resources") }
		val main = resources?.listFiles()!!.firstOrNull { it.isDirectory && it.name.equals("main") }

		return main?.listFiles()!!
			.filter { !it.name.equals(CONFIGURATIONS_FILE_NAME) }
			.filter { it.extension.equals("json", true) }
			.firstNotNullOfOrNull {
				logger.debug("Custom configuration file \"$it\" is chosen.")
				Json.decodeFromString(it.readText())
			}
	}
}
