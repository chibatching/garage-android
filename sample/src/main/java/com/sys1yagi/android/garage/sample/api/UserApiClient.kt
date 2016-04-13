package com.sys1yagi.android.garage.sample.api

import com.google.gson.reflect.TypeToken
import com.sys1yagi.android.garage.core.GarageClient
import com.sys1yagi.android.garage.sample.api.entity.User
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class UserApiClient(val garageClient: GarageClient) {

    companion object {
        val PATH = V1("users")
        val TYPE_LIST = object : TypeToken<List<User>>() {}.type
    }

    fun getUsers(success: (List<User>) -> Unit, fail: (Throwable) -> Unit) {
        garageClient.get(PATH)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            val users: List<User> = garageClient.config.jsonConvertConfiguration.converter
                                    .fromJson(response.body().string(), TYPE_LIST)
                            success.invoke(users)
                        },
                        { error ->
                            fail.invoke(error)
                        }
                )
    }
}
