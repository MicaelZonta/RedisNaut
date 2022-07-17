package com.redis.controller

import com.redis.controller.transferobject.request.SaveItem
import io.lettuce.core.api.StatefulRedisConnection
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post

@Controller("/hello")
class HelloPort (
    private val redisConnection: StatefulRedisConnection<String, String>
) {

    @Get
    fun Get(key: String): String {
        val commands = redisConnection.sync()
        return commands.get(key)
    }

    @Post
    fun Save(saveItem: SaveItem): String {
        println("Key ${saveItem.key}")
        println("Value ${saveItem.value}")
        val commands = redisConnection.sync()
        commands.set(saveItem.key, saveItem.value)
        return "Sucesso"
    }

}