package com.sys1yagi.android.garage.sample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.sys1yagi.android.garage.core.GarageClient
import com.sys1yagi.android.garage.core.config.*
import com.sys1yagi.android.garage.core.executor.Executor
import com.sys1yagi.android.garage.core.impl.DefaultAuthenticator
import com.sys1yagi.android.garage.core.impl.GsonConverter
import com.sys1yagi.android.garage.core.impl.OnMemoryAccessTokenContainer
import com.sys1yagi.android.garage.sample.api.UserApiClient
import com.sys1yagi.android.garage.sample.databinding.ActivityMainBinding
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val client: GarageClient by lazy {
        val authConfig = AuthenticatorConfiguration(OkHttpClient(), BuildConfig.garageEndpoint, BuildConfig.garageId, BuildConfig.garageSecret).apply {
            customPort = 3000
        }
        val authenticator = DefaultAuthenticator("sample@example.com", authConfig, OnMemoryAccessTokenContainer())

        val client = OkHttpClient()
        GarageClient(GarageConfiguration(
                RequestConfiguration(client, BuildConfig.garageEndpoint).apply {
                    customPort = 3000
                },
                authConfig,
                ExecutorConfiguration(Executor()),
                JsonConvertConfiguration(GsonConverter(Gson()))
        )).apply {
            addAuthenticator(authenticator)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.requestApi.setOnClickListener {
            request()
        }
    }

    fun request() {
        UserApiClient(client).getUsers(
                {
                    binding.result.text = it.fold("", { a, b ->
                        a + b.toString() + "\n"
                    })
                },
                {
                    binding.result.text = "error=${it.message}"
                }

        )
    }
}
