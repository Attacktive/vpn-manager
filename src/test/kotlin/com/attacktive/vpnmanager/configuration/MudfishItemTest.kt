package com.attacktive.vpnmanager.configuration

import kotlin.test.Test
import kotlin.test.assertEquals

class MudfishItemTest {
	@Test
	fun test() {
		val mudfishItem = MudfishItem("doesn't matter", "whatever", "iid", "rid")
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
}
