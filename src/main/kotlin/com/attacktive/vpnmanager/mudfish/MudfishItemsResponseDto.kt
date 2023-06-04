package com.attacktive.vpnmanager.mudfish

import kotlinx.serialization.Serializable

typealias OwnedMudfishItem = MudfishItemsResponseDto.Data.User.Item

@Serializable
data class MudfishItemsResponseDto(private val data: Data) {
	fun items(): List<Data.User.Item> = data.user.items

	@Serializable
	data class Data(internal val user: User) {
		@Serializable
		data class User(internal val items: List<Item>) {
			@Serializable
			data class Item(val iid: Int, val rid: Int, val name: String) {
				fun getToggleRequestBody(toTurnOn: Boolean) = """
					{
						"iid": $iid,
						"rid": $rid,
						"onoff": $toTurnOn
					}""".trimIndent()
			}
		}
	}
}
