package com.attacktive.vpnmanager.configuration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.assertThrows

class MudfishItemTest {
	@Test
	fun testGetToggleRequestBody() {
		val mudfishItem = MudfishItem("doesn't matter", "whatever", enabled = false, iid = "iid", rid = "rid")
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

	@Test
	fun testWithoutTestUrl() {
		val mudfishItemJson = """
			{
				"name": "jetbrains.com",
				"iid": "217789",
				"rid": "1475354"
		}
		""".trimIndent()

		assertThrows<SerializationException> { Json.decodeFromString(mudfishItemJson) }
	}

	@Test
	fun testWithTestUrls() {
		val mudfishItemJson = """
			{
				"name": "jetbrains.com",
				"iid": "217789",
				"testUrls": ["https://youtrack.jetbrains.com/"],
				"rid": "1475354"
		}
		""".trimIndent()

		val mudfishItem: MudfishItem = Json.decodeFromString(mudfishItemJson)
		assertEquals("https://youtrack.jetbrains.com/", mudfishItem.getUrlsToTest().first())
	}

	@Test
	fun testWithTestUrlAndTestUrlsAltogether() {
		val mudfishItemJson = """
			{
				"name": "jetbrains.com",
				"iid": "217789",
				"testUrl": "https://youtrack.jetbrains.com/",
				"testUrls": ["https://account.jetbrains.com/"],
				"rid": "1475354"
		}
		""".trimIndent()

		val mudfishItem: MudfishItem = Json.decodeFromString(mudfishItemJson)
		val urlsToTest = mudfishItem.getUrlsToTest()

		assertEquals(1, urlsToTest.size)
		assertEquals("https://youtrack.jetbrains.com/", urlsToTest.first())
	}
}
