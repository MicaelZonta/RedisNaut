package com.redis.controller.transferobject.request

import io.micronaut.core.annotation.Introspected

data class SaveItem(
    val key: String,
    val value: String
)
