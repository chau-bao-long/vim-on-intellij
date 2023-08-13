package com.longcb.vimonintellij.neovim

import java.io.IOException
import java.io.UncheckedIOException
import java.util.function.BiFunction

@FunctionalInterface
interface IOBiFunction<T, U, R> : BiFunction<T, U, R> {
    override fun apply(t: T, u: U): R {
        return try {
            call(t, u)
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    @Throws(IOException::class)
    fun call(t: T, u: U): R
}
