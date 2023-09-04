package com.longcb.vimonintellij.neovim

import java.io.InputStream
import java.io.OutputStream
import java.net.ConnectException
import java.net.Socket

class SocketConnection(private val host: String, private val port: Int) : Connection {
    private var socket: Socket? = null

    init {
        socket = tryCreateSocket()
    }

    override val inputStream: InputStream?
        get() = socket?.getInputStream()


    override val outputStream: OutputStream?
        get() = socket?.getOutputStream()

    override fun resetConnect() {
        socket?.close()
        socket = tryCreateSocket()
    }

    override fun isConnected() = socket?.isConnected ?: false

    override fun isClosed() = socket?.isClosed ?: true

    override fun close() {
        outputStream?.close()
        inputStream?.close()
        socket?.close()
    }

    private fun tryCreateSocket(): Socket? {
        return try {
            Socket(host, port)
        } catch (e: ConnectException) {
            null
        }
    }
}

