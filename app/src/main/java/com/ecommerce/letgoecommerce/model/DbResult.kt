package com.ecommerce.letgoecommerce.model

import kotlin.properties.Delegates

class DbResult {
    var state by Delegates.notNull<Boolean>()
    lateinit var message: String
    var loginId: Int = -1
}


