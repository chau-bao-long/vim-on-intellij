package com.longcb.vimonintellij.neovim

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.msgpack.core.MessageFormatException
import java.io.IOException
import java.util.concurrent.CompletableFuture

class RequestCallback<T>(type: JavaType?) {

    val completableFuture = CompletableFuture<T>()

    private val deserializer: IOBiFunction<ObjectMapper, JsonNode, T> = object :
        IOBiFunction<ObjectMapper, JsonNode, T> {
        override fun call(objectMapper: ObjectMapper, node: JsonNode): T {
            return objectMapper.readValue(
                node.traverse(),
                type,
            )
        }
    }

    fun setResult(objectMapper: ObjectMapper, result: JsonNode) {
        try {
            completableFuture.complete(deserializer.call(objectMapper, result))
        } catch (e: IOException) {
            completableFuture.completeExceptionally(e)
        } catch (e: MessageFormatException) {
            completableFuture.completeExceptionally(e)
        }
    }

    fun setError(error: NeovimException?) {
        completableFuture.completeExceptionally(error)
    }
}
