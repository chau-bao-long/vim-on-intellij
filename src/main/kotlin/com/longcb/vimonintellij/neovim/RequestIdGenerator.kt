package com.longcb.vimonintellij.neovim

import java.util.concurrent.atomic.AtomicLong

class RequestIdGenerator(
    private val id: AtomicLong = AtomicLong(0L),
) {

    fun nextId(): Long {
        return id.getAndIncrement()
    }
}

