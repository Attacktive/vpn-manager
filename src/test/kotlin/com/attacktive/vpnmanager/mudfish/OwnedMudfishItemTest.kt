package com.attacktive.vpnmanager.mudfish

import kotlin.test.Test
import kotlin.test.assertEquals

class OwnedMudfishItemTest {
	@Test
	fun testGetToggleRequestBody() {
		val ownedMudfishItem = OwnedMudfishItem(666, 5150, "doesn't matter")
		val toggleRequestBody = ownedMudfishItem.getToggleRequestBody(true)

		assertEquals(
			"""
				{
					"iid": 666,
					"rid": 5150,
					"onoff": true
				}
			""".trimIndent(),
			toggleRequestBody
		)
	}
}
