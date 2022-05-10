package com.ecommerce.letgoecommerce.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class CategoryModel(
    val id: Int? = null,
    val name: String,
    @DrawableRes var image: Int? = null,
    @ColorRes var color: Int? = null,
    val topId: Int? = null
)
