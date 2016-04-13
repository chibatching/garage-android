package com.sys1yagi.android.garage.core.config

import okhttp3.OkHttpClient

class AuthenticatorConfiguration(
        client: OkHttpClient,
        endpoint: String,
        val applicationId: String,
        val applicationSecret: String
)
: RequestConfiguration(client, endpoint)
