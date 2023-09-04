package com.longcb.vimonintellij.neovim

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class SocketConnection(private val host: String, private val port: Int) : Connection {
    private var socket: Socket

    init {
        socket = Socket(host, port)
    }

    override val inputStream: InputStream
        get() = socket.getInputStream()


    override val outputStream: OutputStream
        get() = socket.getOutputStream()

    override fun resetConnect() {
        if (!socket.isClosed) {
            socket.close()
        }

        socket = Socket(host, port)
    }

    override fun isConnected() = socket.isConnected

    override fun isClosed() = socket.isClosed

    override fun close() {
        outputStream.close()
        inputStream.close()
        socket.close()
    }
}

