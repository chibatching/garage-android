package com.sys1yagi.android.garage.sample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.sys1yagi.android.garage.GarageClient
import com.sys1yagi.android.garage.GarageConfiguration
import com.sys1yagi.android.garage.impl.DefaultAuthenticator
import com.sys1yagi.android.garage.sample.api.V1
import com.sys1yagi.android.garage.sample.databinding.ActivityMainBinding
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val client: GarageClient by lazy {
        val config = GarageConfiguration.make {
            port = 3000
            endpoint = BuildConfig.garageEndpoint
            client = OkHttpClient()
            callbackHandler = Handler()
            applicationId = BuildConfig.garageId
            applicationSecret = BuildConfig.garageSecret
            authenticator = DefaultAuthenticator("morizyun@example.com")
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
        client.get(V1("users"))
                .enqueue(
                        { c, r ->
                            binding.result.text = r.body().string()
                        },
                        { c, e ->
                            binding.result.text = "error=${e.message}"
                        })
    }
}
