package com.longcb.vimonintellij.neovim

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class SocketConnection(host: String, port: Int) : Connection {
    private val socket: Socket

    init {
        socket = Socket(host, port)
    }

    override val inputStream: InputStream
        get() = socket.getInputStream()


    override val outputStream: OutputStream
        get() = socket.getOutputStream()

    override fun close() {
        outputStream.close()
        inputStream.close()
        socket.close()
    }
}

