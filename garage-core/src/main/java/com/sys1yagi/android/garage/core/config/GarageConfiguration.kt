package com.sys1yagi.android.garage.core.config

data class GarageConfiguration(
        val requestConfiguration: RequestConfiguration,
        val authenticationConfiguration: AuthenticatorConfiguration,
        val executorConfiguration: ExecutorConfiguration,
        val jsonConvertConfiguration: JsonConvertConfiguration) {
}
