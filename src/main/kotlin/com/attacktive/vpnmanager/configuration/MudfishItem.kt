package com.attacktive.vpnmanager.configuration

import kotlinx.serialization.Serializable

@Serializable
data class MudfishItem(val iid: Int, val enabled: Boolean) {
	init {
		require(iid > 0) { "You must provide a valid iid." }
	}

	override fun toString(): String {
		return "MudfishItem #$iid (enabled: $enabled)"
	}
}
