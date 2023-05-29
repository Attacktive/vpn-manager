package com.attacktive.vpnmanager.connectivity

import kotlinx.serialization.Serializable

@Serializable
data class ErrorsResponseDto(val error: String? = null, val errors: List<Error> = listOf()) {
	val message: String?
		get() {
			if (errors.isEmpty()) {
				return error
			} else {
				return errors.joinToString("\n") { it.message }
			}
		}

	@Serializable
	data class Error(val message: String)
}
