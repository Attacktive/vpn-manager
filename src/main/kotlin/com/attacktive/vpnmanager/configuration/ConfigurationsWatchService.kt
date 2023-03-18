package com.attacktive.vpnmanager.configuration

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import kotlin.io.path.pathString
import org.slf4j.LoggerFactory

object ConfigurationsWatchService {
	private val logger = LoggerFactory.getLogger(ConfigurationsWatchService.javaClass)

	fun watch(function: () -> Unit) {
		val customConfigurationsPath = ConfigurationsService.getCustomConfigurationsPath()
		FileSystems.getDefault()
			.newWatchService()
			.use {
				customConfigurationsPath.register(it, StandardWatchEventKinds.ENTRY_MODIFY)

				while (true) {
					val watchKey: WatchKey = it.take()
					for (event in watchKey.pollEvents()) {
						// we only register "ENTRY_MODIFY" so the context is always a Path.
						val changedFile = event.context() as Path
						logger.debug("$changedFile has just been changed.")

						if (changedFile.pathString.endsWith(".json", true)) {
							function()
						}
					}

					if (!watchKey.reset()) {
						logger.warn("The WatchKey has not been reset.")
					}
				}
			}
	}
}
