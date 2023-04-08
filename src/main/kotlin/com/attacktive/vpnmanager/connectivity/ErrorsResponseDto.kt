package com.attacktive.vpnmanager.connectivity

import kotlinx.serialization.Serializable

@Serializable
data class ErrorsResponseDto(val errors: List<Error>) {
	val messages: List<String>
		get() = errors.map { it.message }

	@Serializable
	data class Error(val message: String)
}
