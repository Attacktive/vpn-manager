package com.attacktive.vpnmanager.configuration

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ConfigurationsTest {
	@Test
	fun testMergeWithMudfishItems() {
		val sourceMudfishItems = listOf(MudfishItem(1, false))
		val default = Configurations("source-cron-1", "100h", "source-toggle-1", authorization = "source-authorization-1", mudfishItems = sourceMudfishItems)

		val customMudfishItems = listOf(MudfishItem(1, true))
		val custom = NullableConfigurations(null, null, null, authorization = null, mudfishItems = customMudfishItems)

		val merged = default.mergeWith(custom)
		println("merged: $merged")

		assertEquals("source-cron-1", merged.cronExpression)
		assertEquals("100h", merged.testTimeout)
		assertEquals("source-toggle-1", merged.vpnToggleUrl)
		assertEquals("source-authorization-1", merged.authorization)
		assertContentEquals(customMudfishItems, merged.mudfishItems)
	}

	@Test
	fun testMergeOverwriting() {
		val sourceMudfishItems = listOf(MudfishItem(2, false))
		val default = Configurations("source-cron-2", "100h", "source-toggle-2", authorization = "source-authorization-2", mudfishItems = sourceMudfishItems)

		val custom = NullableConfigurations("custom-cron-2", "1s", "custom-toggle-2", authorization = "custom-auth-2")

		val merged = default.mergeWith(custom)
		println("merged: $merged")

		assertEquals("custom-cron-2", merged.cronExpression)
		assertEquals("1s", merged.testTimeout)
		assertEquals("custom-toggle-2", merged.vpnToggleUrl)
		assertEquals("custom-auth-2", merged.authorization)
		assertContentEquals(sourceMudfishItems, merged.mudfishItems)
	}
}
