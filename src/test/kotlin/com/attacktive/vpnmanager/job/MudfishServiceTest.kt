package com.attacktive.vpnmanager.job

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.attacktive.vpnmanager.configuration.Configurations
import com.attacktive.vpnmanager.configuration.ConfigurationsService
import com.attacktive.vpnmanager.configuration.MudfishItem
import com.attacktive.vpnmanager.connectivity.ConnectivityChecker
import com.attacktive.vpnmanager.mudfish.MudfishService
import org.mockito.Mockito

class MudfishServiceTest {
	private val mockConfigurationsService = Mockito.mock(ConfigurationsService::class.java)
	private val mockConnectivityChecker = Mockito.mock(ConnectivityChecker::class.java)

	@Test
	fun testTurnOn() {
		val name = "test-site"
		val testUrl = "http://localhost"
		val enabled = true
		val iid = "iid-1"
		val rid = "rid-1"

		val mudfishItem = MudfishItem(name, testUrl, enabled, iid, rid)

		Mockito.`when`(mockConfigurationsService.getConfigurations())
			.thenReturn(
				Configurations(
					"0 0 0 * * ?",
					"30s",
					"http://toggle.vpn",
					"random-token-1",
					listOf(mudfishItem)
				)
			)

		Mockito.`when`(mockConnectivityChecker.needsVpn(mudfishItem)).thenReturn(true)

		MudfishService.turnOff(mudfishItem)
		val needsVpn = ConnectivityChecker.needsVpn(mudfishItem)
		assertTrue(needsVpn)

		val result = MudfishService.turnOn(mudfishItem)
		assertTrue(result)
	}

	@Test
	fun testTurnOffWhenDisabled() {
		val name = "test-site"
		val testUrl = "http://localhost"
		val enabled = false
		val iid = "iid-2"
		val rid = "rid-2"

		val mudfishItem = MudfishItem(name, testUrl, enabled, iid, rid)

		Mockito.`when`(mockConfigurationsService.getConfigurations())
			.thenReturn(
				Configurations(
					"0 0 * * * ?",
					"2m",
					"http://toggle.vpn",
					"random-token-2",
					listOf(mudfishItem)
				)
			)

		Mockito.`when`(mockConnectivityChecker.needsVpn(mudfishItem)).thenReturn(false)

		MudfishService.turnOn(mudfishItem)
		val needsVpn = ConnectivityChecker.needsVpn(mudfishItem)
		assertTrue(needsVpn)

		val result = MudfishService.turnOff(mudfishItem)
		assertFalse(result)
	}
}
