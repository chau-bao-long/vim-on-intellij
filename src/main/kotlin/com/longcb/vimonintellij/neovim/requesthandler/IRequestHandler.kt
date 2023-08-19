package com.longcb.vimonintellij.neovim.requesthandler

import com.fasterxml.jackson.databind.JsonNode

interface IRequestHandler {
    val method: String

    fun handle(data: JsonNode): Any?
}
