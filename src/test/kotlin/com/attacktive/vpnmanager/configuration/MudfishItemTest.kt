package com.attacktive.vpnmanager.configuration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MudfishItemTest {
	@Test
	fun testGetToggleRequestBody() {
		val mudfishItem = MudfishItem("doesn't matter", "whatever", false, "iid", "rid")
		val toggleRequestBody = mudfishItem.getToggleRequestBody(true)

		assertEquals(
			"""
				{
					"iid": "iid",
					"rid": "rid",
					"onoff": true
				}
			""".trimIndent(),
			toggleRequestBody
		)
	}

	@Test
	fun testIfEnabledIsTrueByDefault() {
		val mudfishItemJson = """
			{
				"name": "jetbrains.com",
				"testUrl": "https://youtrack.jetbrains.com/",
				"iid": "217789",
				"rid": "1475354"
		}
		""".trimIndent()

		val mudfishItem: MudfishItem = Json.decodeFromString(mudfishItemJson)
		assertTrue(mudfishItem.enabled)
	}
}
