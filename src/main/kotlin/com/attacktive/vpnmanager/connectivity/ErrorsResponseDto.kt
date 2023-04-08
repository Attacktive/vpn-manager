package com.attacktive.vpnmanager.connectivity

import kotlinx.serialization.Serializable

@Serializable
data class ErrorsResponseDto(val errors: List<Error>) {
	private val messages: List<String>
		get() = errors.map { it.message }

	val mergedMessages: String
		get() = messages.joinToString("\n")

	@Serializable
	data class Error(val message: String)
}
