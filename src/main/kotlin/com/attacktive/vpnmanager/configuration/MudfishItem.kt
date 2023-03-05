package com.attacktive.vpnmanager.configuration

import kotlinx.serialization.Serializable

@Serializable
data class MudfishItem(val name: String, val testUrl: String, val iid: String, val rid: String) {
	fun getToggleRequestBody(toTurnOn: Boolean) = """
		{
			"iid": "$iid",
			"rid": "$rid",
			"onoff": $toTurnOn
		}""".trimIndent()
}
