package com.ecommerce.letgoecommerce.model

import java.io.Serializable

data class User(
    var id: Int? = null,
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val city: String?=null,
    val cityId: Int?=null,
    var photo_url: String? = null,
):Serializable

