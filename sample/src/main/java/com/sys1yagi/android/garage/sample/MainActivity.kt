package com.sys1yagi.android.garage.sample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.sys1yagi.android.garage.GarageClient
import com.sys1yagi.android.garage.GarageConfiguration
import com.sys1yagi.android.garage.impl.DefaultAuthenticator
import com.sys1yagi.android.garage.sample.api.UserApiClient
import com.sys1yagi.android.garage.sample.databinding.ActivityMainBinding
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val client: GarageClient by lazy {
        val config = GarageConfiguration.make(BuildConfig.garageId, BuildConfig.garageSecret, BuildConfig.garageEndpoint, OkHttpClient()) {
            port = 3000
            callbackHandler = Handler()
            authenticator = DefaultAuthenticator("sample@example.com")
        }
        GarageClient(config)
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
