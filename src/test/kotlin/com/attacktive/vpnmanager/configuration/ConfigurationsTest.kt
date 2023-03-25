package com.attacktive.vpnmanager.configuration

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ConfigurationsTest {
	@Test
	fun testMergeWithMudfishItems() {
		val sourceMudfishItems = listOf(MudfishItem("source-name-1", "source-test-url-1", enabled = false, iid = "source-iid-1", rid = "source-rid-1"))
		val default = Configurations("source-cron-1", "100h", "source-toggle-1", "source-authorization-1", sourceMudfishItems)

		val customMudfishItems = listOf(MudfishItem("custom-name-1", "custom-test-url-1", enabled = true, iid = "custom-iid", rid = "custom-rid"))
		val custom = NullableConfigurations(null, null, null, null, customMudfishItems)

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
		val sourceMudfishItems = listOf(MudfishItem("source-name", "source-test-url", enabled = false, iid = "source-iid", rid = "source-rid"))
		val default = Configurations("source-cron-2", "100h", "source-toggle-2", "source-authorization-2", sourceMudfishItems)

		val custom = NullableConfigurations("custom-cron-2", "1s", "custom-toggle-2", "custom-auth-2")

		val merged = default.mergeWith(custom)
		println("merged: $merged")

		assertEquals("custom-cron-2", merged.cronExpression)
		assertEquals("1s", merged.testTimeout)
		assertEquals("custom-toggle-2", merged.vpnToggleUrl)
		assertEquals("custom-auth-2", merged.authorization)
		assertContentEquals(sourceMudfishItems, merged.mudfishItems)
	}
}
