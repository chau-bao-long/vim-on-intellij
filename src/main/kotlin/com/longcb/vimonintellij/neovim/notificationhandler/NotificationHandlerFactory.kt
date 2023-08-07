package com.longcb.vimonintellij.neovim.notificationhandler

class NotificationHandlerFactory {
    private val notificationHandlers = listOf(
        BufEnterHandler(),
    )

    fun getHandler(method: String): INotificationHandler {
        return notificationHandlers.first { it.method == method }
    }
}
