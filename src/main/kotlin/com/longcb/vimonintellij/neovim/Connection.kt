package com.longcb.vimonintellij.neovim

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

interface Connection : Closeable {
    val inputStream: InputStream?
    val outputStream: OutputStream?

    fun resetConnect()

    fun isClosed(): Boolean

    fun isConnected(): Boolean
}

