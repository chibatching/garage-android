package com.sys1yagi.android.garage.sample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.sys1yagi.android.garage.sample.api.GarageClient
import com.sys1yagi.android.garage.sample.api.GarageConfiguration
import com.sys1yagi.android.garage.sample.databinding.ActivityMainBinding
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val client: GarageClient by lazy {
        val config = GarageConfiguration.make {
            port = 3000
            endpoint = "10.0.2.2"
            client = OkHttpClient()
            callbackHandler = Handler()
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
        //        client.get(Path("users"),
        //                { c, r ->
        //                    binding.result.text = r.body().string()
        //                },
        //                { c, e ->
        //                    binding.result.text = "error=${e.message}"
        //                })
    }
}
