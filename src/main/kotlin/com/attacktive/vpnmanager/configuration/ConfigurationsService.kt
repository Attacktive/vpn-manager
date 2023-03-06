package com.attacktive.vpnmanager.configuration

import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Paths
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

object ConfigurationsService {
	private const val CONFIGURATIONS_FILE_NAME = "default-configurations.json"
	private const val CUSTOM_CONFIGURATIONS_FILE_NAME = "configurations.json"

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

		logger.debug("Loaded configurations:\n${Json.encodeToString(configurations)}")

		return configurations!!
	}

	private fun getDefaultConfigurations(): Configurations {
		val resource = ConfigurationsService::class.java.classLoader.getResource(CONFIGURATIONS_FILE_NAME) ?: throw FileNotFoundException("File $CONFIGURATIONS_FILE_NAME does not exist; returning the default which surely won't work.")

		val json = resource.readText().trim()

		return run { Json.decodeFromString(json) }
	}

	private fun getCustomConfigurations(): NullableConfigurations? {
		val configurationsDirectory = getCustomConfigurationsPath().toFile()
		val appConfigurationsDirectory = Paths.get(configurationsDirectory.absolutePath, "vpn-manager").toFile()

		if (appConfigurationsDirectory.exists() && appConfigurationsDirectory.isDirectory) {
			return appConfigurationsDirectory.listFiles()!!
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
					Json.decodeFromString(it.readText())
				}
		}

		appConfigurationsDirectory.mkdirs()
		return null
	}

	private fun getCustomConfigurationsPath(): Path {
		val os = System.getProperty("os.name")
		val home = System.getProperty("user.home")

		val config = if (os.contains("Mac")) {
			Paths.get(home, "Library", "Application Support")
		} else if (os.contains("Windows")) {
			getPathFromEnv("APPDATA", false, Paths.get(home, "AppData", "Roaming"))
		} else {
			getPathFromEnv("XDG_CONFIG_HOME", true, Paths.get(home, ".config"))
		}

		return config
	}

	private fun getPathFromEnv(envName: String, needsToBeAbsolute: Boolean, defaultPath: Path): Path {
		var path: Path
		val envValue = System.getenv(envName)
		if (envValue.isNullOrEmpty()) {
			path = defaultPath
			logger.info("$envName is not defined in env; falling back on \"$path\"")
		} else {
			path = Paths.get(envValue)

			if (needsToBeAbsolute && !path.isAbsolute) {
				path = defaultPath
				logger.info("$envName is not an absolute path; falling back on \"$path\"")
			}
		}

		return path
	}
}
