package com.longcb.vimonintellij.neovim.requesthandler

class RequestHandlerFactory {
    private val requestHandlers = listOf(
        SyncCursorHandler(),
    )

    fun getHandler(method: String): IRequestHandler {
        return requestHandlers.first { it.method == method }
    }
}

