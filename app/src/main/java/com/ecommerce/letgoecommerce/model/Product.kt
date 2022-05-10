package com.ecommerce.letgoecommerce.model

import android.graphics.Bitmap
import android.net.Uri
import java.io.Serializable

data class Product(
    var id: Int? = null,
    val categoryId: Int,
    val userId: Int,
    var header: String,
    var price: Float,
    var description: String? = null,
    var pictureList: List<Uri>? = null,
    var bitmapList: List<Bitmap>? = null,
    var state: Int? = null,
    var createdDate: String? = null,
    var carExtension: CarExtension? = null
) : Serializable
