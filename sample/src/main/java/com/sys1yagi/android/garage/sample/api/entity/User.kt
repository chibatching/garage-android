package com.sys1yagi.android.garage.sample.api.entity

import com.google.gson.annotations.SerializedName

//{"id":1,"name":"alice","email":"alice@example.com","_links":{"posts":{"href":"/v1/users/1/posts"}}}
class User {

    @SerializedName("id")
    var id: Int = 0
    
    @SerializedName("name")
    var name: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("_links")
    lateinit var link: Link

    override fun toString(): String {
        return "User(id=$id, name='$name', email='$email', link=$link)"
    }

}
