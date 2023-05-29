package com.attacktive.vpnmanager.mudfish

import kotlin.test.Test
import kotlin.test.assertEquals

class MudfishItemResponseDtoTest {
	@Test
	fun testCidr() {
		val mudfishItemResponseDto = MudfishItemResponseDto(
			MudfishItemResponseDto.Data(
				MudfishItemResponseDto.Data.User(
					MudfishItemResponseDto.Data.User.Item(666, "localhost", "127.0.0.1/32")
				)
			)
		)

		assertEquals("http://127.0.0.1", mudfishItemResponseDto.routingUrlSet.first())
	}

	@Test
	fun testMultipleCidrs() {
		val mudfishItemResponseDto = MudfishItemResponseDto(
			MudfishItemResponseDto.Data(
				MudfishItemResponseDto.Data.User(
					MudfishItemResponseDto.Data.User.Item(666, "localhost", "127.0.0.1/32\r\n\r\n192.168.0.1/18")
				)
			)
		)

		assertEquals("http://127.0.0.1", mudfishItemResponseDto.routingUrlSet.first())
	}

	@Test
	fun testDomain() {
		val mudfishItemResponseDto = MudfishItemResponseDto(
			MudfishItemResponseDto.Data(
				MudfishItemResponseDto.Data.User(
					MudfishItemResponseDto.Data.User.Item(666, "Jetbrains", "youtrack.jetbrains.com")
				)
			)
		)

		assertEquals("http://youtrack.jetbrains.com", mudfishItemResponseDto.routingUrlSet.first())
	}

	@Test
	fun testMultipleDomains() {
		val mudfishItemResponseDto = MudfishItemResponseDto(
			MudfishItemResponseDto.Data(
				MudfishItemResponseDto.Data.User(
					MudfishItemResponseDto.Data.User.Item(666, "Jetbrains", "http://jetbrains.com\r\nyoutrack.jetbrains.com")
				)
			)
		)

		assertEquals(
			setOf("http://jetbrains.com", "http://youtrack.jetbrains.com"),
			mudfishItemResponseDto.routingUrlSet
		)
	}
}
