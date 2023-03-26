package com.redis.controller.transferobject.request

import io.micronaut.core.annotation.Introspected

@Introspected
data class EncryptRequest(
    val sugar: String,
    val palavra: String,
)
