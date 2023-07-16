package com.attacktive.vpnmanager.configuration

import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import kotlin.system.exitProcess
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

object ConfigurationsService {
	private const val CONFIGURATIONS_FILE_NAME = "default-configurations.json"
	private const val CUSTOM_CONFIGURATIONS_FILE_NAME = "configurations.json"

	private val logger = LoggerFactory.getLogger(ConfigurationsService::class.java)
	private val json = Json { ignoreUnknownKeys = true }

	private var configurations: Configurations? = null

	fun getConfigurations(): Configurations {
		configurations = getDefaultConfigurations()

		val customConfigurations = getCustomConfigurations()
		if (customConfigurations == null) {
			val customConfigurationPath = getCustomConfigurationsPath().toFile().absolutePath
			val newCustomConfigurationFile = Paths.get(customConfigurationPath, CUSTOM_CONFIGURATIONS_FILE_NAME).toFile()
			newCustomConfigurationFile.writeText(Json.encodeToString(configurations))
		} else {
			configurations = configurations!!.mergeWith(customConfigurations)
		}

		logger.debug("Loaded configurations:\n${Json.encodeToString(configurations)}")

		return configurations!!
	}

	fun getCustomConfigurationsPath(): Path {
		val os = System.getProperty("os.name")
		val home = System.getProperty("user.home")

		val config = if (os.contains("Mac")) {
			Paths.get(home, "Library", "Application Support")
		} else if (os.contains("Windows")) {
			getPathFromEnv("APPDATA", false, Paths.get(home, "AppData", "Roaming"))
		} else {
			getPathFromEnv("XDG_CONFIG_HOME", true, Paths.get(home, ".config"))
		}

		return Paths.get(config.absolutePathString(), "vpn-manager")
	}

	private fun getDefaultConfigurations(): Configurations {
		val resource = ConfigurationsService::class.java.classLoader.getResource(CONFIGURATIONS_FILE_NAME) ?: throw FileNotFoundException("File $CONFIGURATIONS_FILE_NAME does not exist; returning the default which surely won't work.")

		val json = resource.readText().trim()

		return run { Json.decodeFromString(json) }
	}

	private fun getCustomConfigurations(): NullableConfigurations? {
		val configurationsDirectory = getCustomConfigurationsPath().toFile()

		if (configurationsDirectory.exists() && configurationsDirectory.isDirectory) {
			return configurationsDirectory.listFiles()!!
				.sortedWith { left, right ->
					when {
						left.name.equals(right.name) -> 0
						left.name.equals(CUSTOM_CONFIGURATIONS_FILE_NAME) -> -1
						right.name.equals(CUSTOM_CONFIGURATIONS_FILE_NAME) -> 1
						else -> left.name.compareTo(right.name)
					}
				}
				.filter { it.extension.equals("json", true) }
				.firstNotNullOfOrNull {
					logger.debug("Custom configuration file \"$it\" is chosen.")
					try {
						json.decodeFromString(it.readText())
					} catch (serializationException: SerializationException) {
						logger.error(serializationException.message, serializationException)
						exitProcess(1)
					}
				}
		}

		configurationsDirectory.mkdirs()
		return null
	}

	private fun getPathFromEnv(envName: String, needsToBeAbsolute: Boolean, defaultPath: Path): Path {
		var path: Path
		val envValue = System.getenv(envName)
		if (envValue.isNullOrEmpty()) {
			path = defaultPath
			logger.debug("$envName is not defined in env; falling back on \"$path\"")
		} else {
			path = Paths.get(envValue)

			if (needsToBeAbsolute && !path.isAbsolute) {
				path = defaultPath
				logger.debug("$envName is not an absolute path; falling back on \"$path\"")
			}
		}

		return path
	}
}
