package com.attacktive.vpnmanager.configuration

import kotlinx.serialization.Serializable

@Serializable
data class NullableConfigurations(val cronExpression: String?, val testTimeout: String?, val vpnToggleUrl: String?, val mudfishGraphqlUrl: String? = "https://api.mudfish.net/graphql", val authorization: String?, val mudfishItems: List<MudfishItem> = listOf())
