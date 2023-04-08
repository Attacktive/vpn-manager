package com.attacktive.vpnmanager.configuration

import kotlinx.serialization.Serializable

/**
	* @param testUrl A URL to test if the connection to the target site is live.
 * Takes priority over [testUrls] if provided.
 * @param testUrls URLs to test if the connection to the target site is live.
 * Gets ignored if [testUrl] is provided.
 */
@Serializable
data class MudfishItem(val name: String, private val testUrl: String? = null, private val testUrls: Set<String> = setOf(), val enabled: Boolean = true, val iid: String, val rid: String) {
	init {
		require(iid.isNotBlank()) { "You must provide a valid iid for \"$name\"." }
		require(rid.isNotBlank()) { "You must provide a valid rid for \"$name\"." }
	}

	override fun toString(): String {
		return "$name ${super.toString()}"
	}

	fun getUrlsToTest(): Set<String> {
		if (testUrl?.isNotBlank() == true) {
			return setOf(testUrl)
		}

		return testUrls
	}

	fun getToggleRequestBody(toTurnOn: Boolean) = """
		{
			"iid": "$iid",
			"rid": "$rid",
			"onoff": $toTurnOn
		}""".trimIndent()
}
