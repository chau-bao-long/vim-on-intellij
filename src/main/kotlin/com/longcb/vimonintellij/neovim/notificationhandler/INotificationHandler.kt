package com.longcb.vimonintellij.neovim.notificationhandler

import com.fasterxml.jackson.databind.JsonNode

interface INotificationHandler {
    val method: String

    fun handle(data: JsonNode)
}
