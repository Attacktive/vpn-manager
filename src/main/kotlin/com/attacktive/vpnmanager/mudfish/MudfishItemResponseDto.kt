package com.attacktive.vpnmanager.mudfish

import kotlinx.serialization.Serializable

@Serializable
data class MudfishItemResponseDto(val data: Data) {
	val routingUrlSet: Set<String>
		get() = data.user.item.routingUrlSet

	@Serializable
	data class Data(val user: User) {
		@Serializable
		data class User(val item: Item) {
			@Serializable
			@Suppress("HttpUrlsUsage")
			data class Item(val iid: Int, val name: String, val rtList: String) {
				val routingUrlSet: Set<String>
					get() = rtList.split("\r", "\n")
						.filter { it.isNotEmpty() }
						.map {
							val cidrRegex = Regex("([0-9a-f.:]+)/[0-9]+", RegexOption.IGNORE_CASE)
							val cidrMatchResult = cidrRegex.find(it)

							if (cidrMatchResult?.groups?.isNotEmpty() == true) {
								"http://${cidrMatchResult.groupValues[1]}"
							} else {
								val httpRegex = Regex("(?:https?://)?(.+)", RegexOption.IGNORE_CASE)
								"http://${httpRegex.find(it)!!.groupValues[1]}"
							}
						}
						.toSet()
			}
		}
	}
}
