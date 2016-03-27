package com.sys1yagi.android.garage.sample.api

import com.google.gson.reflect.TypeToken
import com.sys1yagi.android.garage.GarageClient
import com.sys1yagi.android.garage.sample.api.entity.User

class UserApiClient(val garageClient: GarageClient) {

    companion object {
        val PATH = V1("users")
        val TYPE_LIST = object : TypeToken<List<User>>() {}.type
    }

    fun getUsers(success: (List<User>) -> Unit, fail: (Exception) -> Unit) {
        garageClient.get(PATH).enqueue(
                { c, r ->
                    val users: List<User> = garageClient.configuration.gson.fromJson(r.body().string(), TYPE_LIST)
                    success.invoke(users)
                },
                { c, e ->
                    fail.invoke(e)
                }
        )
    }
}
