package com.ecommerce.letgoecommerce.model

import java.io.Serializable

data class Filter(
    val categoryId: Int,
    val cityId: Int?=null,
    val maxPrice: Int? = null,
    val minPrice: Int? = null,

    val minYear: Int? = null,
    val maxYear: Int? = null,
    val minKm: Int? = null,
    val maxKm: Int? = null,

    val carType: String? = null,
    val carColor: String? = null,
    val carEngine: String? = null,
    val carGear: String? = null,
    val carFuel: String? = null,
    val carTraction: String? = null,


    ):Serializable