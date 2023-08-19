package com.longcb.vimonintellij.neovim

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkState
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.longcb.vimonintellij.neovim.notificationhandler.NotificationHandlerFactory
import com.longcb.vimonintellij.neovim.requesthandler.RequestHandlerFactory
import org.msgpack.jackson.dataformat.MessagePackFactory
import java.io.IOException
import java.io.UncheckedIOException
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class NeovimApi(private val connection: Connection) : Disposable {
    private val logger = Logger.getInstance(javaClass)
    private var receiverFuture: Future<*>? = null
    private val callbacks: ConcurrentMap<Long, RequestCallback<*>> = ConcurrentHashMap()
    private val executorService: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }
    private val objectMapper = ObjectMapper(MessagePackFactory())
        .registerKotlinModule()
        .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
        .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)

    private val notificationHandlerFactory = NotificationHandlerFactory()
    private val requestHandlerFactory = RequestHandlerFactory()

    init {
        startReadingInputStream()
    }

    fun moveCursor(offset: Int) {
        val request = Request(
            method = "nvim_exec_lua",
            args = listOf("require'intellij-on-vim'.move_cursor($offset)", listOf<Any>()),
        )

        sendRequest(request, Any::class.java)
    }

    fun openFile(path: String) {
        val request = Request(method = "nvim_command", args = listOf("edit $path"))

        sendRequest(request, Any::class.java)
    } 

    fun getVimInfo(): CompletableFuture<Any> {
        val request = Request(method = "nvim_get_api_info", args = emptyList())
        return sendRequest(request, Any::class.java)
    }

    private fun startReadingInputStream() {
        checkState(receiverFuture == null, "Already Started")
        receiverFuture = executorService.submit(::readFromInput)
    }

    private fun <T> sendRequest(request: Request, callback: RequestCallback<T>): CompletableFuture<T> {
        callbacks.putIfAbsent(request.id, callback)

        try {
            send(request)
        } catch (e: IOException) {
            logger.error("Error when send request: $e")

            callbacks.remove(request.id)
            callback.completableFuture.completeExceptionally(e)

            throw UncheckedIOException(e)
        }
        return callback.completableFuture
    }

    private fun <T> sendRequest(request: Request, resultTypeReference: TypeReference<T>): CompletableFuture<T> {
        return sendRequest(
            request,
            RequestCallback(objectMapper.constructType(resultTypeReference.type)),
        )
    }

    private fun <T> sendRequest(request: Request, resultClass: Class<T>?): CompletableFuture<T> {
        return sendRequest(
            request,
            RequestCallback(objectMapper.constructType(resultClass)),
        )
    }

    private fun send(message: Message) {
        logger.info("Sent message: $message")

        val outputStream = connection.outputStream
        objectMapper.writeValue(outputStream, message)

        outputStream.flush()
    }

    private fun readFromInput() {
        while (!Thread.interrupted()) {
            try {
                val jsonNode = objectMapper.readTree(connection.inputStream)

                logger.info("Got data from input $jsonNode")

                if (jsonNode == null) {
                    logger.error("Interrupt thread.")
                    Thread.currentThread().interrupt()
                    continue
                }

                handleInputData(jsonNode)
            } catch (t: Throwable) {
                logger.error("Cannot handle message $t")
                Thread.currentThread().interrupt()
            }
        }
    }

    private fun handleInputData(node: JsonNode) {
        checkArgument(node.isArray, "Node needs to be an array")
        checkArgument(node.size() == 3 || node.size() == 4)

        val messageType = MessageType.valueOf(node.get(0).asInt(-1))

        logger.info("Handle message type $messageType")

        when (messageType) {
            MessageType.NOTIFICATION -> handleNotification(node)
            MessageType.REQUEST -> handleRequest(node)
            MessageType.RESPONSE -> handleResponse(node)
            else -> throw IllegalStateException("Invalid message type on $node")
        }
    }

    private fun handleNotification(node: JsonNode) {
        checkArgument(node.isArray, "Node needs to be an array")
        checkArgument(node.size() == 3, "Notification array should be size 3")

        val method = getText(node[1])
        val args = node[2]

        notificationHandlerFactory.getHandler(method).handle(args)
    }

    private fun handleRequest(node: JsonNode) {
        checkArgument(node.isArray, "Node needs to be an array")
        checkArgument(node.size() == 4, "Request array should be size 4")

        val requestId = node[1].asLong()
        val method: String = getText(node[2])
        val args = node[3]

        val result = requestHandlerFactory.getHandler(method).handle(args)

        try {
            send(Response(requestId, null, result))
        } catch (e: IOException) {
            logger.error("Error when response: $e")
        }
    }


    private fun handleResponse(node: JsonNode) {
        checkArgument(node.isArray, "Node needs to be an array")
        checkArgument(node.size() == 4, "Response array should be size 4")

        val requestId = node.get(1).asLong()
        logger.info("Handle request id $requestId")
        val callback: RequestCallback<*> = callbacks[requestId] ?: return
        val neovimException: Optional<NeovimException> = NeovimException.parseError(node.get(2))

        if (neovimException.isPresent){
            callback.setError(neovimException.get())
        } else {
            callback.setResult(objectMapper, node.get(3))
        }
    }

    override fun dispose() {
        connection.close()
        executorService.shutdown()

        try {
            receiverFuture?.get()
        } catch (e: InterruptedException) {
            executorService.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}
