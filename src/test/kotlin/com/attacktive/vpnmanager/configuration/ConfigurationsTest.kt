package com.attacktive.vpnmanager.configuration

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ConfigurationsTest {
	@Test
	fun testMergeWithMudfishItems() {
		val default = Configurations("source", "100h", "source", "source", listOf(MudfishItem("source", "source", false, "source", "source")))

		val custom = NullableConfigurations(null, null, null, null, listOf(MudfishItem("custom", "custom", true, "custom", "custom")))

		val merged = default.mergeWith(custom)
		println("merged: $merged")

		assertEquals("source", merged.cronExpression)
		assertEquals("100h", merged.testTimeout)
		assertEquals("source", merged.vpnToggleUrl)
		assertEquals("source", merged.authorization)
		assertContentEquals(listOf(MudfishItem("custom", "custom", true, "custom", "custom")), merged.mudfishItems)
	}

	@Test
	fun testMergeOverwriting() {
		val default = Configurations("source", "100h", "source", "source", listOf(MudfishItem("source", "source", false, "source", "source")))

		val custom = NullableConfigurations("custom", "1s", "custom", "custom")

		val merged = default.mergeWith(custom)
		println("merged: $merged")

		assertEquals("custom", merged.cronExpression)
		assertEquals("1s", merged.testTimeout)
		assertEquals("custom", merged.vpnToggleUrl)
		assertEquals("custom", merged.authorization)
		assertContentEquals(listOf(MudfishItem("source", "source", false, "source", "source")), merged.mudfishItems)
	}
}
