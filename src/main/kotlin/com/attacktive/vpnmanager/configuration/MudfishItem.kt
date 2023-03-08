package com.attacktive.vpnmanager.configuration

import kotlinx.serialization.Serializable

@Serializable
data class MudfishItem(val name: String, val testUrl: String, val enabled: Boolean = true, val iid: String, val rid: String) {
	override fun toString(): String {
		return "$name ${super.toString()}"
	}

	fun getToggleRequestBody(toTurnOn: Boolean) = """
		{
			"iid": "$iid",
			"rid": "$rid",
			"onoff": $toTurnOn
		}""".trimIndent()
}
