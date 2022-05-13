package com.ecommerce.letgoecommerce.model

import java.io.Serializable

data class Message(
    val id: Int? = null,
    val receiver_id: Int? = null,
    val sender_id: Int? = null,
    val product_id: Int? = null,
    val message: String? = null,
    var created_date: String? = null,
    ):Serializable
